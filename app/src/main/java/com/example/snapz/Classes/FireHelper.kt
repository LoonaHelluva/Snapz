package com.example.snapz.Classes

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat.startActivity
import com.example.snapz.Chat
import com.example.snapz.MainActivity
import com.example.snapz.fragments.SettingsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class FireHelper {
    companion object {
        val auth = FirebaseAuth.getInstance()
        var user = auth.currentUser

        val Chats = FirebaseDatabase.getInstance().getReference("Chats")
        val Users = FirebaseDatabase.getInstance().getReference("Users")
        val Storage = FirebaseStorage.getInstance().getReference()

        var me = UserModel()

        fun deleteMessage(message: MessageModel, chatId: String){
            val messageRef = Chats.child(chatId).child("Messages").child(message.id)

            messageRef.removeValue()
        }

        fun editMessage(message: MessageModel, chatId: String, newMessage: String){
            val messageRef = Chats.child(chatId).child("Messages").child(message.id)

            messageRef.child("text").setValue(newMessage)
        }

        fun deleteImage(image: MessageModel, chatId: String){
            val realtime = Chats.child(chatId).child("Messages").child(image.id)
            realtime.removeValue()

            val storage = FirebaseStorage.getInstance().getReferenceFromUrl(image.link)
            storage.delete().addOnCompleteListener {
                if(it.isSuccessful){
                    Log.d("Image", "Deleted successfuly")
                }
            }
        }

        fun uploadFileToStorage(context: Context, uri: Uri, chatId: String = "", sender: String = ""){
            val imageRef = Storage.child(fileName(context, uri))
            val realtime = Chats.child(chatId).child("Messages")

            val empty = MessageModel(
                id =  realtime.push().key.toString(),
                type = "Image",
                link = "Loading",
                text = "",
                sender = sender)
            realtime.child(empty.id).setValue(empty)

            val uploadTask = imageRef.putFile(uri).continueWithTask { task ->
                if(!task.isSuccessful){
                    task.exception.let { throw it!! }
                }

                return@continueWithTask imageRef.downloadUrl
            }.addOnCompleteListener {
                if(it.isSuccessful){
                    val message = MessageModel(
                        id =  empty.id,
                        type = "Image",
                        link = it.result.toString(),
                        text = "",
                        sender = sender)

                    realtime.child(message.id).setValue(message)
                }
            }
        }

        fun getType(context: Context, uri: Uri) : String{
            val contentResolver: ContentResolver = context.contentResolver
            return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri)).toString()
        }

        fun fileName(context: Context, uri:Uri) : String{
            val name = System.currentTimeMillis().toString()
            val type = getType(context, uri)

            return name + "." + type
        }

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
                                    val intent = Intent(context, MainActivity::class.java)
                                    startActivity(context, intent, null)
                                    FireHelper.user = FirebaseAuth.getInstance().currentUser
                                    me = user
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
                            Log.d("CHAT EXIST" ,"CHAT EXISTTTTTT")
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
                    Chat.scroll.fullScroll(View.FOCUS_DOWN)
                } else {
                    Log.e("Message", it.exception.toString())
                }
            }
        }

        fun isMyNameExist(name: String, me : UserModel) : Boolean{
            val names = mutableListOf<String>()
            var curName: String = ""

            //Separating the names and + symbols
            for(i in name.indices){
                if(name[i] != '+'){
                    Log.i("I", name[i].toString())
                    curName += name[i]
                }
                else{
                    names.add("+")
                    names.add(curName)
                    Log.i("CURNAME", curName)
                    curName = ""
                }
                if((i + 1) == name.length){
                    Log.i("CURNAME", curName)
                    names.add(curName)
                }
            }

            //Looking for my name in chat name and changing it to my new name
            for (i in names){
                Log.i("NAMEIIIIII", i)
                if(i == me.name){
                    return  true
                }
            }

            return false
        }

    }
}