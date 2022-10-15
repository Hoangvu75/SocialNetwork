package com.example.fakebook.adapter

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fakebook.R
import com.example.fakebook.models.Comments
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(private val mContext: Context, private val mComments: List<Comments>) : RecyclerView.Adapter<CommentAdapter.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = mComments[position]
        Picasso.get().load(comment.getAvatar()).into(holder.dialogCommentItemIvAvatar)
        holder.dialogCommentItemTvUsername.text = comment.getUsername()
        holder.dialogCommentItemTvTime.text = comment.getTime()
        if (comment.getContent() != "") {
            holder.dialogCommentItemTvContent.visibility = View.VISIBLE
            holder.dialogCommentItemTvContent.text = comment.getContent()
        }
        if (comment.getImage().isNotEmpty()) {
            holder.dialogCommentItemIvImage.visibility = View.VISIBLE
            Picasso.get().load(comment.getImage()).into(holder.dialogCommentItemIvImage)
        }
        holder.dialogCommentItemIvImage.setOnClickListener {
            val dialogViewImage = Dialog(mContext)
            dialogViewImage.setContentView(R.layout.dialog_view_image)
            dialogViewImage.setCancelable(true)
            val ivDialogImage = dialogViewImage.findViewById<ImageView>(R.id.dialogViewImageIvImage)
            ivDialogImage?.setImageDrawable(holder.dialogCommentItemIvImage.drawable)
            dialogViewImage.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialogViewImage.window?.setGravity(Gravity.CENTER)
            dialogViewImage.show()
            ivDialogImage.setOnClickListener {
                dialogViewImage.dismiss()
            }
        }
    }

    override fun getItemCount(): Int {
        return mComments.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dialogCommentItemIvAvatar: CircleImageView = itemView.findViewById(R.id.dialogCommentItemIvAvatar)
        val dialogCommentItemTvUsername: TextView = itemView.findViewById(R.id.dialogCommentItemTvUsername)
        val dialogCommentItemTvTime: TextView = itemView.findViewById(R.id.dialogCommentItemTvTime)
        val dialogCommentItemTvContent: TextView = itemView.findViewById(R.id.dialogCommentItemTvContent)
        val dialogCommentItemIvImage: ImageView = itemView.findViewById(R.id.dialogCommentItemIvImage)
    }
}