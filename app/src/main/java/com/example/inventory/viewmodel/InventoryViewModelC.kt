package com.example.inventory.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventory.repository.InventoryRepository
import com.example.inventory.model.Inventory
import kotlinx.coroutines.launch

class InventoryViewModelC(application: Application) : AndroidViewModel(application) {
    val context = getApplication<Application>()
    private val inventoryRepository = InventoryRepository(context)


    private val _listInventory = MutableLiveData<MutableList<Inventory>>()
    val listInventory: LiveData<MutableList<Inventory>> get() = _listInventory

    private val _progressState = MutableLiveData(false)
    val progressState: LiveData<Boolean> = _progressState

    fun saveInventory(inventory: Inventory, message: (String) -> Unit) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                inventoryRepository.saveInventory(inventory) { msg ->
                    message(msg)
                }
                _progressState.value = false
            } catch (e: Exception) {
                _progressState.value = false
            }
        }
    }

    fun getListInventory() {
        viewModelScope.launch {
            _progressState.value = true
            try {
                _listInventory.value = inventoryRepository.getListInventory()
                _progressState.value = false
                Log.d("DEBUG_VIEWMODEL", "Items recibidos: ${listInventory.value?.size}")
                Log.d("DEBUG_VIEWMODEL", "Contenido: ${listInventory.value}")
            } catch (e: Exception) {
                Log.d("ERROR_VIEWMODEL", e.message.toString())
                _progressState.value = false
            }

        }
    }

    fun deleteInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                inventoryRepository.deleteInventory(inventory)
                _progressState.value = false
            } catch (e: Exception) {
                _progressState.value = false
            }

        }
    }

    fun updateInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                inventoryRepository.updateInventory(inventory)
                _progressState.value = false
            } catch (e: Exception) {
                _progressState.value = false
            }
        }
    }
}