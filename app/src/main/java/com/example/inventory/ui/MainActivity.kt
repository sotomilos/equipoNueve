package com.example.inventory.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.inventory.R
import com.example.inventory.fragments.HomeInventoryFragment
import com.example.inventory.fragments.LoginRegistreFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        if (savedInstanceState == null) {
            if (auth.currentUser != null) {
                showHome()
            } else {
                showLogin()
            }
        }
    }

    fun showHome() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.main_fragment_container, HomeInventoryFragment())
        }
    }

    fun showLogin() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.main_fragment_container, LoginRegistreFragment())
        }
    }
}
