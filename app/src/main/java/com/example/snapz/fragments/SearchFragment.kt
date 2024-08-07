package com.example.snapz.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapz.Classes.FireHelper
import com.example.snapz.Classes.UserModel
import com.example.snapz.R
import com.example.snapz.adapters.SearchAdapter
import com.example.snapz.Classes.FireHelper.Companion.me
import java.util.Locale

class SearchFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }



    //Views
    lateinit var userImage: ImageView
    lateinit var userName: TextView
    lateinit var rvUsers: RecyclerView
    lateinit var btSearch: ImageButton
    lateinit var search: EditText

    //List of users
    val users: ArrayList<UserModel> = arrayListOf()

    //Adapter
    lateinit var adapter: SearchAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize views
        userImage = view.findViewById(R.id.searchUserImage)
        userName = view.findViewById(R.id.tvUserName)
        rvUsers = view.findViewById(R.id.rvSearch)
        btSearch = view.findViewById(R.id.searchButton)
        search = view.findViewById(R.id.searchName)

        //Recycler view and adapter initialization
        adapter = SearchAdapter(requireView(), users)
        rvUsers.layoutManager = LinearLayoutManager(view.context)
        rvUsers.adapter = adapter

        //Getting user (me)
        getMe(view)

        //OnClick listeners
        btSearch.setOnClickListener {
            if(search.text.toString().trim() != ""){
                getUsers(search.text.toString().toLowerCase(Locale.ROOT).trim())
            }
            else{
                Toast.makeText(requireContext(), "Search can't be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Function getting user (me) data
    fun getMe(view:View){
        if(me.id != ""){
            Glide.with(view.context).load(me.profileImage).into(userImage)
            userName.text = me.name
        }
        else {
            FireHelper.Users.child(FireHelper.user!!.uid).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val me = it.result.getValue(UserModel::class.java)

                    if (me != null) {
                        Glide.with(view.context).load(me.profileImage).into(userImage)

                        userName.text = me.name
                    }
                }
                else {
                    Log.e("Search", it.exception.toString())
                }
            }.addOnFailureListener{
                Log.e("Search", it.message.toString())
            }
        }
    }

    //Function getting users i'm looking for
    fun getUsers(name: String){
        FireHelper.Users.get().addOnCompleteListener {
            if(it.isSuccessful){
                for(i in it.result.children){
                    val user = i.getValue(UserModel::class.java)

                    if(user != null && user.id != FireHelper.user!!.uid && user.name.toLowerCase(Locale.ROOT).contains(name)){
                        users.add(user)
                        adapter.notifyItemInserted(users.size - 1)
                    }
                }
                rvUsers.adapter = SearchAdapter(requireView(), users)
            }
        }
    }
}