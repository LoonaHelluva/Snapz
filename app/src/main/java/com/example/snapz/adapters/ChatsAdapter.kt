package com.example.snapz.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapz.Chat
import com.example.snapz.Classes.ChatModel
import com.example.snapz.Classes.UserModel
import com.example.snapz.R
import java.util.zip.Inflater

class ChatsAdapter(chats: MutableList<ChatModel>, me: UserModel) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    val chats = chats
    val me = me

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val chatImage : ImageView = itemView.findViewById(R.id.chatImage)
        val chatName: TextView = itemView.findViewById(R.id.chatName)
        val lay: ConstraintLayout = itemView.findViewById(R.id.chatsConstraintLay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chats[position]

        holder.chatName.text = chat.name
        Glide.with(holder.itemView.context).load(chat.image).into(holder.chatImage)

        holder.lay.setOnClickListener{
            val intent = Intent(holder.itemView.context, Chat::class.java)

            intent.putExtra("chatId", chat.id)
            intent.putExtra("chatName", chat.name)
            intent.putExtra("meName", me.name)
            intent.putExtra("meId", me.id)
            intent.putExtra("meImage", me.profileImage)

            startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

}