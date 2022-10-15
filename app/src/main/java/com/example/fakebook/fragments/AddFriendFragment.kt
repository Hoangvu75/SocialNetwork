package com.example.fakebook.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fakebook.R
import com.example.fakebook.adapter.UserAdapter
import com.example.fakebook.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class AddFriendFragment : Fragment() {

    private var firebaseUser: FirebaseUser? = null

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_friend, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        recyclerView = view.findViewById(R.id.addFriendFragmentRvUsers)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        mUsers = ArrayList<Users>()

        retrieveFriendRequests()

        return view
    }

    private fun retrieveFriendRequests() {
        val refFriendRequest = FirebaseDatabase.getInstance().getReference("Friends request").child(firebaseUser!!.uid)
        refFriendRequest.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snapshot in dataSnapshot.children) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    (mUsers as ArrayList<Users>).add(user!!)
                }
                userAdapter = UserAdapter(context!!, mUsers as ArrayList<Users>)
                recyclerView!!.adapter = userAdapter
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}