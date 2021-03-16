package com.example.rps_rentparkingspace

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.rps_rentparkingspace.menu.AccountSettingsActivity
import com.example.rps_rentparkingspace.menu.AddBalanceActivity
import com.example.rps_rentparkingspace.menu.RpsActivity
import com.example.rps_rentparkingspace.menu.SpacesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


private val TAG = "MainActivity"

private var etNameUser: TextView? = null
private var nameUser: String? = null

private var tvRpsScrenn: TextView? = null
private var tvAddBalanceScreen: TextView? = null
private var tvAccountSetings: TextView? = null
private var tvSpacesScreen: TextView? = null
private var tvLogOut: TextView? = null

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialise();
    }

    private fun initialise() {
        etNameUser = findViewById<View>(R.id.nameUser) as TextView
        tvRpsScrenn = findViewById<View>(R.id.rpsScreen) as TextView
        tvAccountSetings = findViewById<View>(R.id.accountSettingsScreen) as TextView
        tvAddBalanceScreen = findViewById<View>(R.id.addBalanceScreen) as TextView
        tvLogOut = findViewById<View>(R.id.logOut) as TextView
        tvSpacesScreen = findViewById<View>(R.id.spacesScreen) as TextView


        var setName = findViewById<View>(R.id.nameUser) as TextView
        nameUser = etNameUser?.text.toString()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        val uidRef = rootRef.child("Users").child(uid)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                nameUser = """${
                    dataSnapshot.child("firstName").getValue()
                }  ${dataSnapshot.child("lastName").getValue()}"""
                setName.text = nameUser
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, databaseError.message)
            }
        }
        uidRef.addListenerForSingleValueEvent(valueEventListener)

        tvRpsScrenn!!
            .setOnClickListener { startActivity(Intent(this, RpsActivity::class.java)) }
        tvAddBalanceScreen!!
            .setOnClickListener { startActivity(Intent(this, AddBalanceActivity::class.java)) }
        tvSpacesScreen!!
            .setOnClickListener { startActivity(Intent(this, SpacesActivity::class.java)) }
        tvAccountSetings!!
            .setOnClickListener { startActivity(Intent(this, AccountSettingsActivity::class.java)) }
        tvLogOut!!
            .setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }
    }
}
