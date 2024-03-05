package com.example.snapz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.snapz.Classes.FireHelper

class SignIn : AppCompatActivity() {

    //Views
    lateinit var toSignUp: Button
    lateinit var inEmail: EditText
    lateinit var inPassword: EditText
    lateinit var btSignIn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Initializing views
        toSignUp = findViewById(R.id.toSignUp)
        inEmail = findViewById(R.id.inEmail)
        inPassword = findViewById(R.id.inPassword)
        btSignIn = findViewById(R.id.btSignIn)

        //Button click listeners
        toSignUp.setOnClickListener { val intent = Intent(this, SignUp::class.java)
                                      startActivity(intent)}

        btSignIn.setOnClickListener { signIn() }
    }

    fun signIn(){
        val email: String = inEmail.text.toString().trim()
        val password: String = inPassword.text.toString().trim()

        if(email != "" && password != ""){
            FireHelper.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Log.e("Auth", it.exception.toString())
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
        else{
            Toast.makeText(this, "Field can't be exit", Toast.LENGTH_SHORT).show()
        }
    }
}