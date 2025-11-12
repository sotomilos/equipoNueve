package com.example.inventory.repository

import android.content.Context
import com.example.inventory.data.InventoryDB
import com.example.inventory.data.InventoryDao
import com.example.inventory.model.Inventory
import com.example.inventory.model.Product
import com.example.inventory.webservice.ApiService
import com.example.inventory.webservice.ApiUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InventoryRepository(val context: Context) {
    private var inventoryDao:InventoryDao = InventoryDB.getDatabase(context).inventoryDao()
    private var apiService: ApiService = ApiUtils.getApiService()

    suspend fun saveInventory(inventory:Inventory){
        withContext(Dispatchers.IO){
            inventoryDao.saveInventory(inventory)
        }
    }

    suspend fun getListInventory():MutableList<Inventory>{
        return withContext(Dispatchers.IO){
            inventoryDao.getListInventory()
        }
    }

    suspend fun deleteInventory(inventory: Inventory){
        withContext(Dispatchers.IO){
            inventoryDao.deleteInventory(inventory)
        }
    }

    suspend fun updateRepositoy(inventory: Inventory){
        withContext(Dispatchers.IO){
            inventoryDao.updateInventory(inventory)
        }
    }

    suspend fun getProducts(): MutableList<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProducts()
                response
            } catch (e: Exception) {

                e.printStackTrace()
                mutableListOf()
            }
        }
    }
}