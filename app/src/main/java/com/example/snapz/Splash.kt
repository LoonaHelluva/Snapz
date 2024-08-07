package com.example.snapz

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
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

        if(checkUser()) {
            getMe()
        }
        else{
            val intent = Intent(this, SignUp::class.java)

            startActivity(intent)
            finish()
        }
    }

    private fun checkUser(): Boolean{
        if(!checkInternet()){
            val intent = Intent(this, noInternet::class.java)

            startActivity(intent)
            finish()

            return false
        }
        else{
            if(FireHelper.user != null){
                getMe()
                return true
            }
            return false
        }
    }

    private fun checkInternet() : Boolean{
        val connectivityManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if(networkInfo == null || !networkInfo.isConnected){
            return false
        }

        return true
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