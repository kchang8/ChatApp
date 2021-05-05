package com.bignerdranch.android.chatapp2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit_profile.*
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
        if (selectedPhotoUri == null) {
            Log.d("EditProfile", "No image detected. Starting updateUserInfo method...")
            updateUserInfo("")
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("EditProfile", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("EditProfile", "File location: $it")

                    updateUserInfo(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("EditProfile", "Failed to upload image")
            }
    }

    private fun updateUserInfo(profileImageUrl: String) {
        val uid = auth.uid.toString()
        val username = editProfile_username.text.toString()
        val updates = hashMapOf<String, Any>()
        val user = auth.currentUser

        if (username != ""){
            updates["/users/$uid/username"] = username
        }
        if (profileImageUrl != ""){
            updates["/users/$uid/profileImageUrl"] = profileImageUrl
        }

        db.reference.updateChildren(updates).addOnSuccessListener {
            Toast.makeText(this, "Successfully saved data", Toast.LENGTH_LONG).show()

            finish()
        }.addOnFailureListener {
            Toast.makeText(this@EditProfileActivity, "Failed to save data: ${it.message}", Toast.LENGTH_LONG).show()
        }

    }

}