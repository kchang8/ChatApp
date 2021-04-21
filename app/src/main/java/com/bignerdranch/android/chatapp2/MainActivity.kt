package com.bignerdranch.android.chatapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button.setOnClickListener {
            val email = register_email.text.toString()
            val password = register_password.text.toString()

            Log.d(TAG, "Email is: $email")
            Log.d(TAG, "Password is: $password")

            // Firebase authentication
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    //else if successful
                    Log.d("Main", "Successfully created user with uid: ${it.result?.user?.uid}")
                }
        }

        register_alreadyHaveAccount.setOnClickListener {
            Log.d(TAG, "Showing log in activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}