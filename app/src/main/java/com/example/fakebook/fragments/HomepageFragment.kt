package com.example.fakebook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fakebook.R
import com.example.fakebook.adapter.PostAdapter
import com.example.fakebook.models.Posts
import com.example.fakebook.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Collections.reverse
import kotlin.collections.ArrayList

class HomepageFragment : Fragment() {

    private var firebaseUser: FirebaseUser? = null

    private var postAdapter: PostAdapter? = null
    private var mPosts: List<Posts>? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_homepage, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        recyclerView = view.findViewById(R.id.homepageFragmentRvPostItem)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        mPosts = ArrayList<Posts>()

        setData()

        return view
    }

    private fun setData() {
        FirebaseDatabase.getInstance().getReference("Friend list").child(firebaseUser!!.uid).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (mPosts as ArrayList<Posts>).clear()
                for (dataSnapshot in snapshot.children) {
                    val user: Users? = dataSnapshot.getValue(Users::class.java)
                    FirebaseDatabase.getInstance().getReference("Posts").child(user?.getUID().toString()).addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (postSnapshot in snapshot.children) {
                                val post = postSnapshot.getValue(Posts::class.java)
                                (mPosts as ArrayList<Posts>).add(post!!)
                            }
                            mPosts?.let { reverse(it) }
                            postAdapter = PostAdapter(context!!, mPosts!! as ArrayList<Posts>)
                            recyclerView!!.adapter = postAdapter
                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}