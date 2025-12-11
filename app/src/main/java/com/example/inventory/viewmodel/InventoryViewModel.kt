package com.example.inventory.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.InventoryDB
import com.example.inventory.data.InventoryRepository
import com.example.inventory.model.Inventory
import kotlinx.coroutines.launch

class InventoryViewModel(
    application: Application,
    private val repository: InventoryRepository
) : AndroidViewModel(application) {

    val inventoryItems: LiveData<List<Inventory>> =
        repository.getInventoryItems().asLiveData()

    fun getItem(id: String): LiveData<Inventory> {
        return repository.getInventoryItem(id).asLiveData()
    }

    fun saveInventoryItem(item: Inventory) {
        viewModelScope.launch {
            repository.saveInventoryItem(item)
        }
    }

    fun updateInventoryItem(item: Inventory) {
        viewModelScope.launch {
            repository.updateInventoryItem(item)
        }
    }

    fun deleteInventoryItem(item: Inventory) {
        viewModelScope.launch {
            repository.deleteInventoryItem(item)
        }
    }

    constructor(application: Application) : this(
        application,
        InventoryRepository(
            InventoryDB.getDatabase(application).inventoryDao()
        )
    )
}

