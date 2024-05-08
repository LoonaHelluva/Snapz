package com.example.snapz.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapz.Classes.ChatModel
import com.example.snapz.Classes.FireHelper as fireHelper
import com.example.snapz.Classes.UserModel
import com.example.snapz.R
import com.example.snapz.adapters.ChatsAdapter
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.example.snapz.Classes.FireHelper.Companion.me

class ChatsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }


    //Views
    lateinit var userName: TextView
    lateinit var userImage: ImageView
    lateinit var chatsRv : RecyclerView

    companion object{
        //ChatList
        var chats: MutableList<ChatModel> = mutableListOf()
        lateinit var adapter: ChatsAdapter


        //Variable to know if data is loaded
        var isLoaded = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName = view.findViewById(R.id.chatsUserName)
        userImage = view.findViewById(R.id.chatsImage)
        chatsRv = view.findViewById(R.id.chatsRV)
        chatsRv.layoutManager = LinearLayoutManager(view.context)
        adapter = ChatsAdapter(chats, me)
        chatsRv.adapter = adapter

        if(!isLoaded) {
            getMe()
        }
        else{
            userName.text = me.name

            Glide.with(requireContext()).load(me.profileImage).into(userImage)
        }


        fireHelper.Chats.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?){
                val chat = snapshot.getValue(ChatModel::class.java)

                if(chat != null && isMyNameExist(chat.name)){
                    chat.name = if(chat.name.contains("${me.name}+")){chat.name.replace("${me.name}+", "")}
                                else{chat.name.replace("+${me.name}", "")}
                    chats.add(chat)

                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                loadChats()
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onResume() {
        super.onResume()

        chats.clear()
    }

    fun isMyNameExist(name: String) : Boolean{
        val names = mutableListOf<String>()
        var curName: String = ""

        //Separating the names and + symbols
        for(i in name.indices){
            if(name[i] != '+'){
                curName += name[i]
            }
            else{
                names.add("+")
                names.add(curName)
                curName = ""
            }
            if((i + 1) == name.length){
                names.add(curName)
            }
        }

        //Looking for my name in chat name and changing it to my new name
        for (i in names){
            if(i == me.name){
                return  true
            }
        }

        return false
    }

    fun loadChats(){
        fireHelper.Chats.get().addOnCompleteListener {
            if(it.isSuccessful){
                for(i in it.result.children){
                    val chat = i.getValue(ChatModel::class.java)

                    if(chat != null && fireHelper.isMyNameExist(chat.name, me)){
                        chat.image = chat.image.replace(me.profileImage, "")
                        chat.name = if(chat.name.contains("${me.name}+")){
                            chat.name.replace("${me.name}+", "").toString()
                        }
                        else{
                            chat.name.replace("+${me.name}", "")
                        }
                        chats.add(chat)
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun getMe(){
        fireHelper.Users.child(fireHelper.user!!.uid).get().addOnCompleteListener {
            if(it.isSuccessful){
                val user = it.result.getValue(UserModel::class.java)

                if(user != null && user.id == fireHelper.user!!.uid){
                    me = user
                    loadChats()
                    userName.setText(me.name)

                    Glide.with(requireContext()).load(me.profileImage).into(userImage)
                }
            }
        }
    }
}