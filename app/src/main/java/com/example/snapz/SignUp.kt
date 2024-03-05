package com.example.snapz

import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.util.Log
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.snapz.Classes.FireHelper
import com.example.snapz.Classes.User
import com.example.snapz.databinding.ActivityMainBinding

class SignUp : AppCompatActivity() {

    //Views
    lateinit var toSignIn: Button
    lateinit var upEmail: EditText
    lateinit var upName: EditText
    lateinit var upPassword: EditText
    lateinit var btSignUp: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Initializing views
        toSignIn = findViewById(R.id.toSignIn)
        upEmail = findViewById(R.id.upEmail)
        upName = findViewById(R.id.upName)
        upPassword = findViewById(R.id.upPassword)
        btSignUp = findViewById(R.id.btSignup)



        //On click buttons listeners
        btSignUp.setOnClickListener{signUpCLicked()}
    }

    fun toSignInClicked(){

    }

    fun signUpCLicked(){

        val email: String = upEmail.text.toString().trim()
        val name: String = upName.text.toString().trim()
        val password: String = upPassword.text.toString().trim()

        if(email != "" &&
             name != "" &&
             password != ""){

            FireHelper.createNewUser(email = email, password = password, name = name, this)
        }
        else{
            Toast.makeText(this,"Fields can't be empty", Toast.LENGTH_SHORT).show()
        }
    }

}