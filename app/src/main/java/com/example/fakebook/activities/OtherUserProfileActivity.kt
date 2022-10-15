package com.example.fakebook.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fakebook.R
import com.example.fakebook.adapter.PostAdapter
import com.example.fakebook.adapter.image
import com.example.fakebook.adapter.imageView
import com.example.fakebook.models.Posts
import com.example.fakebook.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_other_user_profile.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.reverse

class OtherUserProfileActivity : AppCompatActivity() {

    private var refOtherUser: DatabaseReference? = null
    private var otherUser: Users? = null

    private var refUsers: DatabaseReference? = null
    private var user: Users? = null
    private var firebaseUser: FirebaseUser? = null

    private var postAdapter: PostAdapter? = null
    private var mPosts: List<Posts>? = null
    private var recyclerView: RecyclerView? = null

    private var postImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user_profile)

        setSupportActionBar(otherUserProfileActivityToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        otherUserProfileActivityToolbar.setNavigationOnClickListener {
            onBackPressed()
            finish()
        }

        val getIntentOtherUserId = intent.getStringExtra("otherUserId")
        refOtherUser = FirebaseDatabase.getInstance().reference.child("Users").child(getIntentOtherUserId!!)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        recyclerView = findViewById(R.id.otherUserProfileActivityRvPostItem)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        mPosts = ArrayList()

        setData()
    }

    private fun setData() {
        refOtherUser?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                otherUser = dataSnapshot.getValue(Users::class.java)
                Picasso.get().load(otherUser?.getCover()).into(otherUserProfileActivityIvCover)
                Picasso.get().load(otherUser?.getAvatar()).into(otherUserProfileActivityIvAvatar)
                otherUserProfileActivityTvBigUsername.text = otherUser?.getUsername()
                supportActionBar?.title = otherUser?.getUsername()
                FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser!!.uid).child(otherUser?.getUID()!!).addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            otherUserProfileActivityTvAddFriend.text = p0.value.toString()
                        }
                    }
                })
                val refPosts = FirebaseDatabase.getInstance().getReference("Posts").child(otherUser?.getUID()!!)
                refPosts.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        (mPosts as ArrayList<Posts>).clear()
                        for (snap in snapshot.children){
                            val post = snap.getValue(Posts::class.java)
                            (mPosts as ArrayList<Posts>).add(post!!)
                            Log.d("Posts", post.getUsername())
                        }
                        mPosts?.let { reverse(it) }
                        postAdapter = PostAdapter(this@OtherUserProfileActivity, mPosts!!)
                        recyclerView!!.adapter = postAdapter
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        refUsers?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(Users::class.java)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun otherUserProfileActivityIvCoverOnClick(view: View) {
        val dialogViewImage = Dialog(this)
        dialogViewImage.setContentView(R.layout.dialog_view_image)
        dialogViewImage.setCancelable(true)
        val ivDialogImage = dialogViewImage.findViewById<ImageView>(R.id.dialogViewImageIvImage)
        ivDialogImage?.setImageDrawable(otherUserProfileActivityIvCover.drawable)
        dialogViewImage.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogViewImage.window?.setGravity(Gravity.CENTER)
        dialogViewImage.show()
        ivDialogImage.setOnClickListener {
            dialogViewImage.dismiss()
        }
    }

    fun otherUserProfileActivityIvAvatarOnClick(view: View) {
        val dialogViewImage = Dialog(this)
        dialogViewImage.setContentView(R.layout.dialog_view_image)
        dialogViewImage.setCancelable(true)
        val ivDialogImage = dialogViewImage.findViewById<ImageView>(R.id.dialogViewImageIvImage)
        ivDialogImage?.setImageDrawable(otherUserProfileActivityIvAvatar.drawable)
        dialogViewImage.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogViewImage.window?.setGravity(Gravity.CENTER)
        dialogViewImage.show()
        ivDialogImage.setOnClickListener {
            dialogViewImage.dismiss()
        }
    }

    fun otherUserProfileActivityTvPostImageButtonOnClick(view: View) {
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : com.karumi.dexter.listener.multi.MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, 0)
                }
            }
            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                AlertDialog.Builder(this@OtherUserProfileActivity)
                    .setTitle("Permission Denied")
                    .setMessage("You have denied permission to access gallery. Please allow permission to access gallery to select image")
                    .setNegativeButton("Cancel") { _, _ ->
                        token?.cancelPermissionRequest()
                    }
                    .setPositiveButton("GO TO SETTING") { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }.show()
            }
        }).check()
    }

    fun otherUserProfileActivityTvPostButtonOnClick(view: View) {
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.dialog_progressbar)
        progressDialog.show()
        if (otherUserProfileActivityIvImagePost.visibility == View.GONE) {
            val postAvatar = user?.getAvatar().toString()
            val postUsername = user?.getUsername().toString()
            val postTime = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
            val postContent = otherUserProfileActivityEtWritePost.text.toString()
            val postImage = ""
            val postId = System.currentTimeMillis().toString()
            val postUserId = otherUser?.getUID()!!
            if (postContent != "") {
                val post = Posts(postAvatar, postUsername, postTime, postContent, postImage, postId, postUserId)
                FirebaseDatabase.getInstance().getReference("Posts").child(postUserId).child(postId).setValue(post)
                otherUserProfileActivityEtWritePost?.text?.clear()
                progressDialog.dismiss()
            } else {
                Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        } else if (otherUserProfileActivityIvImagePost.visibility == View.VISIBLE) {
            val postAvatar = user?.getAvatar().toString()
            val postUsername = user?.getUsername().toString()
            val postTime = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
            val postContent = otherUserProfileActivityEtWritePost.text.toString()
            val postId = System.currentTimeMillis().toString()
            val postUserId = otherUser?.getUID()!!
            val storageReference = FirebaseStorage.getInstance().getReference("Posts").child(postUserId).child(postId).child("image").child(postId)
            storageReference.putFile(postImage!!).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    val postImage: String = it.toString()
                    val post = Posts(postAvatar, postUsername, postTime, postContent, postImage, postId, postUserId)
                    FirebaseDatabase.getInstance().getReference("Posts").child(postUserId)
                        .child(postId).setValue(post)
                    progressDialog.dismiss()
                }
            }
            otherUserProfileActivityIvImagePost?.visibility = View.GONE
            otherUserProfileActivityIvImagePost?.setImageDrawable(null)
            otherUserProfileActivityEtWritePost?.text?.clear()
            postImage = null
        }
    }

    fun otherUserProfileActivityCvAddFriendOnClick(view: View) {
        if (otherUserProfileActivityTvAddFriend.text == "Add friend") {
            FirebaseDatabase.getInstance().getReference("Friends").child(user?.getUID()!!).child(otherUser?.getUID()!!).setValue("Request sent")
            FirebaseDatabase.getInstance().getReference("Friends").child(otherUser?.getUID()!!).child(user?.getUID()!!).setValue("Request received")
            FirebaseDatabase.getInstance().getReference("Friends request").child(otherUser?.getUID()!!).child(user?.getUID()!!).setValue(user)
        } else if (otherUserProfileActivityTvAddFriend.text == "Request sent") {
            FirebaseDatabase.getInstance().getReference("Friends").child(user?.getUID()!!).child(otherUser?.getUID()!!).removeValue()
            FirebaseDatabase.getInstance().getReference("Friends").child(otherUser?.getUID()!!).child(user?.getUID()!!).removeValue()
            otherUserProfileActivityTvAddFriend.text = "Add friend"
            FirebaseDatabase.getInstance().getReference("Friends request").child(otherUser?.getUID()!!).child(user?.getUID()!!).removeValue()
        } else if (otherUserProfileActivityTvAddFriend.text == "Request received") {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Accept friend request")
            dialog.setMessage("Do you want to accept friend request from ${otherUser?.getUsername()}")
            dialog.setNegativeButton("Decline") { _, _ ->
                dialog.setCancelable(true)
                FirebaseDatabase.getInstance().getReference("Friends").child(user?.getUID()!!).child(otherUser?.getUID()!!).removeValue()
                FirebaseDatabase.getInstance().getReference("Friends").child(otherUser?.getUID()!!).child(user?.getUID()!!).removeValue()
                FirebaseDatabase.getInstance().getReference("Friends request").child(user?.getUID()!!).child(otherUser?.getUID()!!).removeValue()
            }.setPositiveButton("Accept") { _, _ ->
                FirebaseDatabase.getInstance().getReference("Friends").child(user?.getUID()!!).child(otherUser?.getUID()!!).setValue("Friends")
                FirebaseDatabase.getInstance().getReference("Friends").child(otherUser?.getUID()!!).child(user?.getUID()!!).setValue("Friends")
                FirebaseDatabase.getInstance().getReference("Friends request").child(user?.getUID()!!).child(otherUser?.getUID()!!).removeValue()
                FirebaseDatabase.getInstance().getReference("Friend list").child(user?.getUID()!!).child(otherUser?.getUID()!!).setValue(otherUser)
                FirebaseDatabase.getInstance().getReference("Friend list").child(otherUser?.getUID()!!).child(user?.getUID()!!).setValue(user)
            }.show()
        } else if (otherUserProfileActivityTvAddFriend.text == "Friends") {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Remove friend")
            dialog.setMessage("Do you want to remove ${otherUser?.getUsername()} from your friend list")
            dialog.setNegativeButton("Cancel") { _, _ ->
                dialog.setCancelable(true)
            }.setPositiveButton("Remove") { _, _ ->
                FirebaseDatabase.getInstance().getReference("Friends").child(user?.getUID()!!).child(otherUser?.getUID()!!).removeValue()
                FirebaseDatabase.getInstance().getReference("Friends").child(otherUser?.getUID()!!).child(user?.getUID()!!).removeValue()
                otherUserProfileActivityTvAddFriend.text = "Add friend"
                FirebaseDatabase.getInstance().getReference("Friend list").child(user?.getUID()!!).child(otherUser?.getUID()!!).removeValue()
                FirebaseDatabase.getInstance().getReference("Friend list").child(otherUser?.getUID()!!).child(user?.getUID()!!).removeValue()
            }.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        postAdapter?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            postImage = data.data
            otherUserProfileActivityIvImagePost.setImageURI(postImage)
            otherUserProfileActivityIvImagePost.visibility = View.VISIBLE
        } else if (requestCode == 3 && resultCode == RESULT_OK && data != null) {
            image = data.data
            imageView!!.setImageURI(image)
            imageView!!.visibility = View.VISIBLE
        }
    }
}