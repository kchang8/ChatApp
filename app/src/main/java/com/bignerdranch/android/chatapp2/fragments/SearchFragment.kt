package com.bignerdranch.android.chatapp2.fragments

import android.os.Bundle
import android.renderscript.Sampler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.chatapp2.Adapters.UserAdapter
import com.bignerdranch.android.chatapp2.R
import com.bignerdranch.android.chatapp2.modelClasses.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.user_item_layout.view.*


const val SEARCH_TAG = "SearchFragment"

class SearchFragment : Fragment()
{
    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<Users>? = null

    lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        db = FirebaseDatabase.getInstance()

//        view.search_searchIcon.setOnClickListener()
//        {
//            getUserList()
//            searchUser()
//        }

        recyclerView = view.findViewById(R.id.search_recyclerView)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, mUser as ArrayList<Users>, true)}
        recyclerView?.adapter = userAdapter


        view.search_searchBar.addTextChangedListener(object: TextWatcher
        {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
            {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (view.search_searchBar.text.toString() == "")
                {

                }
                else
                {
                    Log.d(SEARCH_TAG, "typing something")
                    recyclerView?.visibility = View.VISIBLE

                    retrieveUsers()
                    searchUser(s.toString().toLowerCase())
                }
            }

            override fun afterTextChanged(s: Editable?)
            {

            }
        })


        return view

    }


    private fun retrieveUsers()
    {
        val usersRef = db.reference.child("users")
        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (view?.search_searchBar?.text.toString() == "")
                {
                    mUser?.clear()

                    Log.d(SEARCH_TAG, "starting to retrieve users")
                    for (snapshot in dataSnapshot.children)
                    {
                        val user = snapshot.getValue(Users::class.java)

                        Log.d(SEARCH_TAG, "User: $user")
                        if (user != null)
                        {
                            mUser?.add(user)
                            Log.d(SEARCH_TAG, "List of Users: $mUser")
                        }
                    }

                    userAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

    private fun searchUser(input: String)
    {
        val searchQuery = db.reference
                .child("users")
                .orderByChild("username")
                .startAt(input)
                .endAt(input + "\uf8ff")

        val auth = FirebaseAuth.getInstance()

        searchQuery.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                mUser?.clear()

                Log.d(SEARCH_TAG, "searching user")

                val user = FirebaseAuth.getInstance()
                for (snapshot in dataSnapshot.children)
                {
                    val user = snapshot.getValue(Users::class.java)
                    val uid = user!!.getUID()
                    if (user != null && uid != auth.uid)
                    {
                        mUser?.add(user)
                    }
                }

                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })
    }

}