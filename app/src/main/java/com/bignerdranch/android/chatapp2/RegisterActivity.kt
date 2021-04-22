package com.bignerdranch.android.chatapp2

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

const val TAG = "RegisterActivity"

class RegisterActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage
    lateinit var db: FirebaseDatabase

    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        db = FirebaseDatabase.getInstance()

        // checks when the register button is clicked and registers user
        register_button.setOnClickListener {
            createUser()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            register_selectPhoto_circleView.setImageBitmap(bitmap)

            register_photoButton.alpha = 0f

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            register_photoButton.setBackgroundDrawable(bitmapDrawable)
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

                            uploadImageToFirebaseStorage()

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

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("Register", "Successfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d(TAG, "File location: $it")

                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to upload image")
                }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = auth.uid ?: ""
        val ref = db.getReference("/users/$uid")

        val user = User(uid, register_username.text.toString(), profileImageUrl)

        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved the user to firebase database")
                }
                .addOnFailureListener {
                    Log.d(TAG, "Failed to save user to firebase db: ${it.message}")
                }
    }

}

class User(val uid: String, val username: String, val profileImageUrl: String)