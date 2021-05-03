package com.bignerdranch.android.chatapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.bignerdranch.android.chatapp2.modelClasses.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_chat_log.view.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    val db = FirebaseDatabase.getInstance()
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        chatLog_backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

//        val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
//        if (username != null) {
//            Log.d("Chat Log Username", username)
//        }

        val user = intent.getParcelableExtra<Users>(NewMessageActivity.USER_KEY)

        // makes the chat log text view the user name of the person you are chatting with
        chatLog_TextView.text = user!!.username

        val adapter = GroupAdapter<ViewHolder>()

        //retrieve messages from userMessages in realtime database
        val messageRef = db.reference.child("user-messages").child(auth.uid.toString()).child(user.uid)
        messageRef.addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)

                if (message?.getFromUid() == auth.uid.toString()){
                    adapter.add(ChatToItem(message.getText()))
                }
                else {
                    adapter.add(ChatFromItem(message?.getText().toString()))
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        val button = findViewById<Button>(R.id.send_button_chat_log)
        button.setOnClickListener(){
            val message = findViewById<EditText>(R.id.edittext_chat_log).text.toString()
            sendMessage(message, auth.uid.toString(), user.uid)
        }

        chatLog_recyclerView.adapter = adapter
    }

    private fun sendMessage(text: String, fromUid: String, toUid:String){
        //send message
        var messageRef = db.reference.child("user-messages")
            .child(fromUid)
            .child(toUid)
            .push()
        val message = Message(messageRef.key.toString(), text, fromUid, toUid)
        messageRef.setValue(message)

        //reflect message for targeted user
        messageRef = db.reference.child("user-messages")
            .child(toUid)
            .child(fromUid)
            .push()
        messageRef.setValue(message)

    }

    private fun testMessage1(){
        //sends message from peter-parker to pussy slayer
        var messageRef = db.reference.child("user-messages")
            .child(auth.uid.toString())
            .child("KuJUGS0hAqPBDHjYYaWDxavu2613")
            .push()
        val message = Message(messageRef.key.toString(), "kys pls", auth.uid.toString(), "KuJUGS0hAqPBDHjYYaWDxavu2613")
        messageRef.setValue(message)

        //need to do reverse for pussy slayer
        messageRef = db.reference.child("user-messages")
            .child("KuJUGS0hAqPBDHjYYaWDxavu2613")
            .child(auth.uid.toString())
            .push()
        messageRef.setValue(message)
    }
}

class ChatFromItem(message: String) : Item<ViewHolder>() {

    private val msg = message

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.chatLog_messageFromRowTextView.text = msg

    }

    override fun getLayout(): Int {

        return R.layout.chat_from_row
    }
}

class ChatToItem(message: String): Item<ViewHolder>() {

    private val msg = message

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.chatLog_messageToRowTextView.text = msg

    }

    override fun getLayout(): Int {

        return R.layout.chat_to_row
    }
}
