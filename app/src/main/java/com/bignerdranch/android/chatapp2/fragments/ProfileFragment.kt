package com.bignerdranch.android.chatapp2.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bignerdranch.android.chatapp2.ChatLogActivity
import com.bignerdranch.android.chatapp2.EditProfileActivity
import com.bignerdranch.android.chatapp2.R
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

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

        // sets an on click listener on the edit profile text view
        view.profile_editProfile.setOnClickListener {
            Log.d("ProfileFragment", "edit profile clicked")

            // switches to the edit profile activity page
            val intent = Intent(this.context, EditProfileActivity::class.java)
            startActivity(intent)
        }

        firebaseUser = auth.currentUser
        refUsers = db.reference.child("users").child(firebaseUser!!.uid)

        refUsers!!.addValueEventListener(object: ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {

                if(p0.exists()){

                    // gets profile image to display
                    val imageUrl = p0.child("profileImageUrl").value.toString()
                    Picasso.get().load(imageUrl).into(profile_profileImage)

                    val user: Users? = p0.getValue(Users::class.java)

                    // gets the big username to display
                    profile_username.text = user!!.username

                    // gets the small text field username to display
                    profile_username2.text = user!!.username

                    // gets their emails to be displayed
                    profile_email.text = user.email

                    // displays how many friends the user has
                    var friendCount : Int = 0
                    p0.child("friendList").children.forEach {
                        if (it.value == true){
                            friendCount++
                        }
                    }
                    profile_friends.text = friendCount.toString()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return view
    }

}