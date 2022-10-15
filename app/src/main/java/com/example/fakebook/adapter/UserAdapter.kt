package com.example.fakebook.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.fakebook.R
import com.example.fakebook.activities.ChatActivity
import com.example.fakebook.activities.MainActivity
import com.example.fakebook.activities.OtherUserProfileActivity
import com.example.fakebook.models.Users
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val mContext: Context, private val mUsers: List<Users>) : RecyclerView.Adapter<UserAdapter.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUsers[position]
        holder.recyclerViewItemUserTvUsername.text = user.getUsername()
        Picasso.get().load(user.getAvatar()).into(holder.recyclerViewItemUserIvAvatar)
        holder.recyclerViewItemUserCvItem.setOnClickListener {
            val mainActivity = mContext as MainActivity
            val mainActivityTabLayout = mainActivity.findViewById<TabLayout>(R.id.mainActivityTabLayout)
            val tabLayoutPosition = mainActivityTabLayout.selectedTabPosition
            if (tabLayoutPosition == 2) {
                val dialog = Dialog(mContext)
                dialog.setContentView(R.layout.dialog_search_user)
                dialog.setCancelable(true)
                Picasso.get().load(user.getCover()).into(dialog.findViewById<ImageView>(R.id.dialogSearchUserIvCover))
                Picasso.get().load(user.getAvatar()).into(dialog.findViewById<CircleImageView>(R.id.dialogSearchUserIvAvatar))
                dialog.findViewById<TextView>(R.id.dialogSearchUserTvUsername).text = user.getUsername()
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                dialog.show()

                dialog.findViewById<CircleImageView>(R.id.dialogSearchUserIvFacebookIcon).setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.getFacebook()))
                    mContext.startActivity(intent)
                }

                dialog.findViewById<CircleImageView>(R.id.dialogSearchUserIvInstagramIcon).setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.getInstagram()))
                    mContext.startActivity(intent)
                }

                dialog.findViewById<CircleImageView>(R.id.dialogSearchUserIvTiktokIcon).setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(user.getTiktok()))
                    mContext.startActivity(intent)
                }

                dialog.findViewById<Button>(R.id.dialogSearchUserBtnViewProfile).setOnClickListener {
                    val intent = Intent(mContext, OtherUserProfileActivity::class.java)
                    intent.putExtra("otherUserId", user.getUID())
                    mContext.startActivity(intent)
                    dialog.dismiss()
                }

                dialog.findViewById<Button>(R.id.dialogSearchUserBtnSendMessage).setOnClickListener {
                    val intent = Intent(mContext, ChatActivity::class.java)
                    intent.putExtra("otherUserId", user.getUID())
                    mContext.startActivity(intent)
                    dialog.dismiss()
                }
            }
            if (tabLayoutPosition == 1) {
                val intent = Intent(mContext, OtherUserProfileActivity::class.java)
                intent.putExtra("otherUserId", user.getUID())
                mContext.startActivity(intent)
            }
            if (tabLayoutPosition == 3) {
                val intent = Intent(mContext, ChatActivity::class.java)
                intent.putExtra("otherUserId", user.getUID())
                mContext.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerViewItemUserIvAvatar: CircleImageView = itemView.findViewById(R.id.recyclerViewItemUserIvAvatar)
        val recyclerViewItemUserTvUsername: TextView = itemView.findViewById(R.id.recyclerViewItemUserTvUsername)
        val recyclerViewItemUserCvItem: CardView = itemView.findViewById(R.id.recyclerViewItemUserCvItem)
    }
}