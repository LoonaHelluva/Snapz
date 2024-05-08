package com.example.snapz

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.snapz.Classes.ChatModel
import com.example.snapz.Classes.FireHelper
import com.example.snapz.Classes.UserModel
import com.example.snapz.adapters.ChatsAdapter
import com.example.snapz.fragments.ChatsFragment
import com.example.snapz.Classes.FireHelper.Companion.me

class Splash : AppCompatActivity() {

    val TAG = "Splash"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getMe()
    }

    private fun getMe(){
        if(FireHelper.auth.currentUser != null) {
            FireHelper.Users.child(FireHelper.user!!.uid).get().addOnCompleteListener{
                if(it.isSuccessful){
                    val user = it.result.getValue(UserModel::class.java)

                    if(user != null){
                        me = user

                        Log.d(TAG, "Got me: $me")

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        ChatsFragment.isLoaded = false

                        val intent = Intent(this, SignUp::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                else{
                    ChatsFragment.isLoaded = false

                    val intent = Intent(this, SignUp::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}