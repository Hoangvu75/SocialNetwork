package com.example.fakebook.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fakebook.R
import com.example.fakebook.models.Messages
import com.example.fakebook.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(private val mContext: Context, private val mMessageList: List<Messages>, private val mReceiverId: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.layout_sender_chat, parent, false)
                SenderViewHolder(view)
            }
            2 -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.layout_receiver_chat, parent, false)
                ReceiverViewHolder(view)
            }
            else -> {
                null!!
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SenderViewHolder -> {
                holder.senderChatLayoutTvSenderMessage.text = mMessageList[position].getContent()
                holder.senderChatLayoutTvTimeOfMessage.text = mMessageList[position].getTime()
                if (mMessageList[position].getImage() != "" && mMessageList[position].getRemoved() == "false"){
                    Picasso.get().load(mMessageList[position].getImage()).into(holder.senderChatLayoutIvSenderImage)
                    holder.senderChatLayoutIvSenderImage.visibility = View.VISIBLE
                }
                if (mMessageList[position].getContent().toString() == "") {
                    holder.senderChatLayoutTvSenderMessage.visibility = View.GONE
                }
                if (mMessageList[position].getRemoved() == "true"){
                    holder.senderChatLayoutTvSenderMessage.text = "This message was removed"
                    holder.senderChatLayoutTvSenderMessage.setTypeface(holder.senderChatLayoutTvSenderMessage.typeface, Typeface.ITALIC)
                }
                holder.senderChatLayoutLlMessage.setOnLongClickListener {
                    deleteMessage(position)
                    false
                }
                holder.senderChatLayoutIvSenderImage.setOnLongClickListener {
                    deleteMessage(position)
                    false
                }
            }
            is ReceiverViewHolder -> {
                holder.receiverChatLayoutTvReceiverMessage.text = mMessageList[position].getContent()
                holder.receiverChatLayoutTvTimeOfMessage.text = mMessageList[position].getTime()
                FirebaseDatabase.getInstance().reference.child("Users").child(mReceiverId).addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val user = dataSnapshot.getValue(Users::class.java)
                            Picasso.get().load(user?.getAvatar()).into(holder.receiverChatLayoutIvReceiverAvatar)

                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
                if (mMessageList[position].getImage() != "" && mMessageList[position].getRemoved() == "false"){
                    Picasso.get().load(mMessageList[position].getImage()).into(holder.receiverChatLayoutIvReceiverImage)
                    holder.receiverChatLayoutIvReceiverImage.visibility = View.VISIBLE
                }
                if (mMessageList[position].getContent().toString() == "") {
                    holder.receiverChatLayoutTvReceiverMessage.visibility = View.GONE
                }
                if (mMessageList[position].getRemoved() == "true"){
                    holder.receiverChatLayoutTvReceiverMessage.text = "This message was removed"
                    holder.receiverChatLayoutTvReceiverMessage.setTypeface(holder.receiverChatLayoutTvReceiverMessage.typeface, Typeface.ITALIC)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val currentUserId = firebaseUser!!.uid
        return if (mMessageList[position].getSender() == currentUserId) {
            1
        } else {
            2
        }
    }

    private fun deleteMessage(position: Int) {
        if (mMessageList[position].getRemoved() == "false") {
            val dialog = AlertDialog.Builder(mContext)
            dialog.setTitle("Delete Message")
            dialog.setMessage("Are you sure you want to delete this message?")
            dialog.setPositiveButton("Yes") { _, _ ->
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                FirebaseDatabase.getInstance().getReference("Messages").child(firebaseUser!!.uid + mReceiverId)
                    .child(firebaseUser.uid).child(mMessageList[position].getId().toString()).child("removed").setValue("true")
                FirebaseDatabase.getInstance().getReference("Messages").child(mReceiverId + firebaseUser.uid)
                    .child(mReceiverId).child(mMessageList[position].getId().toString()).child("removed").setValue("true")
            }.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }.show()
        }
    }

    inner class SenderViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val senderChatLayoutTvSenderMessage: TextView = itemView.findViewById(R.id.senderChatLayoutTvSenderMessage)
        val senderChatLayoutTvTimeOfMessage: TextView = itemView.findViewById(R.id.senderChatLayoutTvTimeOfMessage)
        val senderChatLayoutIvSenderImage: ImageView = itemView.findViewById(R.id.senderChatLayoutIvSenderImage)
        val senderChatLayoutLlMessage: LinearLayout = itemView.findViewById(R.id.senderChatLayoutLlMessage)
        init {
            senderChatLayoutLlMessage.setOnClickListener {
                if (senderChatLayoutTvTimeOfMessage.visibility == View.GONE) {
                    senderChatLayoutTvTimeOfMessage.visibility = View.VISIBLE
                } else if (senderChatLayoutTvTimeOfMessage.visibility == View.VISIBLE) {
                    senderChatLayoutTvTimeOfMessage.visibility = View.GONE
                }
            }

            senderChatLayoutIvSenderImage.setOnClickListener {
                val dialog = Dialog(mContext)
                dialog.setContentView(R.layout.dialog_view_image)
                dialog.setCancelable(true)
                val ivDialogImage = dialog.findViewById<ImageView>(R.id.dialogViewImageIvImage)
                ivDialogImage?.setImageDrawable(senderChatLayoutIvSenderImage.drawable)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.window?.setGravity(Gravity.CENTER)
                dialog.show()
                ivDialogImage.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }

    inner class ReceiverViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val receiverChatLayoutTvReceiverMessage: TextView = itemView.findViewById(R.id.receiverChatLayoutTvReceiverMessage)
        val receiverChatLayoutTvTimeOfMessage: TextView = itemView.findViewById(R.id.receiverChatLayoutTvTimeOfMessage)
        val receiverChatLayoutIvReceiverImage: ImageView = itemView.findViewById(R.id.receiverChatLayoutIvReceiverImage)
        val receiverChatLayoutIvReceiverAvatar: CircleImageView = itemView.findViewById(R.id.receiverChatLayoutIvReceiverAvatar)
        val receiverChatLayoutLlMessage: LinearLayout = itemView.findViewById(R.id.receiverChatLayoutLlMessage)
        init {
            receiverChatLayoutLlMessage.setOnClickListener {
                if (receiverChatLayoutTvTimeOfMessage.visibility == View.GONE) {
                    receiverChatLayoutTvTimeOfMessage.visibility = View.VISIBLE
                } else if (receiverChatLayoutTvTimeOfMessage.visibility == View.VISIBLE) {
                    receiverChatLayoutTvTimeOfMessage.visibility = View.GONE
                }
            }

            receiverChatLayoutIvReceiverImage.setOnClickListener {
                val dialog = Dialog(mContext)
                dialog.setContentView(R.layout.dialog_view_image)
                dialog.setCancelable(true)
                val ivDialogImage = dialog.findViewById<ImageView>(R.id.dialogViewImageIvImage)
                ivDialogImage?.setImageDrawable(receiverChatLayoutIvReceiverImage.drawable)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.window?.setGravity(Gravity.CENTER)
                dialog.show()
                ivDialogImage.setOnClickListener {
                    dialog.dismiss()
                }
            }
        }
    }
}