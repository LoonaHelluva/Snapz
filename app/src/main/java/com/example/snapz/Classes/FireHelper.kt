package com.example.snapz.Classes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FireHelper {
    companion object{
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val Chats = FirebaseDatabase.getInstance().getReference("Chats")
        val Users = FirebaseDatabase.getInstance().getReference("Users")
    }
}