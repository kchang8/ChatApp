package com.bignerdranch.android.chatapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_friend_list.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.feed_list_item.view.*
import kotlinx.android.synthetic.main.friend_list_item.view.*
import kotlinx.android.synthetic.main.new_message_user_item.view.*

class FriendListActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        // checks if the back button is pressed
        friendList_backButton.setOnClickListener {
            finish()
        }

        fetchFriends()
    }

    private fun fetchFriends() {
        val ref = db.reference.child("users").child(auth.uid.toString()).child("friendList")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                adapter.clear()

                p0.children.forEach {
                    Log.d("FriendList", it.toString())
                    if (it.value == true){
                        val userRef = db.reference.child("users").child(it.key.toString()).get().addOnSuccessListener {
                            val user = it.getValue(Users::class.java)
                            Log.d("FriendList", user.toString())
                            if (user != null){
                                adapter.add(FriendListRow(user))
                            }
                        }
                    }
                }

                friendList_recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}

class FriendListRow(val user: Users): Item<ViewHolder>() {
    private var refUsers: DatabaseReference? = null

    lateinit var db: FirebaseDatabase
    lateinit var auth: FirebaseAuth

    override fun bind(viewHolder: ViewHolder, position: Int) {
        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        // get user's usernames to display
        viewHolder.itemView.friendList_username.text = user.username

        // get user's profile pics to display
        refUsers = db.reference.child("users").child(user.uid!!)
        refUsers!!.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    val imageUrl = snapshot.child("profileImageUrl").getValue().toString()

                    Picasso.get().load(imageUrl).into(viewHolder.itemView.friendList_profileImage)
                }
            }


            override fun onCancelled(error: DatabaseError) {

            }
        })

        //remove friend button pressed
        viewHolder.itemView.friendList_removeButton.setOnClickListener {
            // remove friend from your friend list
            db.reference.child("users")
                .child(auth.uid.toString())
                .child("friendList")
                .child(user.uid)
                .removeValue()

            //now we do the same for the user that you declined
            db.reference.child("users")
                .child(user.uid)
                .child("friendList")
                .child(auth.uid.toString())
                .removeValue()
        }


    }

    override fun getLayout(): Int {
        return R.layout.friend_list_item
    }
}