package com.example.snapz

import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.util.Log
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
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
        if(upEmail.text.toString().trim() != "" &&
            upName.text.toString().trim() != "" &&
            upPassword.text.toString().trim() != ""){

            FireHelper.auth.createUserWithEmailAndPassword(upEmail.text.toString().trim(), upPassword.text.toString().trim()).addOnCompleteListener{
                if(it.isSuccessful){
                    val user = User(
                        id = FireHelper.auth.uid!!,
                        name = upName.text.toString().trim(),
                        email = upEmail.text.toString().trim()
                    )

                    FireHelper.Users.child(user.id).setValue(user).addOnCompleteListener {
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