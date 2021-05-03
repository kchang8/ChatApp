package com.bignerdranch.android.chatapp2.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.chatapp2.ChatLogActivity
import com.bignerdranch.android.chatapp2.NewMessageActivity
import com.bignerdranch.android.chatapp2.NewMessageActivity.Companion.USER_KEY
import com.bignerdranch.android.chatapp2.R
import com.bignerdranch.android.chatapp2.modelClasses.Message
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
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
        recyclerView!!.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

        //set item click listener on adapter
        adapter.setOnItemClickListener { item, view ->
            Log.d("ChatFragment", "123")
            val intent = Intent(this.context, ChatLogActivity::class.java)

            //gets the chat partner you are talking to when you press on their adapter view
            val row = item as ChatMessageRow

            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

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

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase

    var chatPartnerUser: Users? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        viewHolder.itemView.chat_latestMessageTextView.text = chatMessage.getText()

        val chatPartnerId: String
        if (chatMessage.getFromUid() == auth.uid) {
            chatPartnerId = chatMessage.getToUid()
        } else {
            chatPartnerId = chatMessage.getFromUid()
        }

        val ref = db.getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(Users::class.java)
                val targetImageView = viewHolder.itemView.chat_profileImageView

                viewHolder.itemView.chat_usernameTextView.text = chatPartnerUser?.username

                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
            }

        })

    }

    override fun getLayout(): Int {
        return R.layout.chat_list_item
    }
}