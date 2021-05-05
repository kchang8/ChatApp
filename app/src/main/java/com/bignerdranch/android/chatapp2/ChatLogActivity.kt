package com.bignerdranch.android.chatapp2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.bignerdranch.android.chatapp2.modelClasses.Message
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_chat_log.view.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.util.*


class ChatLogActivity : AppCompatActivity() {

    val db = FirebaseDatabase.getInstance()
    val auth = FirebaseAuth.getInstance()

    val storage = FirebaseStorage.getInstance()

    var toUser: Users? = null

    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        //checks if the back button is clicked
        chatLog_backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        toUser = intent.getParcelableExtra(NewMessageActivity.USER_KEY)
        Log.d("ChatLogActivity", toUser.toString())


        // checks if the image icon is clicked
        image_button.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        // makes the chat log text view the user name of the person you are chatting with
        chatLog_TextView.text = toUser!!.username

        val adapter = GroupAdapter<ViewHolder>()

        //retrieve messages from userMessages in realtime database
        val messageRef = db.reference.child("user-messages").child(auth.uid.toString()).child(toUser!!.uid)
        messageRef.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)

                if (message?.getFromUid() == auth.uid.toString()){
                    val currentUser = HomeActivity.currentUser
                    adapter.add(ChatToItem(message.getText(), currentUser!!, message.getType()))
                    Log.d("ChatLog", message.getType())
                }
                else {
                    adapter.add(ChatFromItem(message!!.getText(), toUser!!, message.getType()))
                    Log.d("ChatLog", message.getType())
                }

                chatLog_recyclerView.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        val button = findViewById<Button>(R.id.send_button_chat_log)
        button.setOnClickListener(){


            val message = findViewById<EditText>(R.id.edittext_chat_log).text.toString()

            val type = "text"

            sendMessage(message, auth.uid.toString(), toUser!!.uid, type)

            edittext_chat_log.text.clear()

        }

        val imageButton = findViewById<ImageButton>(R.id.image_button)
        imageButton.setOnClickListener(){
            Log.d(TAG, "Showing photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)

        }

        chatLog_recyclerView.adapter = adapter


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("ChatLogActivity", "Photo was selected")

            selectedPhotoUri = data.data
            Log.d("ChatLogActivity", selectedPhotoUri.toString())

            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null){
            Log.d("ChatLogActivity", "selecetedPhotoUri was null")
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = storage.getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("ChatLogActivity", "Successfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("ChatLogActivity", "File location: $it")

                        val type = "image"

                        val message = it.toString()

                        sendMessage(message, auth.uid.toString(), toUser!!.uid, type)
                    }
                }
                .addOnFailureListener {
                    Log.d("ChatLogActivity", "Failed to upload image")
                }
    }

    private fun sendMessage(text: String, fromUid: String, toUid:String, type: String){
        //send message
        var messageRef = db.reference.child("user-messages")
                .child(fromUid)
                .child(toUid)
                .push()
        var message = Message(messageRef.key.toString(), text, fromUid, toUid, type)

        messageRef.setValue(message)

        //reflect message for targeted user
        messageRef = db.reference.child("user-messages")
                .child(toUid)
                .child(fromUid)
                .push()
        message = Message(messageRef.key.toString(), text, fromUid, toUid, type)
        messageRef.setValue(message)


        // showing latest messages refs
        val latestMessageFromRef = db.reference.child("latest-messages")
                .child(fromUid)
                .child(toUid)
        latestMessageFromRef.setValue(message)

        val latestMessageToRef = db.reference.child("latest-messages")
                .child(toUid)
                .child(fromUid)
        latestMessageToRef.setValue(message)
    }
}

class ChatFromItem(message: String, user: Users?, type: String) : Item<ViewHolder>() {

    private val msg = message
    private val uri = user!!.profileImageUrl
    private val type = type

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val fromRowTargetImage = viewHolder.itemView.chatLog_profileFromRowImageView

        if(type == "text"){

            viewHolder.itemView.chatLog_messageFromRowTextView.visibility = View.VISIBLE
            viewHolder.itemView.chatLog_imageFromRowTextView.visibility = View.GONE

            viewHolder.itemView.chatLog_messageFromRowTextView.text = msg

        }
        else{

            viewHolder.itemView.chatLog_messageFromRowTextView.visibility = View.GONE
            viewHolder.itemView.chatLog_imageFromRowTextView.visibility = View.VISIBLE

            Picasso.get().load(msg).placeholder(R.drawable.ic_image_black).into(viewHolder.itemView.chatLog_imageFromRowTextView)


        }


        Picasso.get().load(uri).into(fromRowTargetImage)

    }

    override fun getLayout(): Int {

        return R.layout.chat_from_row
    }
}

class ChatToItem(message: String, user: Users?, type: String): Item<ViewHolder>() {

    private val msg = message
    private val uri = user!!.profileImageUrl
    private val type = type

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val toRowTargetImage = viewHolder.itemView.chatLog_profileToRowImageView

        if(type == "text"){

            viewHolder.itemView.chatLog_messageToRowTextView.visibility = View.VISIBLE
            viewHolder.itemView.chatLog_imageToRowTextView.visibility = View.GONE

            viewHolder.itemView.chatLog_messageToRowTextView.text = msg

        }

        else{

            viewHolder.itemView.chatLog_messageToRowTextView.visibility = View.GONE
            viewHolder.itemView.chatLog_imageToRowTextView.visibility = View.VISIBLE

            Picasso.get().load(msg).placeholder(R.drawable.ic_image_black).into(viewHolder.itemView.chatLog_imageToRowTextView)
        }


        Picasso.get().load(uri).into(toRowTargetImage)

    }

    override fun getLayout(): Int {

        return R.layout.chat_to_row
    }
}

