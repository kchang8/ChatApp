package com.bignerdranch.android.chatapp2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    var selectedPhotoUri: Uri? = null

    lateinit var db: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        // check if the back button is pressed
        editProfile_backButton.setOnClickListener {
            finish()
        }

        // checks if the profile image icon is clicked
        editProfile_profileImage.setOnClickListener {
            Log.d("EditProfile", "Trying to change picture")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        // checks if the save changes button is clicked
        editProfile_saveButton.setOnClickListener {
            Log.d("EditProfile", "Save button is clicked")

            uploadImageToFirebaseStorage()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("EditProfile", "new profile image is selected")

            selectedPhotoUri = data.data
            Log.d("EditProfile", selectedPhotoUri.toString())

            editProfile_profileImage.setImageURI(selectedPhotoUri)
        }

    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("EditProfile", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("EditProfile", "File location: $it")

                    saveUserInfoToDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("EditProfile", "Failed to upload image")
            }
    }

    private fun saveUserInfoToDatabase(profileImageUrl: String) {
        val uid = auth.uid
        val username = editProfile_username.text.toString()
        val email = editProfile_email.text.toString()

        if(username.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please enter a new username or email", Toast.LENGTH_SHORT).show()
        }
        else if (username.isNotEmpty() && email.isNotEmpty()) {
            try {
                val key = db.reference.child("users").push().key
                if (key == null) {
                    Log.w("EditProfile", "Couldn't get push key for users")
                    return
                }

                val newUserInfo = Users(auth.uid!!, username, email, profileImageUrl)
                val newUserInfoValues = newUserInfo.toMap()

                val childUpdates = hashMapOf<String, Any>(
                    "/users/$key" to newUserInfoValues
                )

                db.reference.updateChildren(childUpdates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully saved data", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed to save data: ${it.message}", Toast.LENGTH_LONG).show()
                    }


            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}