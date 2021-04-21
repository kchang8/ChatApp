package com.bignerdranch.android.chatapp2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            val email = login_email.text.toString()
            //val password = login_password.text.toString()

            Log.d("Login", "Attempt login with email/pw: $email/***")
        }

        login_backToRegistration.setOnClickListener {
            finish()
        }
    }
}