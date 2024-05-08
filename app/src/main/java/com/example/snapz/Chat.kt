package com.example.snapz

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
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
import com.example.snapz.adapters.OnLongCLickListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.text.FieldPosition
import com.example.snapz.Classes.FireHelper.Companion.me

class Chat : AppCompatActivity() {

    //Views
    lateinit var chatName: TextView
    lateinit var rvMessages: RecyclerView

    lateinit var chooseFile: ImageButton
    lateinit var etMessage: EditText
    lateinit var ibSendMessage: ImageButton

    lateinit var etMessageEdit: EditText
    lateinit var ibEditDone: ImageButton

    lateinit var adapter: MessageAdapter

    //Users
    lateinit var chatWithId: String
    lateinit var chatWithName: String
    lateinit var chatWithImage: String

    //Chat
    lateinit var chatId: String

    var messages = mutableListOf<MessageModel>()

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

        chooseFile = findViewById(R.id.ibChooseFile)
        etMessage = findViewById(R.id.etMessage)
        ibSendMessage = findViewById(R.id.ibSendMessage)

        etMessageEdit = findViewById(R.id.etEditMessage)
        ibEditDone = findViewById(R.id.ibEditDone)

        me.id = FireHelper.user!!.uid

        //Getting users
        if(intent.getStringExtra("chatId") != null){
            Log.d("CHATTTT", "GOT INTO CHAT FORM THE CHATS PAGE")
            chatId = intent.getStringExtra("chatId").toString()

            FireHelper.isChatExist(chatId)

            if(me.id == "") {
                me.name = intent.getStringExtra("meName").toString()
                me.id = intent.getStringExtra("meId").toString()
                me.profileImage = intent.getStringExtra("meImage").toString()
            }

            chatWithId = intent.getStringExtra("chatId").toString()
            chatWithName = intent.getStringExtra("chatName").toString()
            chatWithImage = intent.getStringExtra("userImage").toString()

            chatWithId = chatWithId.replace(me.id, "")
            chatWithName = if(chatWithName.contains("${me.name}+")){
                chatWithName.replace("${me.name}+", "").toString()
            }
            else{
                chatWithName.replace("+${me.name}", "")
            }

            chatWithImage.replace(me.profileImage, "")

            chatId = if(FireHelper.user!!.uid.hashCode() < chatWithId.hashCode()){
                FireHelper.user!!.uid + chatWithId
            } else{
                chatWithId + FireHelper.user!!.uid
            }

            chatName.text = chatWithName
        }
        else{

            Log.d("CHATTTT", "GOT INTO CHAT FORM THE SEARCH PAGE")
            chatWithId = intent.getStringExtra("userId").toString()
            chatWithName = intent.getStringExtra("userName").toString()
            chatWithImage = intent.getStringExtra("userImage").toString()

            chatName.setText(chatWithName)

            chatId = if(me.id.hashCode() < chatWithId.hashCode()){
                me.id + chatWithId
            }
            else{
                chatWithId + me.id
            }

            FireHelper.isChatExist(chatId)

            //Getting me
            if(me.id == "") {
                getMe()
            }
        }

        adapter = MessageAdapter(messages, object : OnLongCLickListener{
            override fun onLongClickMessageLIstener(position: Int, view: View) {
                messageLongListener(position, view)
            }

            override fun onLognClickImageListener(position: Int, view: View) {
                imageLongListener(position, view)
            }

        })

        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = adapter


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
                FireHelper.createNewChat(chatId, "${me.name}+${chatWithName}", chatWithImage+me.profileImage)
            }
        }

        chooseFile.setOnClickListener { getFile() }
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
                refreshMessages()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                refreshMessages()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data?.data != null){
            FireHelper.uploadFileToStorage(this, data.data!!, chatId, me.id)
        }
    }

    fun getFile(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"

        startActivityForResult(intent, 1)
    }

    fun getMe(){
        FireHelper.Users.child(FireHelper.user!!.uid).get().addOnCompleteListener {
            if(it.isSuccessful){
                val user = it.result.getValue(UserModel::class.java)

                if(user != null && user.id == FireHelper.user!!.uid){
                    me = user

                    return@addOnCompleteListener
                }
            }
        }
    }

    fun imageLongListener(position: Int, view: View) {
        val popImageMenu = PopupMenu(this, view)

        popImageMenu.menuInflater.inflate(R.menu.iamge_long_menu, popImageMenu.menu)

        popImageMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.imageDelete ->{
                    FireHelper.deleteImage(messages[position], chatId)
                    true
                }
                else -> {
                    true
                }
            }
        }

        popImageMenu.show()
    }

    fun messageLongListener(position: Int, view: View){
        val popMessageMenu = PopupMenu(this, view)
        popMessageMenu.menuInflater.inflate(R.menu.message_long_menu, popMessageMenu.menu)

        popMessageMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.Edit ->{

                    hideSendLay()

                    showEditLay()

                    etMessageEdit.setText(messages[position].text)

                    ibEditDone.setOnClickListener {
                        val message = etMessageEdit.text.toString().trim()
                        if(message != ""){
                            if(message != messages[position].text){
                                FireHelper.editMessage(messages[position], chatId, message)
                            }
                        }
                        else{
                            Toast.makeText(this, "Field can't be empty", Toast.LENGTH_SHORT).show()
                        }

                        hideEditLay()

                        showSendLay()

                        adapter.notifyItemChanged(position)
                    }


                    true
                }
                R.id.messageDelete ->{
                    FireHelper.deleteMessage(messages[position], chatId)
                    adapter.notifyItemRemoved(position)
                    true
                }
                else ->{
                    true
                }
            }
        }

        popMessageMenu.show()
    }

    private fun hideSendLay(){
        chooseFile.visibility = View.GONE
        etMessage.visibility = View.GONE
        ibSendMessage.visibility = View.GONE
    }

    private fun showSendLay(){
        chooseFile.visibility = View.VISIBLE
        etMessage.visibility = View.VISIBLE
        ibSendMessage.visibility = View.VISIBLE
    }

    private fun hideEditLay(){
        etMessageEdit.visibility = View.GONE
        ibEditDone.visibility = View.GONE
    }

    private fun showEditLay(){
        etMessageEdit.visibility = View.VISIBLE
        ibEditDone.visibility = View.VISIBLE
    }

    fun refreshMessages(){
        messages.clear()
        FireHelper.Chats.child(chatId).child("Messages").get().addOnCompleteListener {
            if(it.isSuccessful){
                for(i in it.result.children){
                    val message = i.getValue(MessageModel::class.java)

                    if(message != null){
                        messages.add(message)
                    }
                }
                adapter = MessageAdapter(messages, object : OnLongCLickListener{
                    override fun onLongClickMessageLIstener(position: Int, view: View) {
                        messageLongListener(position, view)
                    }

                    override fun onLognClickImageListener(position: Int, view: View) {
                        imageLongListener(position, view)
                    }

                })
                rvMessages.adapter = adapter
            }
        }
    }

    override fun onPause() {
        super.onPause()

        messages.clear()

        adapter = MessageAdapter(messages, object : OnLongCLickListener{
            override fun onLongClickMessageLIstener(position: Int, view: View) {
                messageLongListener(position, view)
            }

            override fun onLognClickImageListener(position: Int, view: View) {
                imageLongListener(position, view)
            }

        })
    }

}