package com.example.inventory.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.inventory.R
import com.example.inventory.fragments.HomeInventoryFragment
import com.example.inventory.fragments.LoginRegistreFragment
import com.example.inventory.ui.widget.EXTRA_FROM_WIDGET   // 👈 IMPORTANTE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        // ¿Esta Activity se abrió porque el usuario tocó el widget?
        val fromWidget = intent.getBooleanExtra(EXTRA_FROM_WIDGET, false)

        if (savedInstanceState == null) {
            when {
                fromWidget -> {
                    showLogin()
                }
                auth.currentUser != null -> {
                    showHome()
                }
                else -> {
                    showLogin()
                }
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

