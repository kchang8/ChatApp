package com.bignerdranch.android.chatapp2.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.chatapp2.R
import com.bignerdranch.android.chatapp2.modelClasses.Post
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.posts_layout.view.*

class PostAdapter
    (private val mContext: Context,
     private val mPost: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>()
{

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

        Picasso.get().load(post.getPostImage()).into(holder.postImage)

        publisherInfo(holder.profileImage, holder.username, holder.uid, post.getPostUid())
    }


    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profileImage: CircleImageView
        var postImage: ImageView
        var likeButton: ImageView
        var commentButton: ImageView
        var username: TextView
        var likes: TextView
        var uid: TextView
        var description: TextView
        var comments: TextView


        init {
            profileImage = itemView.findViewById(R.id.post_userProfile)
            postImage = itemView.findViewById(R.id.post_mainImage)
            likeButton = itemView.findViewById(R.id.post_likeButton)
            commentButton = itemView.findViewById(R.id.post_commentButton)
            username = itemView.findViewById(R.id.post_username)
            likes = itemView.findViewById(R.id.post_likes)
            uid = itemView.findViewById(R.id.post_uid)
            description = itemView.findViewById(R.id.post_userProfile)
            profileImage = itemView.findViewById(R.id.post_userProfile)
            comments = itemView.findViewById(R.id.post_comments)
        }
    }

    private fun publisherInfo(profileImage: CircleImageView, username: TextView, uid: TextView, postUid: String)
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(postUid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    val user = p0.getValue<Users>(Users::class.java)

                    Picasso.get().load(user!!.getImageURL()).placeholder(R.drawable.profile_icon).into(profileImage)
                    username.text = user!!.getUsername()


                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}