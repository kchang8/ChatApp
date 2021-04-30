package com.bignerdranch.android.chatapp2.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bignerdranch.android.chatapp2.R
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.bignerdranch.android.chatapp2.modelClasses.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_chat.view.*

class ChatFragment : Fragment() {


    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)


        //testMessage() will lay foundation for sending messages, but still need a listenForMessages()
        view.chats.setOnClickListener(){
            testMessage()
        }



        return view
    }

    private fun testMessage(){
        //sends message from peter-parker to pussy slayer
        var messageRef = db.reference.child("user-messages")
                .child(auth.uid.toString())
                .child("KuJUGS0hAqPBDHjYYaWDxavu2613")
                .push()
        val message = Message(messageRef.key.toString(), "kys pls", auth.uid.toString(), "KuJUGS0hAqPBDHjYYaWDxavu2613")
        messageRef.setValue(message)
        Log.d("test_message", "text message was sent to pussy slayer!")

        //need to do reverse for pussy slayer
        messageRef = db.reference.child("user-messages")
                .child("KuJUGS0hAqPBDHjYYaWDxavu2613")
                .child(auth.uid.toString())
                .push()
        messageRef.setValue(message)
        Log.d("test_message", "pussy slayer received message!")


    }

}