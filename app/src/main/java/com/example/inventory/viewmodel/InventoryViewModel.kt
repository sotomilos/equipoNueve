package com.example.inventory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.InventoryRepository
import com.example.inventory.model.Inventory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    val inventoryItems: LiveData<List<Inventory>> = repository.getInventoryItems().asLiveData()

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
}
