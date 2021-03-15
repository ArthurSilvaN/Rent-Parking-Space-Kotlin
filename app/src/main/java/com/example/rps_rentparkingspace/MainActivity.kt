package com.example.rps_rentparkingspace

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


private val TAG = "MainActivity"

private var etFirstName: TextView? = null

private var firstName: String? = null

class MainActivity : AppCompatActivity() {
    private var textName: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialise();
    }

    private fun initialise() {
        etFirstName = findViewById<View>(R.id.nameUser) as TextView
        var setName = findViewById<View>(R.id.nameUser) as TextView
        firstName = etFirstName?.text.toString()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child("Users").child(uid)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                firstName = """${dataSnapshot.child("firstName").getValue()}  ${dataSnapshot.child("lastName").getValue()}"""
                setName.text = firstName
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        }
        uidRef.addListenerForSingleValueEvent(valueEventListener)
    }
}
