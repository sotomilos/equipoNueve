package com.example.inventory.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.inventory.model.Inventory
import com.example.inventory.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers

class HomeInventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InventoryRepository(application)

    val inventoryItems: LiveData<List<Inventory>> = liveData(Dispatchers.IO) {
        try {
            emit(repository.getListInventory())
        } catch (e: Exception) {
            emit(emptyList<Inventory>())
        }
    }
}
