package com.example.fakebook.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fakebook.R
import com.example.fakebook.adapter.MessageAdapter
import com.example.fakebook.models.Messages
import com.example.fakebook.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private var firebaseUser: FirebaseUser? = null

    private var imageUri: Uri? = null

    private var messageAdapter: MessageAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var mMessageList: List<Messages>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        recyclerView = findViewById(R.id.chatActivityRvMessageList)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        mMessageList = ArrayList()

        showMessages()

        setData()
    }

    private fun setData() {
        val otherUserId = intent.getStringExtra("otherUserId")
        setSupportActionBar(chatActivityToolbar)
        FirebaseDatabase.getInstance().reference.child("Users").child(otherUserId!!)
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: Users? = dataSnapshot.getValue(Users::class.java)
                    supportActionBar?.title = user?.getUsername()
                    chatActivityTvUserStatus.text = user?.getStatus()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Error", error.toString())
                }
            })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        chatActivityToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        FirebaseDatabase.getInstance().reference.child("Users").child(otherUserId).child("status")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot1: DataSnapshot) {
                    FirebaseDatabase.getInstance().reference.child("Messages").child(intent.getStringExtra("otherUserId")!! + firebaseUser!!.uid)
                        .child("seen").addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot2: DataSnapshot) {
                                if (dataSnapshot2.value == null) {
                                    FirebaseDatabase.getInstance().reference.child("Messages").child(intent.getStringExtra("otherUserId")!! + firebaseUser!!.uid)
                                        .child("seen").setValue("Not seen")
                                }
                                val status = "${dataSnapshot1.value.toString()}\n${dataSnapshot2.value.toString()}"
                                chatActivityTvUserStatus.text = status
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Error", error.toString())
                            }
                        })

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Error", error.toString())
                }
            })

        FirebaseDatabase.getInstance().reference.child("Messages").child(firebaseUser!!.uid + intent.getStringExtra("otherUserId")!!)
            .child("seen").setValue("Last seen: " + SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date()))
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                FirebaseDatabase.getInstance().reference.child("Messages").child(firebaseUser!!.uid + intent.getStringExtra("otherUserId")!!)
                    .child("seen").setValue("Last seen: " + SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date()))
                handler.postDelayed(this, 100)
            }
        }
        handler.postDelayed(runnable, 100)
    }

    private fun showMessages(){
        val messagesRef = FirebaseDatabase.getInstance().reference.child("Messages")
            .child(firebaseUser!!.uid + intent.getStringExtra("otherUserId")).child(firebaseUser!!.uid)
        messagesRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (mMessageList as ArrayList<Messages>).clear()
                for (snapshot in dataSnapshot.children){
                    val message: Messages? = snapshot.getValue(Messages::class.java)
                    (mMessageList as ArrayList<Messages>).add(message!!)
                }
                messageAdapter = MessageAdapter(this@ChatActivity, mMessageList!!, intent.getStringExtra("otherUserId").toString())
                recyclerView?.adapter = messageAdapter
                recyclerView?.scrollToPosition(mMessageList!!.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error", error.toString())
            }
        })
    }

    fun chatActivityTvSendImageOnClick(view: View) {
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
                AlertDialog.Builder(this@ChatActivity)
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

    fun chatActivityTvSendButtonOnClick(view: View) {
        val messageContent: String = chatActivityEtSendMessage.text.toString()
        val messageSender: String = firebaseUser?.uid.toString()
        val messageReceiver: String = intent.getStringExtra("otherUserId").toString()
        val messageTime: String = SimpleDateFormat("dd-MM-yyyy, HH:mm", Locale.getDefault()).format(Date())
        val messageRemoved: String = "false"
        val messageId: String = System.currentTimeMillis().toString()
        val imageRef = FirebaseStorage.getInstance().getReference("Message Images").child(UUID.randomUUID().toString())

        if (chatActivityIvImageSend.visibility != View.GONE || messageContent != "") {
            val messageSenderRef = FirebaseDatabase.getInstance().reference.child("Messages")
                .child(firebaseUser!!.uid + intent.getStringExtra("otherUserId")!!).child(firebaseUser!!.uid)
            val messageReceiverRef = FirebaseDatabase.getInstance().reference.child("Messages")
                .child(intent.getStringExtra("otherUserId")!! + firebaseUser!!.uid).child(intent.getStringExtra("otherUserId")!!)

            if (imageUri != null) {
                val progressDialog = Dialog(this)
                progressDialog.setContentView(R.layout.dialog_progressbar)
                progressDialog.show()
                imageRef.putFile(imageUri!!).addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener {
                        val message = HashMap<String, Any>()
                        message["content"] = messageContent
                        message["sender"] = messageSender
                        message["receiver"] = messageReceiver
                        message["time"] = messageTime
                        message["removed"] = messageRemoved
                        message["id"] = messageId
                        message["image"] = it.toString()

                        messageSenderRef.child(messageId).setValue(message)
                        messageReceiverRef.child(messageId).setValue(message)

                        chatActivityEtSendMessage.text.clear()
                        chatActivityIvImageSend.visibility = View.GONE
                        imageUri = null
                        progressDialog.dismiss()
                    }
                }
            } else if (imageUri == null) {
                val message = HashMap<String, Any>()
                message["content"] = messageContent
                message["sender"] = messageSender
                message["receiver"] = messageReceiver
                message["time"] = messageTime
                message["removed"] = messageRemoved
                message["id"] = messageId
                message["image"] = ""

                messageSenderRef.child(messageId).setValue(message)
                messageReceiverRef.child(messageId).setValue(message)

                chatActivityEtSendMessage.text.clear()
                chatActivityIvImageSend.visibility = View.GONE
            }

            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: Users? = dataSnapshot.getValue(Users::class.java)
                    FirebaseDatabase.getInstance().reference.child("BoxChat").child(intent.getStringExtra("otherUserId")!!).child("receiver").child(firebaseUser!!.uid).setValue(user)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Error", error.toString())
                }
            })
            FirebaseDatabase.getInstance().reference.child("Users").child(intent.getStringExtra("otherUserId")!!).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: Users? = dataSnapshot.getValue(Users::class.java)
                    FirebaseDatabase.getInstance().reference.child("BoxChat").child(firebaseUser!!.uid).child("receiver").child(intent.getStringExtra("otherUserId")!!).setValue(user)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("Error", error.toString())
                }
            })
        } else {
            Toast.makeText(this, "Please enter message or select image", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            chatActivityIvImageSend.setImageURI(imageUri)
            chatActivityIvImageSend.visibility = View.VISIBLE
        }
    }
}