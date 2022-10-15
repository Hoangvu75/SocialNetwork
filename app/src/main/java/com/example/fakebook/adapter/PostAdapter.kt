package com.example.fakebook.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fakebook.R
import com.example.fakebook.adapter.PostAdapter.ViewHolder
import com.example.fakebook.models.Comments
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
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_current_user_profile.*
import kotlinx.android.synthetic.main.activity_other_user_profile.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StaticFieldLeak")
var imageView: ImageView? = null
var image: Uri? = null

class PostAdapter(private val mContext: Context, private val mPost: List<Posts>) : RecyclerView.Adapter<ViewHolder?>() {

    private var user: Users? = null
    private var refUsers: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null

    private var commentAdapter: CommentAdapter? = null
    private var mComments: List<Comments>? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_post, parent, false)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        refUsers!!.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    user = p0.getValue(Users::class.java)
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
        return ViewHolder(view as ViewGroup)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = mPost[position]
        Picasso.get().load(post.getAvatar()).into(holder.recyclerViewItemPostIvAvatar)
        holder.recyclerViewItemPostTvUsername.text = post.getUsername()
        holder.recyclerViewItemPostTvTime.text = post.getTime()
        if (post.getContent() != "") {
            holder.recyclerViewItemPostTvContent.visibility = LinearLayout.VISIBLE
            holder.recyclerViewItemPostTvContent.text = post.getContent()
        }
        if (post.getImage() != "") {
            holder.recyclerViewItemPostIvImage.visibility = LinearLayout.VISIBLE
            Picasso.get().load(post.getImage()).into(holder.recyclerViewItemPostIvImage)
        }
        holder.recyclerViewItemPostLlComment.setOnClickListener {
            val dialog = Dialog(mContext)
            dialog.setContentView(R.layout.dialog_post_comment)
            dialog.setCancelable(true)
            dialog.window?.setGravity(Gravity.CENTER)
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            recyclerView = dialog.findViewById(R.id.dialogPostCommentRvComments)
            recyclerView!!.setHasFixedSize(true)
            recyclerView!!.layoutManager = LinearLayoutManager(mContext)
            mComments = ArrayList()

            val refComments = FirebaseDatabase.getInstance().getReference("Posts").child(post.getUserId()).child(post.getId()).child("Comments")
            refComments.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    (mComments as ArrayList<Comments>).clear()
                    for (snapshot in dataSnapshot.children) {
                        val comment = snapshot.getValue(Comments::class.java)
                        (mComments as ArrayList<Comments>).add(comment!!)
                    }
                    commentAdapter = CommentAdapter(mContext, mComments!!)
                    recyclerView!!.adapter = commentAdapter
                }
                override fun onCancelled(p0: DatabaseError) {

                }
            })

            val dialogPostCommentTvCommentImageButton = dialog.findViewById<TextView>(R.id.dialogPostCommentTvCommentImageButton)
            dialogPostCommentTvCommentImageButton.setOnClickListener {
                Dexter.withContext(mContext).withPermissions(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(object : com.karumi.dexter.listener.multi.MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            imageView = dialog.findViewById(R.id.dialogPostCommentIvImageCommented)
                            (mContext as Activity).startActivityForResult(intent, 3)
                        }
                    }
                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        AlertDialog.Builder(mContext)
                            .setTitle("Permission Denied")
                            .setMessage("You have denied permission to access gallery. Please allow permission to access gallery to select image")
                            .setNegativeButton("Cancel") { _, _ ->
                                token?.cancelPermissionRequest()
                            }
                            .setPositiveButton("GO TO SETTING") { _, _ ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", mContext.packageName, null)
                                intent.data = uri
                                mContext.startActivity(intent)
                            }.show()
                    }
                }).check()
            }

            val dialogPostCommentTvSendComment = dialog.findViewById<TextView>(R.id.dialogPostCommentTvSendComment)
            dialogPostCommentTvSendComment.setOnClickListener {
                val progressDialog = Dialog(mContext)
                progressDialog.setContentView(R.layout.dialog_progressbar)
                progressDialog.show()
                val dialogPostCommentIvImageCommented = dialog.findViewById<ImageView>(R.id.dialogPostCommentIvImageCommented)
                if (dialogPostCommentIvImageCommented.visibility == View.GONE) {
                    val commentAvatar = user!!.getAvatar().toString()
                    val commentUsername = user!!.getUsername().toString()
                    val commentTime = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
                    val commentContent = dialog.findViewById<EditText>(R.id.dialogPostCommentEtWriteComment).text.toString()
                    val commentId = System.currentTimeMillis().toString()
                    if (commentContent != "") {
                        val commentItem = Comments(commentAvatar, commentUsername, commentTime, commentContent, "", commentId)
                        FirebaseDatabase.getInstance().getReference("Posts").child(post.getUserId())
                            .child(post.getId()).child("Comments").child(commentId).setValue(commentItem)
                        dialog.findViewById<EditText>(R.id.dialogPostCommentEtWriteComment).text.clear()
                        progressDialog.dismiss()
                    } else {
                        Toast.makeText(mContext, "Please write a comment", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }
                } else if (dialogPostCommentIvImageCommented.visibility == View.VISIBLE) {
                    val commentAvatar = user!!.getAvatar().toString()
                    val commentUsername = user!!.getUsername().toString()
                    val commentTime = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
                    val commentContent = dialog.findViewById<EditText>(R.id.dialogPostCommentEtWriteComment).text.toString()
                    val commentId = System.currentTimeMillis().toString()
                    val storageReference = FirebaseStorage.getInstance().getReference("Posts").child(post.getUserId()).child(post.getId()).child("Comments").child(commentId)
                    storageReference.putFile(image!!).addOnSuccessListener {
                        storageReference.downloadUrl.addOnSuccessListener {
                            val image: String = it.toString()
                            val commentItem = Comments(commentAvatar, commentUsername, commentTime, commentContent, image, commentId)
                            FirebaseDatabase.getInstance().getReference("Posts").child(post.getUserId())
                                .child(post.getId()).child("Comments").child(commentId).setValue(commentItem)
                            progressDialog.dismiss()
                        }
                    }
                    imageView?.visibility = ImageView.GONE
                    dialog.findViewById<EditText>(R.id.dialogPostCommentEtWriteComment).text.clear()
                    imageView = null
                    image = null
                }
            }
            dialog.show()
        }
        holder.recyclerViewItemPostCvPost.setOnLongClickListener {
            if (firebaseUser!!.uid == post.getUserId()) {
                val dialog = AlertDialog.Builder(mContext)
                val dialogChoices = arrayOf("Edit", "Delete")
                dialog.setItems(dialogChoices) { _, which ->
                    when (which) {
                        0 -> {
                            val editPostDialog = Dialog(mContext)
                            editPostDialog.setContentView(R.layout.dialog_edit_post)
                            editPostDialog.setCanceledOnTouchOutside(true)
                            Picasso.get().load(post.getAvatar()).into(editPostDialog.findViewById<ImageView>(R.id.dialogEditPostIvAvatar))
                            editPostDialog.findViewById<TextView>(R.id.dialogEditPostTvUsername).text = post.getUsername()
                            editPostDialog.findViewById<TextView>(R.id.dialogEditPostTvTime).text = post.getTime()
                            if (post.getContent() != "") {
                                editPostDialog.findViewById<EditText>(R.id.dialogEditPostEtContent).setText(post.getContent())
                            }
                            if (post.getImage() != "") {
                                Picasso.get().load(post.getImage()).into(editPostDialog.findViewById<ImageView>(R.id.dialogEditPostIvImage))
                            }
                            editPostDialog.findViewById<ImageView>(R.id.dialogEditPostIvImage).setOnClickListener {
                                Dexter.withContext(mContext).withPermissions(
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ).withListener(object : com.karumi.dexter.listener.multi.MultiplePermissionsListener {
                                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                                        if (report!!.areAllPermissionsGranted()) {
                                            val intent = Intent(Intent.ACTION_PICK)
                                            intent.type = "image/*"
                                            imageView = editPostDialog.findViewById(R.id.dialogEditPostIvImage)
                                            (mContext as Activity).startActivityForResult(intent, 3)
                                        }
                                    }
                                    override fun onPermissionRationaleShouldBeShown(
                                        permissions: MutableList<PermissionRequest>?,
                                        token: PermissionToken?
                                    ) {
                                        AlertDialog.Builder(mContext)
                                            .setTitle("Permission Denied")
                                            .setMessage("You have denied permission to access gallery. Please allow permission to access gallery to select image")
                                            .setNegativeButton("Cancel") { _, _ ->
                                                token?.cancelPermissionRequest()
                                            }
                                            .setPositiveButton("GO TO SETTING") { _, _ ->
                                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                                val uri = Uri.fromParts("package", mContext.packageName, null)
                                                intent.data = uri
                                                mContext.startActivity(intent)
                                            }.show()
                                    }
                                }).check()
                            }
                            editPostDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
                            editPostDialog.findViewById<Button>(R.id.dialogEditPostChangeDataBtnSave).setOnClickListener {
                                val progressDialog = Dialog(mContext)
                                progressDialog.setContentView(R.layout.dialog_progressbar)
                                progressDialog.show()
                                val postContent = editPostDialog.findViewById<EditText>(R.id.dialogEditPostEtContent).text.toString()
                                val storageReference = FirebaseStorage.getInstance().getReference("Posts").child(post.getUserId()).child(post.getId()).child("image").child(post.getId())
                                if (image != null) {
                                    storageReference.putFile(image!!).addOnSuccessListener {
                                        storageReference.downloadUrl.addOnSuccessListener {
                                            val image: String = it.toString()
                                            FirebaseDatabase.getInstance().getReference("Posts").child(post.getUserId()).child(post.getId()).child("content").setValue(postContent)
                                            FirebaseDatabase.getInstance().getReference("Posts").child(post.getUserId()).child(post.getId()).child("image").setValue(image)
                                            editPostDialog.dismiss()
                                            progressDialog.dismiss()
                                        }
                                    }
                                } else {
                                    FirebaseDatabase.getInstance().getReference("Posts").child(post.getUserId()).child(post.getId()).child("content").setValue(postContent)
                                    editPostDialog.dismiss()
                                    progressDialog.dismiss()
                                }
                            }
                            editPostDialog.show()
                        }
                        1 -> {
                            FirebaseDatabase.getInstance().getReference("Posts").child(post.getUserId()).child(post.getId()).removeValue()
                            FirebaseStorage.getInstance().getReference("Posts").child(post.getUserId()).child(post.getId()).child("image").child(post.getImage()).delete()
                        }
                    }
                }.show()
            }
            return@setOnLongClickListener true
        }
        holder.recyclerViewItemPostIvImage.setOnClickListener {
            val dialogViewImage = Dialog(mContext)
            dialogViewImage.setContentView(R.layout.dialog_view_image)
            dialogViewImage.setCancelable(true)
            val ivDialogImage = dialogViewImage.findViewById<ImageView>(R.id.dialogViewImageIvImage)
            ivDialogImage?.setImageDrawable(holder.recyclerViewItemPostIvImage.drawable)
            dialogViewImage.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialogViewImage.window?.setGravity(Gravity.CENTER)
            dialogViewImage.show()
            ivDialogImage.setOnClickListener {
                dialogViewImage.dismiss()
            }
        }
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    inner class ViewHolder(itemView: ViewGroup) : RecyclerView.ViewHolder(itemView) {
        val recyclerViewItemPostIvAvatar: CircleImageView = itemView.findViewById(R.id.recyclerViewItemPostIvAvatar)
        val recyclerViewItemPostTvUsername: TextView = itemView.findViewById(R.id.recyclerViewItemPostTvUsername)
        val recyclerViewItemPostTvTime: TextView = itemView.findViewById(R.id.recyclerViewItemPostTvTime)
        val recyclerViewItemPostTvContent: TextView = itemView.findViewById(R.id.recyclerViewItemPostTvContent)
        val recyclerViewItemPostIvImage: ImageView = itemView.findViewById(R.id.recyclerViewItemPostIvImage)
        val recyclerViewItemPostLlComment: LinearLayout = itemView.findViewById(R.id.recyclerViewItemPostLlComment)
        val recyclerViewItemPostCvPost: CardView = itemView.findViewById(R.id.recyclerViewItemPostCvPost)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }
}
