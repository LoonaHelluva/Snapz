package com.example.snapz

import android.os.Bundle
import android.os.Message
import android.widget.Adapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snapz.Classes.FireHelper
import com.example.snapz.Classes.MessageModel
import com.example.snapz.Classes.UserModel
import com.example.snapz.adapters.MessageAdapter
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class Chat : AppCompatActivity() {

    //Views
    lateinit var chatName: TextView
    lateinit var rvMessages: RecyclerView

    lateinit var messageLay: ConstraintLayout
    lateinit var chooseFile: ImageButton
    lateinit var etMessage: EditText
    lateinit var ibSendMessage: ImageButton

    lateinit var messageEditLay: ConstraintLayout
    lateinit var etMessageEdit: EditText
    lateinit var ibEditDone: ImageButton

    lateinit var adapter: MessageAdapter

    //Users
    lateinit var chatWithId: String
    lateinit var chatWithName: String
    lateinit var chatWithImage: String
    lateinit var me: UserModel

    //Chat
    lateinit var chatId: String
    val messages = arrayListOf<MessageModel>()

    companion object{
        var exist = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Initializing views
        chatName = findViewById(R.id.tvChatName)
        rvMessages = findViewById(R.id.rvMessages)

        messageLay = findViewById(R.id.messageSendLay)
        chooseFile = findViewById(R.id.ibChooseFile)
        etMessage = findViewById(R.id.etMessage)
        ibSendMessage = findViewById(R.id.ibSendMessage)

        messageEditLay = findViewById(R.id.messageEditLay)
        etMessageEdit = findViewById(R.id.etEditMessage)
        ibEditDone = findViewById(R.id.ibEditDone)

        adapter = MessageAdapter(messages)

        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = adapter

        //Users
        val intent = intent
        chatWithId = intent.getStringExtra("userId").toString()
        chatWithName = intent.getStringExtra("userName").toString()
        chatWithImage = intent.getStringExtra("userImage").toString()

        chatId = if(FireHelper.user!!.uid.hashCode() < chatWithId.hashCode()){
            FireHelper.user.uid + chatWithId
        } else{
            chatWithId + FireHelper.user.uid
        }

        chatName.text = chatWithName

        //Getting me
        getMe()

        //On Click listeners

        ibSendMessage.setOnClickListener{
            if(exist){
                if(etMessage.text.toString().trim() != ""){
                    FireHelper.sendMessage(etMessage.text.toString().trim(), chatId)
                }
                else{
                    Toast.makeText(this, "Field can't be empty", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                FireHelper.createNewChat(chatId, me.name+chatWithName, chatWithImage+me.profileImage)
            }
        }

        //Firebase listener

        FireHelper.Chats.child(chatId).child("Messages").addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(MessageModel::class.java)

                if(message != null){
                    messages.add(message)

                    adapter.notifyItemInserted(messages.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    fun getMe(){
        FireHelper.Users.child(FireHelper.user!!.uid).get().addOnCompleteListener {
            if(it.isSuccessful){
                val user = it.result.getValue(UserModel::class.java)

                if(user != null && user.id == FireHelper.user.uid){
                    me = user

                    FireHelper.isChatExist(chatId)

                    return@addOnCompleteListener
                }
            }
        }
    }
}