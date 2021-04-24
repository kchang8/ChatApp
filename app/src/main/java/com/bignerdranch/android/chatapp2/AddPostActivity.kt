package com.bignerdranch.android.chatapp2

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import kotlin.collections.HashMap

const val ADD_POST_TAG = "AddPostActivity"

class AddPostActivity : AppCompatActivity() {
    lateinit var storage: FirebaseStorage
    lateinit var db: FirebaseDatabase
    lateinit var auth: FirebaseAuth

    private var myUrl = ""

    var storagePostPicRef: StorageReference? = null
    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storage = FirebaseStorage.getInstance()
        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        storagePostPicRef = storage.reference.child("Post Pictures")

        addPost_submitButton.setOnClickListener {
            uploadImage()
        }

        addPost_closeButton.setOnClickListener {
            finish()
        }

        addPost_imagePost.setOnClickListener {
            Log.d(TAG, "Trying to post picture")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        CropImage.activity()
//            .setAspectRatio(1, 1)
//            .start(this@AddPostActivity)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(ADD_POST_TAG, "Photo was selected to be added to post")

            selectedPhotoUri = data.data
//            val result = CropImage.getActivityResult(data)
//            selectedPhotoUri = result.uri
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
//
//            val bitmapDrawable = BitmapDrawable(bitmap)
            addPost_imagePost.setImageURI(selectedPhotoUri)
        }


    }

    private fun uploadImage() {
        when{
            selectedPhotoUri == null -> Toast.makeText(this, "Please select an image", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(addPost_description.text.toString()) ->
                Toast.makeText(this, "Please write a description of your photo", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Adding Post")
                progressDialog.setMessage("Adding your post, please wait...")
                progressDialog.show()

                val fileRef = storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(selectedPhotoUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener (OnCompleteListener<Uri> {task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = db.reference.child("Posts")
                        val postId = ref.push().key

                        val userMap = HashMap<String, Any>()
                        userMap["postid"] = postId!!
                        userMap["description"] = addPost_description.text.toString().toLowerCase()
                        userMap["uid"] = auth.currentUser!!.uid
                        userMap["postImage"] = myUrl

                        ref.child(postId).updateChildren(userMap)

                        Toast.makeText(this, "Post uploaded successfully", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@AddPostActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }
                }).addOnFailureListener() {
                    Toast.makeText(this, "Failed to register: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

}