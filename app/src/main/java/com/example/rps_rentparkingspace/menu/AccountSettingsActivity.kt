@file:Suppress("DEPRECATION")

package com.example.rps_rentparkingspace.menu

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rps_rentparkingspace.MainActivity
import com.example.rps_rentparkingspace.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.lang.System.currentTimeMillis


private val TAG = "AccountSettingsActivity"

private var mProgressBar: ProgressDialog? = null

//Firebase references
private var mDatabaseReference: DatabaseReference? = null
private var mDatabase: FirebaseDatabase? = null
private var mAuth: FirebaseAuth? = null

private var etNameUser: TextView? = null
private var nameUser: String? = null

private var tvLastName: TextView? = null
private var lastNameUser: String? = null

private var tvEmail: TextView? = null
private var emailUser: String? = null

private var tvBalance: TextView? = null
private var balanceUser: String? = null

private var tvPassword: TextView? = null
private var passwordUser: String? = null

private const val RequestCode = 438
private var imageUri: Uri? = null
private var coverChecker: String? = null
private var storageRef: StorageReference? = null

@Suppress("DEPRECATION")
class AccountSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acount_settings)

        var editIMG = findViewById<View>(R.id.editImg) as TextView
        var ivBack = findViewById<View>(R.id.goBack) as ImageView
        var tvSave = findViewById<View>(R.id.save) as TextView

        var setName = findViewById<View>(R.id.firstName) as TextView
        nameUser = etNameUser?.text.toString()

        var setLastName = findViewById<View>(R.id.lastName) as TextView
        lastNameUser = tvLastName?.text.toString()

        var setEmail = findViewById<View>(R.id.emailUser) as TextView
        emailUser = tvEmail?.text.toString()

        var setBalance = findViewById<View>(R.id.addBalanceUser) as TextView
        balanceUser = tvBalance?.text.toString()

        var setPassword = findViewById<View>(R.id.passwordUser) as TextView
        passwordUser = tvPassword?.text.toString()

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child("Users").child(uid)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                nameUser = """${dataSnapshot.child("firstName").value}"""
                lastNameUser = """${dataSnapshot.child("lastName").value}"""
                emailUser = """${dataSnapshot.child("email").value}"""
                balanceUser = """$ ${dataSnapshot.child("balance").value}"""
                passwordUser = """${dataSnapshot.child("password").value}"""

                setName.text = nameUser
                setLastName.text = lastNameUser
                setEmail.text = emailUser
                setBalance.text = balanceUser
                setPassword.text = passwordUser
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        }
        uidRef.addListenerForSingleValueEvent(valueEventListener)

        ivBack
                .setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }

        editIMG
                .setOnClickListener { pickImage() }

        tvSave
                .setOnClickListener { uploadFile() }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode && resultCode == RESULT_OK && data!!.data != null) {
            var imageUser = findViewById<View>(R.id.imgUser) as ImageView
            imageUri = data.data
            imageUser.setImageURI(imageUri)
        }
    }

    private fun uploadFile() {
        if (imageUri != null) {
            //StorageFire
            mProgressBar?.setTitle("Uploading")
            mProgressBar?.show()
            val filepath: StorageReference = FirebaseStorage.getInstance().reference.child("images/${imageUri.toString()}.jpg")
            filepath.putFile(imageUri!!).addOnSuccessListener() { task ->
                val downloadUrl = task.uploadSessionUri
                val url = downloadUrl.toString()
                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val rootRef = FirebaseDatabase.getInstance().reference
                val uidRef = rootRef.child("Users").child(uid)
                if (coverChecker == "cover") {
                    val mapCoverImg = HashMap<String, Any>()
                    mapCoverImg["cover"] = url
                    uidRef.child("profilePhoto").setValue(mapCoverImg)
                    coverChecker = ""
                } else {
                    val mapCoverImg = HashMap<String, Any>()
                    mapCoverImg["profile"] = url
                    uidRef.child("profilePhoto").setValue(mapCoverImg)
                    coverChecker = ""
                }
                mProgressBar?.dismiss()
                Toast.makeText(this, "File Uploaded", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener() { pO ->
                mProgressBar?.dismiss()
                Toast.makeText(applicationContext, pO.message, Toast.LENGTH_SHORT).show()
            }.addOnProgressListener { pO ->
                var progress: Double = (100.0 * pO.bytesTransferred)
                mProgressBar?.setMessage("Uploaded ${progress.toInt()}%")
            }

        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }
}

