package com.example.fakebook.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fakebook.R
import com.example.fakebook.adapter.UserAdapter
import com.example.fakebook.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var recyclerView: RecyclerView? = null
    private var searchEditText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.searchFragmentRvUserSearch)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        mUsers = ArrayList<Users>()
        searchEditText = view.findViewById(R.id.searchFragmentEtSearch)

        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    recyclerView!!.visibility = View.GONE
                    hideAllUser()
                    userAdapter!!.notifyDataSetChanged()
                } else {
                    recyclerView!!.visibility = View.VISIBLE
                    searchForUsers(s.toString().toLowerCase())
                }
            }
        })

        return view
    }

    private fun hideAllUser(){
        (mUsers as ArrayList<Users>).clear()
        userAdapter = context?.let { UserAdapter(it, mUsers!!) }
        recyclerView!!.adapter = userAdapter
    }

    private fun searchForUsers(str: String){
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val queryUsers = FirebaseDatabase.getInstance().reference.child("Users").orderByChild("search").startAt(str).endAt(str + "\uf8ff")
        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snapshot in dataSnapshot.children) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (!(user!!.getUID()).equals(firebaseUserID)) {
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!, mUsers!! as ArrayList<Users>)
                recyclerView!!.adapter = userAdapter
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}