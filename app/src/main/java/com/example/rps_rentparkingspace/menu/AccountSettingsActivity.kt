@file:Suppress("DEPRECATION")

package com.example.rps_rentparkingspace.menu

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.rps_rentparkingspace.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

private val TAG = "AccountSettingsActivity"

private var mProgressBar: ProgressDialog? = null


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

private var uploadImage: ImageView? = null
private const val RequestCode = 438
private var imageUri: Uri? = null
private var storageRef: StorageReference? = null


@Suppress("DEPRECATION")
class AccountSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acount_settings)

        var editIMG = findViewById<View>(R.id.editImg) as ImageView

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
                nameUser = """${dataSnapshot.child("firstName").getValue()}"""
                lastNameUser = """${dataSnapshot.child("lastName").getValue()}"""
                emailUser = """${dataSnapshot.child("email").getValue()}"""
                balanceUser = """$ ${dataSnapshot.child("balance").getValue()}"""
                passwordUser = """${dataSnapshot.child("password").getValue()}"""

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

        editIMG!!
            .setOnClickListener { pickImage() }
    }

    private fun pickImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RequestCode && resultCode == RESULT_OK && data!!.data != null){
            var imageUser = findViewById<View>(R.id.imgUser) as ImageView
            imageUri = data.data

            imageUser.setImageURI(imageUri)
        }
    }

    private fun uploadToDataBase(){
        mProgressBar = ProgressDialog(this)
        mProgressBar!!.setMessage("image is uploading, please wait...")

        if (imageUri!=null){
        }
    }
}