package com.bignerdranch.android.chatapp2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        login_button.setOnClickListener {
            //Log.d("Login", "Attempt login with email/pw: $email/***")
            login()
        }

        login_backToRegistration.setOnClickListener {
            finish()
        }
    }

    private fun login() {
        val email = login_email.text.toString()
        val password = login_password.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter an email or password", Toast.LENGTH_SHORT).show()
        }
        else if (email.isNotEmpty() && password.isNotEmpty()) {
            try {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (!it.isSuccessful) return@addOnCompleteListener

                            // if successful
                            // Log.d(TAG, "Login successfully")
                            Toast.makeText(this, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to login: ${it.message}", Toast.LENGTH_LONG).show()
                        }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}