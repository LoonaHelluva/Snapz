package com.example.snapz.Classes

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FireHelper {
    companion object{
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val Chats = FirebaseDatabase.getInstance().getReference("Chats")
        val Users = FirebaseDatabase.getInstance().getReference("Users")



        fun createNewUser(email: String, password: String, name: String, context: Context){
            if(email.trim() != "" &&
                name.trim() != "" &&
                password.trim() != ""){

                auth.createUserWithEmailAndPassword(email.trim(), password.trim()).addOnCompleteListener{
                    if(it.isSuccessful){
                        val user = UserModel(
                            id = auth.uid!!,
                            name = name.trim(),
                            email = email.trim(),
                            profileImage = "https://png.pngtree.com/png-clipart/20191120/original/pngtree-outline-user-icon-png-image_5045523.jpg"
                        )

                        Users.child(user.id).setValue(user).addOnCompleteListener {
                            if(it.isSuccessful){
                                Log.i("Realtime", "New user added successfully")
                            }
                        }
                    }
                    else{
                        Log.e("Auth", it.exception.toString())
                    }
                }
            }
        }
    }
}