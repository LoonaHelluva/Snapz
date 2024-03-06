package com.example.snapz.Classes

import android.content.Context
import android.util.Log
import com.example.snapz.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text

class FireHelper {
    companion object {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val Chats = FirebaseDatabase.getInstance().getReference("Chats")
        val Users = FirebaseDatabase.getInstance().getReference("Users")


        fun createNewUser(email: String, password: String, name: String, context: Context) {
            if (email.trim() != "" &&
                name.trim() != "" &&
                password.trim() != ""
            ) {

                auth.createUserWithEmailAndPassword(email.trim(), password.trim())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = UserModel(
                                id = auth.uid!!,
                                name = name.trim(),
                                email = email.trim(),
                                profileImage = "https://png.pngtree.com/png-clipart/20191120/original/pngtree-outline-user-icon-png-image_5045523.jpg"
                            )

                            Users.child(user.id).setValue(user).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Log.i("Realtime", "New user added successfully")
                                }
                            }
                        } else {
                            Log.e("Auth", it.exception.toString())
                        }
                    }
            }
        }

        fun createNewChat(id: String, name: String, image: String) {
            val chat = ChatModel(
                id,
                name,
                image
            )

            Chats.child(chat.id).setValue(chat).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Chat", "New chat created")
                    Chat.exist = true
                } else {
                    Log.e("Chat", it.exception.toString())
                }
            }
        }

        fun isChatExist(id: String) {
            Chats.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    for (i in it.result.children) {
                        val chat = i.getValue(ChatModel::class.java)

                        if (chat != null && chat.id == id) {
                            Chat.exist = true
                            return@addOnCompleteListener
                        }
                    }
                } else {
                    Log.e("Chat", it.exception.toString())
                }
            }
        }

        fun sendMessage(message: String, chatId: String, type: String = "Text", link: String = "") {

            val Messages = Chats.child(chatId).child("Messages")

            val mymessage: MessageModel = MessageModel(
                id = Messages.push().key.toString(),
                type = type,
                link = link,
                text = message,
                sender = user!!.uid
            )

            Chats.child(chatId).child("Messages").child(mymessage.id).setValue(mymessage).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Message", "Message sent")
                } else {
                    Log.e("Message", it.exception.toString())
                }
            }
        }
    }
}