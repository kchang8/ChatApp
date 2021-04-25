package com.bignerdranch.android.chatapp2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.chatapp2.fragments.ChatFragment
import com.bignerdranch.android.chatapp2.fragments.FeedFragment
import com.bignerdranch.android.chatapp2.fragments.SettingsFragment
import com.bignerdranch.android.chatapp2.fragments.adapters.ViewPagerAdapter
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*

const val HOME_TAG = "HomeActivity"

class HomeActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null


    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()


        verifyUserIsLoggedIn()

        //Display username and provide pic
        firebaseUser = auth.currentUser
        refUsers = db.reference.child("users").child(firebaseUser!!.uid)

        refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot){

                p0.children.forEach{
                    Log.d("UserInfo", it.toString())
                }

                if (p0.exists()){

                    val user: Users? = p0.getValue(Users::class.java)

                    home_username.text = user!!.getUsername()

                    val imageUrl = p0.child("profileImageUrl").getValue().toString()

                    Picasso.get().load(imageUrl).into(home_profilePicture)


                }

            }

            override fun onCancelled(p0: DatabaseError){

            }
        })



        setUpTabs()

        home_sign_out.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


    }

    private fun verifyUserIsLoggedIn() {
        val uid = auth.uid

        Log.d("Register", "Current uid: $uid")
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun setUpTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(ChatFragment(), "Chats")
        adapter.addFragment(FeedFragment(), "Feed")
        adapter.addFragment(SettingsFragment(), "Settings")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_chat_24)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_feed_24)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_baseline_settings_24)
    }

}