package com.example.snapz.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.snapz.Classes.ChatModel
import com.example.snapz.Classes.FireHelper
import com.example.snapz.Classes.MessageModel
import com.example.snapz.Classes.UserModel
import com.example.snapz.R

class SettingsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }


    //Views
    lateinit var profImage: ImageButton
    lateinit var userName: EditText
    lateinit var done_button: ImageButton

    //User
    lateinit var me: UserModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initializing views
        profImage = view.findViewById(R.id.ibUserImage)
        userName = view.findViewById(R.id.settingsUserName)
        done_button = view.findViewById(R.id.done_button)


        setThePage() //Setting the page

        //On click listeners
        profImage.setOnClickListener { //If user clicks on profile image
            changeProfImage()
        }

        done_button.setOnClickListener {
            val name = userName.text.toString().trim()
            if(name != "" && name != me.name){
                changeName(name)
            }
            else{
                Toast.makeText(requireContext(), "New name can't be empty or equal to old name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setThePage(){ //This function is searching me as user in realtime database and setting my data to views

        //Initializing realtime refference
        val realtime = FireHelper.Users.child(FireHelper.user!!.uid)

        realtime.get().addOnCompleteListener { //Getting the user
            if(it.isSuccessful){
                val user = it.result.getValue(UserModel::class.java) //Converting to class UserModel

                if(user != null && user.id == FireHelper.user!!.uid){ //Checking if this user is me
                    me = user //Setting me as this user

                    userName.setHint(me.name) //Setting my name to hint of the userName view
                    userName.setText("")

                    Glide.with(this).load(me.profileImage).into(profImage) //Setting my profile image into profileImage view using Glide

                    Log.d("SettingsUser", "image: ${me.profileImage}")
                }
            }
        }
    }

    fun changeProfImage(){ //This function is changing my profile image

        val intent = Intent(Intent.ACTION_GET_CONTENT) //Initializing the intent of file chooser

        intent.type = "image/*" //Setting the type of files as Image only

        startActivityForResult(intent, 1) //Starting activity
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data?.data != null){
            val storage = FireHelper.Storage.child("${me.name}.profileImage.${FireHelper.getType(requireContext(), data.data!!)}")

            val uploadTask = storage.putFile(data.data!!).continueWithTask { task ->
                if(!task.isSuccessful){
                    task.exception.let { throw it!! }
                }

                return@continueWithTask storage.downloadUrl
            }.addOnCompleteListener {
                val link = it.result.toString()

                Log.d("LINK", link)

                FireHelper.Users.child(me.id).child("profileImage").setValue(link).addOnCompleteListener {
                    if(it.isSuccessful){
                        setThePage()
                    }
                }
            }
        }
    }

    fun changeName(name: String){
        val realtime = FireHelper.Users.child(me.id)

        if(me.name != name){

            FireHelper.Chats.get().addOnCompleteListener {
                if(it.isSuccessful){
                    for(i in it.result.children){
                        val chat = i.getValue(ChatModel::class.java)

                        if(chat != null){
                            Log.i("NAME", isMyNameExist(chat.name).toString())
                            if(isMyNameExist(chat.name)){
                                FireHelper.Chats.child(chat.id).child("name").setValue(separateNames(chat.name, name))
                            }
                        }
                    }
                    realtime.child("name").setValue(name)
                    setThePage()
                }
            }
        }
    }

    fun separateNames(name: String, myName: String) : String{
        val names = mutableListOf<String>()
        var curName: String = ""
        var newChatName = ""

        //Separating the names and + symbols
        for(i in name.indices){
            if(name[i] != '+'){
                curName += name[i]
            }
            else if(name[i] == '+'){
                if(curName == me.name){
                    names.add(myName)
                    curName = ""
                }
                else{
                    names.add(curName)
                    curName = ""
                }
                names.add("+")
            }
            if((i + 1) == name.length){
                if(curName == me.name){
                    names.add(myName)
                }
                else{
                    names.add(curName)
                }
            }
        }

        //Looking for my name in chat name and changing it to my new name

        //Concatenating chat name in to string
        for(i in names){
            newChatName += i
        }

        return newChatName
    }

    fun isMyNameExist(name: String) : Boolean{
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