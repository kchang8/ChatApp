package com.bignerdranch.android.chatapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.new_message_user_item.view.*

class NewMessageActivity : AppCompatActivity() {

    lateinit var db: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var fireUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

//        val adapter = GroupAdapter<ViewHolder>()
//
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//
//        newMessage_recyclerView.adapter = adapter
        newMessage_recyclerView.layoutManager = LinearLayoutManager(this)

        fetchUsers()

        newMessage_backButton.setOnClickListener {
            finish()
        }

    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers() {
        val ref = db.reference.child("users").child(auth.uid.toString()).child("friendList")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    if (it.value == true){
                        val userRef = db.reference.child("users").child(it.key.toString()).get().addOnSuccessListener {
                            val user = it.getValue(Users::class.java)
                            Log.d("NewMessage", user.toString())
                            if (user != null){
                                adapter.add(UserItem(user))
                            }
                        }
                    }
                }

                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
//                    intent.putExtra(USER_KEY, userItem.user.getUsername())
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish()
                }

                newMessage_recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

class UserItem(val user: Users): Item<ViewHolder>() {
    private var refUsers: DatabaseReference? = null

    lateinit var db: FirebaseDatabase

    override fun bind(viewHolder: ViewHolder, position: Int) {
        db = FirebaseDatabase.getInstance()

        // get user's usernames to display
        viewHolder.itemView.newMessage_username.text = user.username

        // get user's profile pics to display
        refUsers = db.reference.child("users").child(user.uid!!)
        refUsers!!.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    val imageUrl = snapshot.child("profileImageUrl").getValue().toString()

                    Picasso.get().load(imageUrl).into(viewHolder.itemView.newMessage_profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.new_message_user_item
    }
}

