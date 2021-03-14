package com.example.rps_rentparkingspace.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import com.example.rps_rentparkingspace.R


@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        changeToLogin()
        }

    private fun changeToLogin() {
        val intent = Intent( this, LoginActivity::class.java);
        Handler().postDelayed( {
            intent.change()
        },  5000)
    }
    fun Intent.change() {
        startActivity(this)
        finish()
    }
}