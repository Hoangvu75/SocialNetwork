package com.example.fakebook.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.fakebook.R
import com.example.fakebook.fragments.*
import com.example.fakebook.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var refUsers: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(mainActivityToolbar)
        supportActionBar?.title = ""

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(HomepageFragment())
        viewPagerAdapter.addFragment(AddFriendFragment())
        viewPagerAdapter.addFragment(SearchFragment())
        viewPagerAdapter.addFragment(ChatFragment())
        viewPagerAdapter.addFragment(SettingFragment())

        mainActivityViewPager.adapter = viewPagerAdapter
        mainActivityTabLayout.setupWithViewPager(mainActivityViewPager)

        setIconTabLayout(0, R.drawable.ic_baseline_home_24)
        setIconTabLayout(1, R.drawable.ic_baseline_people_alt_24)
        setIconTabLayout(2, R.drawable.ic_baseline_search_24)
        setIconTabLayout(3, R.drawable.ic_baseline_chat_24)
        setIconTabLayout(4, R.drawable.ic_baseline_settings_24)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        refUsers!!.child("status").onDisconnect().setValue("offline")

        setData()
    }

    private fun setIconTabLayout(tab: Int, icon: Int) {
        mainActivityTabLayout.getTabAt(tab)?.setIcon(icon)
        mainActivityTabLayout.getTabAt(tab)?.icon?.setTint(resources.getColor(R.color.white))
    }

    private fun setData() {
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user: Users? = p0.getValue(Users::class.java)
                    mainActivityTvUsername.text = user!!.getUsername()
                    Picasso.get().load(user.getAvatar()).into(mainActivityIvAvatar)
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    fun mainActivityIvAvatarOnClick(view: View) {
        val intent = Intent(this, CurrentUserProfileActivity::class.java)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        refUsers!!.child("status").setValue("online")
    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        private val fragments: ArrayList<Fragment> = ArrayList<Fragment>()

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment: Fragment) {
            fragments.add(fragment)
        }
    }
}