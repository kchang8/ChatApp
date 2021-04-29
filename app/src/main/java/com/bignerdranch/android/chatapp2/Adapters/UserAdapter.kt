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
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.user_item_layout.view.*

class UserAdapter
    (private var mContext: Context,
     private var mUser: List<Users>,
     private var isFragment: Boolean = false) : RecyclerView.Adapter<UserAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]

        holder.searchUsernameTextView.text = user.getUsername()

        holder.searchAddButton.setOnClickListener(){
            Log.d("addingFriend", "now adding friend")
            val db = FirebaseDatabase.getInstance()
            val auth = FirebaseAuth.getInstance()
            var addUser = holder.searchUsernameTextView.text.toString()
            for (user in mUser){
                if (user.getUsername() == addUser){
                    addUser = user.getUID()!!
                }
            }
            val setUser = db.reference.child("users").child(auth.uid.toString()).child("friendList").child(addUser).setValue(false)
        }

        //Picasso.get().load(user.getImageURL()).placeholder(R.drawable.ic_baseline_person_24).into(holder.searchProfileImage)
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var searchUsernameTextView: TextView = itemView.findViewById(R.id.searchItem_username)
        var searchProfileImage: CircleImageView = itemView.findViewById(R.id.searchItem_profileImage)
        var searchAddButton: ImageView = itemView.findViewById(R.id.searchItem_addButton)

    }

}