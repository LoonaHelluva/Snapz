package com.example.snapz.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapz.Classes.FireHelper
import com.example.snapz.Classes.UserModel
import com.example.snapz.R
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

    //Me
    var me = UserModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userName = view.findViewById(R.id.chatsUserName)
        userImage = view.findViewById(R.id.chatsImage)
        chatsRv = view.findViewById(R.id.chatsRV)


    }

    fun getMe(){
        FireHelper.Users.child(FireHelper.user!!.uid).get().addOnCompleteListener {
            if(it.isSuccessful){
                val user = it.result.getValue(UserModel::class.java)

                if(user != null && user.id == FireHelper.user!!.uid){
                    me = user

                    userName.setText(me.name)

                    Glide.with(requireContext()).load(me.profileImage).into(userImage)
                }
            }
        }
    }
}