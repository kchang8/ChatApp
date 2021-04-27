package com.bignerdranch.android.chatapp2.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.chatapp2.R
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.user_item_layout.view.*

class UserAdapter
    (private var mContext: Context,
     private var mUser: List<Users>,
     private var isFragment: Boolean = false) : RecyclerView.Adapter<UserAdapter.ViewHolder>()
{
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
        Picasso.get().load(user.getImageURL()).placeholder(R.drawable.profile_icon).into(holder.searchProfileImage)
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var searchUsernameTextView: TextView = itemView.findViewById(R.id.search_username)
        var searchProfileImage: CircleImageView = itemView.findViewById(R.id.search_profileImage)
        var searchAddButton: Button = itemView.findViewById(R.id.search_addButton)

    }

}