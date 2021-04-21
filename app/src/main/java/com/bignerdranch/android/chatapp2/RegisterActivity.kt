package com.bignerdranch.android.chatapp2

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "RegisterActivity"

class RegisterActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth

    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // checks when the register button is clicked and registers user
        register_button.setOnClickListener {
            createUser()

        }

        // checks if the 'already have account' label is clicked and takes them to login activity
        register_alreadyHaveAccount.setOnClickListener {
            Log.d(TAG, "Showing login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // checks if the select photo button is clicked
        register_photoButton.setOnClickListener {
            Log.d(TAG, "Showing photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }

    private fun createUser() {
        val email = register_email.text.toString()
        val password = register_password.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter an email or password", Toast.LENGTH_SHORT).show()
        }
        else if (email.isNotEmpty() && password.isNotEmpty()) {
            try {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (!it.isSuccessful) return@addOnCompleteListener

                            // if successful
                            Log.d(TAG, "Successfully created user with uid: ${it.result?.user?.uid}")
                            Toast.makeText(this, "Successfully registered", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to register: ${it.message}", Toast.LENGTH_LONG).show()
                        }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            val bitmapDrawable = BitmapDrawable(bitmap)
            register_photoButton.setBackgroundDrawable(bitmapDrawable)
        }
    }

}