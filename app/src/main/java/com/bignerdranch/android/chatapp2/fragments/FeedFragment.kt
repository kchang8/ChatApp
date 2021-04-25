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
import com.bignerdranch.android.chatapp2.AddPostActivity
import com.bignerdranch.android.chatapp2.HomeActivity
import com.bignerdranch.android.chatapp2.R
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.fragment_feed.view.*

class FeedFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        var recyclerView: RecyclerView? = null

        view.feed_addPostFab.setOnClickListener {
            val intent = Intent(activity, AddPostActivity::class.java)
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.feed_recyclerView)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        return view
    }


}