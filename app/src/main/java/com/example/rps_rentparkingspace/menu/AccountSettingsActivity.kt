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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


private val TAG = "AccountSettingsActivity"

private var mProgressBar: ProgressDialog? = null

private var profilePhoto: String? = null

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


@Suppress("DEPRECATION")
class AccountSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acount_settings)

        val editIMG = findViewById<View>(R.id.editImg) as TextView
        val ivBack = findViewById<View>(R.id.goBack) as ImageView
        val tvSave = findViewById<View>(R.id.save) as TextView
        val imageUser = findViewById<View>(R.id.imgUserLogin) as ImageView

        val setName = findViewById<View>(R.id.firstName) as TextView
        nameUser = etNameUser?.text.toString()

        val setLastName = findViewById<View>(R.id.lastName) as TextView
        lastNameUser = tvLastName?.text.toString()

        val setEmail = findViewById<View>(R.id.emailUser) as TextView
        emailUser = tvEmail?.text.toString()

        val setBalance = findViewById<View>(R.id.addBalanceUser) as TextView
        balanceUser = tvBalance?.text.toString()

        val setPassword = findViewById<View>(R.id.passwordUser) as TextView
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
                profilePhoto = """${dataSnapshot.child("profilephoto").value}"""

                Picasso.get().load(profilePhoto).into(imageUser)

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
            val imageUser = findViewById<View>(R.id.imgUserLogin) as ImageView
            imageUri = data.data
            imageUser.setImageURI(imageUri)
        }
    }

    private fun uploadFile() {
        if (imageUri != null) {
            mProgressBar?.setTitle("Uploading")
            mProgressBar?.show()
            //StorageFire
            val filepath: StorageReference = FirebaseStorage.getInstance().reference.child("images/${imageUri.toString()}.jpg")
            filepath.putFile(imageUri!!).addOnSuccessListener { it ->
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                filepath.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")

                    savePhotoToFirebaseDatabase(it.toString())
                    mProgressBar?.dismiss()
                    Toast.makeText(this, "File Success Uploaded", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
                mProgressBar?.dismiss()
                Toast.makeText(this, "Filed to Upload", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePhotoToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child("Users").child(uid)

        uidRef.child("profilephoto").setValue(profileImageUrl)
    }
}

