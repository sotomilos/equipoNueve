package com.example.inventory.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.inventory.R
import com.example.inventory.fragments.HomeInventoryFragment
import com.example.inventory.fragments.LoginFragment
import com.example.inventory.sessions.SessionManager


class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize SessionManager before using it
        sessionManager = SessionManager(this)

        if (savedInstanceState == null) {
            if (sessionManager.isLoggedIn()) {
                showHome()
            } else {
                showLogin()
            }
        }
    }


    private fun showHome() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.main_fragment_container, HomeInventoryFragment())
        }
    }
    private fun showLogin() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.main_fragment_container, LoginFragment())
        }
    }
}
