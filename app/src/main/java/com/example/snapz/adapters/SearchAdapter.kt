package com.example.snapz.adapters

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapz.Classes.User
import com.example.snapz.R

class SearchAdapter(view: View, users: ArrayList<User>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>(){

    val users = users

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val userImage = itemView.findViewById<ImageView>(R.id.searchUserImage)
        val userName = itemView.findViewById<TextView>(R.id.searchUserName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.d("User", "Got user ${users[position].name}")
        Glide.with(holder.itemView.context).load(users[position].profileImage).into(holder.userImage)
        holder.userName.text = users[position].name
    }

}