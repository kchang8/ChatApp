package com.bignerdranch.android.chatapp2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.bignerdranch.android.chatapp2.modelClasses.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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

class ChatLogActivity : AppCompatActivity() {
    var selectedPhotoUri: Uri? = null

    val db = FirebaseDatabase.getInstance()
    val auth = FirebaseAuth.getInstance()

    var toUser: Users? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        //checks if the back button is clicked
        chatLog_backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        // checks if the image icon is clicked
        chatLog_addImageIcon.setOnClickListener {
            Log.d("ChatLog", "Showing photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        toUser = intent.getParcelableExtra(NewMessageActivity.USER_KEY)

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
                    adapter.add(ChatToItem(message.getText(), currentUser!!))

                }
                else {
                    adapter.add(ChatFromItem(message!!.getText(), toUser!!))

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

            sendMessage(message, auth.uid.toString(), toUser!!.uid)

            edittext_chat_log.text.clear()

        }

        chatLog_recyclerView.adapter = adapter
    }

    private fun sendMessage(text: String, fromUid: String, toUid:String){
        //send message
        var messageRef = db.reference.child("user-messages")
            .child(fromUid)
            .child(toUid)
            .push()
        var message = Message(messageRef.key.toString(), text, fromUid, toUid)
        messageRef.setValue(message)

        //reflect message for targeted user
        messageRef = db.reference.child("user-messages")
            .child(toUid)
            .child(fromUid)
            .push()
        message = Message(messageRef.key.toString(), text, fromUid, toUid)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(ADD_POST_TAG, "Photo was selected to be sent")

            selectedPhotoUri = data.data

            Log.d("ChatLog", selectedPhotoUri.toString())

            chatLog_addImageIcon.setImageURI(selectedPhotoUri)

        }
    }
}

class ChatFromItem(message: String, user: Users?) : Item<ViewHolder>() {

    private val msg = message
    private val uri = user!!.profileImageUrl

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val fromRowTargetImage = viewHolder.itemView.chatLog_profileFromRowImageView

        viewHolder.itemView.chatLog_messageFromRowTextView.text = msg

        Picasso.get().load(uri).into(fromRowTargetImage)

    }

    override fun getLayout(): Int {

        return R.layout.chat_from_row
    }
}

class ChatToItem(message: String, user: Users?): Item<ViewHolder>() {

    private val msg = message
    private val uri = user!!.profileImageUrl

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val toRowTargetImage = viewHolder.itemView.chatLog_profileToRowImageView

        viewHolder.itemView.chatLog_messageToRowTextView.text = msg

        Picasso.get().load(uri).into(toRowTargetImage)

    }

    override fun getLayout(): Int {

        return R.layout.chat_to_row
    }
}

