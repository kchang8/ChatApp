package com.bignerdranch.android.chatapp2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bignerdranch.android.chatapp2.R
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        firebaseUser = auth.currentUser
        refUsers = db.reference.child("users").child(firebaseUser!!.uid)

        refUsers!!.addValueEventListener(object: ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {

                if(p0.exists()){

                    val imageUrl = p0.child("profileImageUrl").getValue().toString()

                    Picasso.get().load(imageUrl).into(profile_profileImage)

                    val user: Users? = p0.getValue(Users::class.java)

                    profile_username.text = user!!.username

                    profile_username2.text = user!!.username

                    val friendsCount = p0.child("friendList").childrenCount.toString()

                    profile_friends.text = friendsCount
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return view
    }

}