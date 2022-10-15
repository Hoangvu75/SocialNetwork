package com.example.fakebook.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_current_user_profile.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.reverse

class CurrentUserProfileActivity : AppCompatActivity() {

    private var user: Users? = null
    private var refUsers: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null

    private var postAdapter: PostAdapter? = null
    private var mPosts: List<Posts>? = null
    private var recyclerView: RecyclerView? = null

    private var postImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_user_profile)

        setSupportActionBar(currentUserProfileActivityToolbar)
        supportActionBar?.title = "Your Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        currentUserProfileActivityToolbar.setNavigationOnClickListener {
            finish()
            onBackPressed()
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        recyclerView = findViewById(R.id.currentUserProfileActivityRvPostItem)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        mPosts = ArrayList()

        setData()
    }

    private fun setData() {
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    user = p0.getValue(Users::class.java)
                    currentUserProfileActivityTvBigUsername.text = user?.getUsername()
                    Picasso.get().load(user?.getAvatar()).into(currentUserProfileActivityIvAvatar)
                    Picasso.get().load(user?.getCover()).into(currentUserProfileActivityIvCover)
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        })
        val refPosts = FirebaseDatabase.getInstance().getReference("Posts").child(firebaseUser!!.uid)
        refPosts.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mPosts as ArrayList<Posts>).clear()
                for (snap in snapshot.children){
                    val post = snap.getValue(Posts::class.java)
                    (mPosts as ArrayList<Posts>).add(post!!)
                }
                mPosts?.let { reverse(it) }
                postAdapter = PostAdapter(this@CurrentUserProfileActivity, mPosts!!)
                recyclerView!!.adapter = postAdapter
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun currentUserProfileActivityIvCoverOnClick(view: View) {
        val dialog = AlertDialog.Builder(this)
        val dialogChoices = arrayOf("View Cover Image", "Change Cover Image")
        dialog.setItems(dialogChoices) { _, which ->
            when (which) {
                0 -> {
                    val dialogViewImage = Dialog(this)
                    dialogViewImage.setContentView(R.layout.dialog_view_image)
                    dialogViewImage.setCancelable(true)
                    val ivDialogImage = dialogViewImage.findViewById<ImageView>(R.id.dialogViewImageIvImage)
                    ivDialogImage?.setImageDrawable(currentUserProfileActivityIvCover.drawable)
                    dialogViewImage.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialogViewImage.window?.setGravity(Gravity.CENTER)
                    dialogViewImage.show()
                    ivDialogImage.setOnClickListener {
                        dialogViewImage.dismiss()
                    }
                }
                1 -> {
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
                            AlertDialog.Builder(this@CurrentUserProfileActivity)
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
            }
        }.show()
    }

    fun currentUserProfileActivityIvAvatarOnClick(view: View) {
        val dialog = AlertDialog.Builder(this)
        val dialogChoices = arrayOf("View Avatar Image", "Change Avatar Image")
        dialog.setItems(dialogChoices) { _, which ->
            when (which) {
                0 -> {
                    val dialogViewImage = Dialog(this)
                    dialogViewImage.setContentView(R.layout.dialog_view_image)
                    dialogViewImage.setCancelable(true)
                    val ivDialogImage = dialogViewImage.findViewById<ImageView>(R.id.dialogViewImageIvImage)
                    ivDialogImage?.setImageDrawable(currentUserProfileActivityIvAvatar.drawable)
                    dialogViewImage.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    dialogViewImage.window?.setGravity(Gravity.CENTER)
                    dialogViewImage.show()
                    ivDialogImage.setOnClickListener {
                        dialogViewImage.dismiss()
                    }
                }
                1 -> {
                    Dexter.withActivity(this).withPermissions(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).withListener(object : com.karumi.dexter.listener.multi.MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            if (report!!.areAllPermissionsGranted()) {
                                val intent = Intent(Intent.ACTION_PICK)
                                intent.type = "image/*"
                                startActivityForResult(intent, 1)
                            }
                        }
                        override fun onPermissionRationaleShouldBeShown(
                            permissions: MutableList<PermissionRequest>?,
                            token: PermissionToken?
                        ) {
                            AlertDialog.Builder(this@CurrentUserProfileActivity)
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
            }
        }.show()
    }

    fun currentUserProfileActivityTvPostImageButtonOnClick(view: View) {
        Dexter.withActivity(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : com.karumi.dexter.listener.multi.MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, 2)
                }
            }
            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                AlertDialog.Builder(this@CurrentUserProfileActivity)
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

    fun currentUserProfileActivityTvPostButtonOnClick(view: View) {
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.dialog_progressbar)
        progressDialog.show()
        if (currentUserProfileActivityIvImagePost.visibility == View.GONE) {
            val postAvatar = user?.getAvatar().toString()
            val postUsername = user?.getUsername().toString()
            val postTime = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
            val postContent = currentUserProfileActivityEtWritePost.text.toString()
            val postImage = ""
            val postId = System.currentTimeMillis().toString()
            val postUserId = user?.getUID()!!
            if (postContent != "") {
                val post = Posts(postAvatar, postUsername, postTime, postContent, postImage, postId, postUserId)
                FirebaseDatabase.getInstance().getReference("Posts").child(postUserId).child(postId).setValue(post)
                currentUserProfileActivityEtWritePost?.text?.clear()
                progressDialog.dismiss()
            } else {
                Toast.makeText(this, "Please write something", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        } else if (currentUserProfileActivityIvImagePost.visibility == View.VISIBLE) {
            val postAvatar = user?.getAvatar().toString()
            val postUsername = user?.getUsername().toString()
            val postTime = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
            val postContent = currentUserProfileActivityEtWritePost.text.toString()
            val postId = System.currentTimeMillis().toString()
            val postUserId = user?.getUID()!!

            val storageReference = FirebaseStorage.getInstance().getReference("Posts").child(postUserId).child(postId).child("image").child(postId)
            storageReference.putFile(postImage!!).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    val postImage: String = it.toString()
                    val post = Posts(postAvatar, postUsername, postTime, postContent, postImage, postId, postUserId)
                    FirebaseDatabase.getInstance().getReference("Posts").child(firebaseUser!!.uid)
                        .child(postId).setValue(post)
                    progressDialog.dismiss()
                }
            }

            currentUserProfileActivityIvImagePost?.visibility = View.GONE
            currentUserProfileActivityIvImagePost?.setImageDrawable(null)
            currentUserProfileActivityEtWritePost?.text?.clear()
            postImage = null
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        postAdapter?.onActivityResult(requestCode, resultCode, data)

        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.dialog_progressbar)
        progressDialog.show()

        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            val image = data.data
            val storageReference = FirebaseStorage.getInstance().getReference(firebaseUser!!.uid).child("cover")
            storageReference.putFile(image!!).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    refUsers!!.child("cover").setValue(it.toString())
                    progressDialog.dismiss()
                }
            }
        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val image = data.data
            val storageReference = FirebaseStorage.getInstance().getReference(firebaseUser!!.uid).child("avatar")
            storageReference.putFile(image!!).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    refUsers!!.child("avatar").setValue(it.toString())
                    progressDialog.dismiss()
                }
            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            postImage = data.data
            currentUserProfileActivityIvImagePost.setImageURI(postImage)
            currentUserProfileActivityIvImagePost.visibility = View.VISIBLE
            progressDialog.dismiss()
        } else if (requestCode == 3 && resultCode == RESULT_OK && data != null) {
            image = data.data
            imageView!!.setImageURI(image)
            imageView!!.visibility = View.VISIBLE
            progressDialog.dismiss()
        }
    }
}