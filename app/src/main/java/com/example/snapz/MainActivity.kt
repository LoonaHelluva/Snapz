package com.example.snapz

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.snapz.fragments.ChatsFragment
import com.example.snapz.fragments.SearchFragment
import com.example.snapz.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.intellij.lang.annotations.Identifier

class MainActivity : AppCompatActivity() {

    //Views
    lateinit var menu: BottomNavigationView
    lateinit var frame: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        menu = findViewById(R.id.mainBottomMenu)
        frame = findViewById(R.id.mainFrame)

        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.mainFrame, ChatsFragment()).commit()

        //ClickListeners
        menu.setOnNavigationItemSelectedListener{menuClicked(it.itemId)}
    }

    fun menuClicked(id: Int): Boolean{
        when(id) {
            R.id.chats -> {
                replace(ChatsFragment())
            }
            R.id.search ->{
                replace(SearchFragment())
            }
            R.id.settings ->{
                replace(SettingsFragment())
            }
        }
        return true
    }

    fun replace(fragment: Fragment){
        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.mainFrame, fragment).commit()

    }
}