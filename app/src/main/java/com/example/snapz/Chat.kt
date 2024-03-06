package com.example.snapz

import android.os.Bundle
import android.os.Message
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.snapz.Classes.MessageModel
import com.example.snapz.Classes.UserModel

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

    //Users
    lateinit var chatWithId: String
    lateinit var chatWithName: String
    lateinit var me: UserModel

    //Chat
    lateinit var chatId: String
    val messages = arrayListOf<MessageModel>()

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

        //Users
        val intent = intent
        chatWithId = intent.getStringExtra("userId").toString()
        chatWithName = intent.getStringExtra("userName").toString()

        chatName.text = chatWithName


    }
}