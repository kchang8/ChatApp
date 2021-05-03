package com.bignerdranch.android.chatapp2.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.chatapp2.NewMessageActivity
import com.bignerdranch.android.chatapp2.R
import com.bignerdranch.android.chatapp2.modelClasses.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_list_item.view.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*

class ChatFragment : Fragment() {

    private var recyclerView: RecyclerView? = null

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        view.chat_newMessageImageView.setOnClickListener {
            val intent = Intent(activity, NewMessageActivity::class.java)
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.chat_recyclerView)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)


        recyclerView!!.adapter = adapter

        listenForLatestMessages()

        return view
    }

    val latestMessagesMap = HashMap<String, Message>()

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(ChatMessageRow(it))
        }
    }

    private fun listenForLatestMessages() {
        val fromUid = auth.uid
        val ref = db.getReference("/latest-messages/$fromUid")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(Message::class.java) ?: return

                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(Message::class.java) ?: return

                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

        })
    }


}

class ChatMessageRow(val chatMessage: Message): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        // ISSUE HERE
        //viewHolder.itemView.chat_latestMessageTextView.text = chatMessage.text
    }

    override fun getLayout(): Int {
        return R.layout.chat_list_item
    }
}