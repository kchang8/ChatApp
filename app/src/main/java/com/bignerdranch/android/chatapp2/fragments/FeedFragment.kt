package com.bignerdranch.android.chatapp2.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.chatapp2.*
import com.bignerdranch.android.chatapp2.Adapters.PostAdapter
import com.bignerdranch.android.chatapp2.R
import com.bignerdranch.android.chatapp2.modelClasses.Post
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.feed_list_item.view.*
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.fragment_feed.view.*
import kotlinx.android.synthetic.main.new_message_user_item.view.*
import kotlinx.android.synthetic.main.posts_layout.*

class FeedFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

//        view.feed_addPostFab.setOnClickListener {
//            val intent = Intent(activity, AddPostActivity::class.java)
//            startActivity(intent)
//        }

        recyclerView = view.findViewById(R.id.feed_recyclerView)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        recyclerView!!.adapter = adapter

        //setupDummyData()
        viewFriendRequests()

        return view
    }

//    private fun setupDummyData() {
//        adapter.add(FriendRequestRow())
//        adapter.add(FriendRequestRow())
//    }

    private fun viewFriendRequests() {
        val ref = db.reference.child("users").child(auth.uid.toString()).child("friendList")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                p0.children.forEach {
                    Log.d("FeedFragment", it.toString())
                    if (it.value == false){
                        val userRef = db.reference.child("users").child(it.key.toString()).get().addOnSuccessListener {
                            val user = it.getValue(Users::class.java)
                            Log.d("FeedFragment", user.toString())
                            if (user != null){
                                adapter.add(FriendRequestRow(user))
                            }
                        }
                    }
                }

                recyclerView!!.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


}

class FriendRequestRow(val user: Users): Item<ViewHolder>() {
    private var refUsers: DatabaseReference? = null

    lateinit var db: FirebaseDatabase

    override fun getLayout(): Int {
        return R.layout.feed_list_item
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        db = FirebaseDatabase.getInstance()

        // get user's usernames to display
        viewHolder.itemView.feedItem_username.text = user.username

        // get user's profile pics to display
        refUsers = db.reference.child("users").child(user.uid!!)
        refUsers!!.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    val imageUrl = snapshot.child("profileImageUrl").getValue().toString()

                    Picasso.get().load(imageUrl).into(viewHolder.itemView.feedItem_profileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}