package com.bignerdranch.android.chatapp2.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.chatapp2.R
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.search_list_item.view.*

const val USER_ADPT_TAG = "UserAdapter"

class UserAdapter
    (private var mContext: Context,
     private var mUser: List<Users>,
     private var isFragment: Boolean = false) : RecyclerView.Adapter<UserAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = null
    private var refUsers: DatabaseReference? = null

    lateinit var db: FirebaseDatabase
    lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.search_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val user = mUser[position]

        //gets user's username to display
        holder.searchUsernameTextView.text = user.getUsername()

        // add button click listener
        holder.searchAddButton.setOnClickListener(){
            var addUser = holder.searchUsernameTextView.text.toString()

            //this for loop is bad code yiiiikes
            for (user in mUser){
                if (user.getUsername() == addUser){
                    addUser = user.getUID()!!
                }
            }

            //gets node at which the addUser should be at
            val friendList = db.reference.child("users").child(addUser).child("friendList").child(auth.uid.toString()).get()
            friendList.addOnSuccessListener {
                if (it.exists()){
                    Log.d("friendAlreadyAdded", "You already made a friend request ")
                }
                //writes to targeted user's information
                else{
                    Log.d("addingFriend", "now adding friend")
                    db.reference.child("users").child(addUser.toString()).child("friendList").child(auth.uid.toString()).setValue(false)
                }
            }.addOnFailureListener {
            }
        }

        // displaying user profile pictures
        refUsers = db.reference.child("users").child(user.getUID()!!)

        refUsers!!.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {
                    val imageUrl = snapshot.child("profileImageUrl").getValue().toString()

                    Picasso.get().load(imageUrl).into(holder.searchProfileImage)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var searchUsernameTextView: TextView = itemView.findViewById(R.id.searchItem_username)
        var searchProfileImage: CircleImageView = itemView.findViewById(R.id.searchItem_profileImage)
        var searchAddButton: ImageView = itemView.findViewById(R.id.searchItem_addButton)

    }

}