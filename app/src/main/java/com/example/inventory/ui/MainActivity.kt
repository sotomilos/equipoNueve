package com.example.inventory.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.inventory.R
import com.example.inventory.model.Inventory
import com.example.inventory.repository.InventoryRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Crear repositorio
        val repository = InventoryRepository(this)

        // Probar los métodos
        lifecycleScope.launch {
            try {
                // Guardar un inventario
                val inv =
                    Inventory(id = 2, name = "Inventario de prueba dos", price = 200, quantity = 20)
                repository.saveInventory(inv)
                Log.d("REPO_TEST", "Inventario guardado: $inv")

                // Listar inventarios
                val list = repository.getListInventory()
                Log.d("REPO_TEST", "Inventarios obtenidos: $list")

                // Probar el servicio remoto
                val products = repository.getProducts()
                Log.d("REPO_TEST", "Productos desde API: ${products.size}")
            } catch (e: Exception) {
                Log.e("REPO_TEST", "Error en prueba de repositorio", e)
            }
        }
    }
}