package com.bignerdranch.android.chatapp2

import android.content.Intent
import android.os.Bundle


import android.widget.TextView

import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bignerdranch.android.chatapp2.fragments.ChatFragment
import com.bignerdranch.android.chatapp2.fragments.SearchFragment
import com.bignerdranch.android.chatapp2.fragments.SettingsFragment
import com.bignerdranch.android.chatapp2.fragments.adapters.ViewPagerAdapter

import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        verifyUserIsLoggedIn()

        //Display username and provide pic
        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)

        refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot){
                if (p0.exists()){

                    val user: Users? = p0.getValue(Users::class.java)

                    home_username.text = user!!.getUsername()
                    // HALP
                    Picasso.get().load(user.getImageURL()).into(home_profilePicture)

                }

            }

            override fun onCancelled(p0: DatabaseError){

            }
        })


        setUpTabs()

        val signOut: TextView = findViewById(R.id.home_sign_out)
        signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


    }


    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun setUpTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(ChatFragment(), "Chats")
        adapter.addFragment(SearchFragment(), "Search")
        adapter.addFragment(SettingsFragment(), "Settings")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_chat_24)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_search_24)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_baseline_settings_24)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}