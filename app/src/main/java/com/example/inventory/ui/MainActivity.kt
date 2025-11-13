package com.example.inventory.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.example.inventory.R
import com.example.inventory.model.Inventory
import com.example.inventory.repository.InventoryRepository
import com.example.inventory.fragments.LoginFragment
import com.example.inventory.sessions.SessionManager
import com.example.inventory.utils.Constants
import kotlinx.coroutines.launch
import com.example.inventory.viewmodel.LoginViewModel



class MainActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mainLayout = findViewById<android.view.View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fingerprintIcon = findViewById<ImageView>(R.id.iv_fingerprint_login)
        fingerprintIcon.setOnClickListener {
            loginViewModel.startAuthentication(this)
        }
        setupObservers()
        testRepository()
    }

    private fun setupObservers() {
        loginViewModel.authState.observe(this) { event ->
            event.getContentIfNotHandled()?.let { state ->
                when (state) {
                    LoginViewModel.AuthState.SUCCESS -> {
                        Toast.makeText(this, Constants.AUTHENTICATED, Toast.LENGTH_SHORT).show()
                        // TODO: Navigate to the main part of your app!
                        sessionManager.saveLoginState(true)
                    }
                    LoginViewModel.AuthState.FAILED -> {
                        Toast.makeText(this, Constants.AUTHENTICATED_FAILED, Toast.LENGTH_SHORT).show()
                    }
                    LoginViewModel.AuthState.ERROR -> {
                    }
                }
            }
        }
        loginViewModel.errorMessage.observe(this) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }

        loginViewModel.enrollmentIntent.observe(this) { event ->
            event.getContentIfNotHandled()?.let { intent ->
                Toast.makeText(this, Constants.ADD_FINGERPRINT, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun testRepository() {
        val repository = InventoryRepository(this)
        lifecycleScope.launch {
            try {
                val inv = Inventory(id = 2, name = "Inventario de prueba dos", price = 200, quantity = 20)
                repository.saveInventory(inv)
                Log.d("REPO_TEST", "Inventario guardado: $inv")
                val list = repository.getListInventory()
                Log.d("REPO_TEST", "Inventarios obtenidos: $list")
                val products = repository.getProducts()
                Log.d("REPO_TEST", "Productos desde API: ${products.size}")
            } catch (e: Exception) {
                Log.e("REPO_TEST", "Error en prueba de repositorio", e)
            }
        }
    }
}