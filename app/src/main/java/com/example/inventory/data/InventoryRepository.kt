package com.example.inventory.data

import android.util.Log
import com.example.inventory.model.Inventory
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepository @Inject constructor(private val inventoryDao: InventoryDao) {

    private val firestore = Firebase.firestore.collection("inventory")

    fun getInventoryItem(id: String): Flow<Inventory> {
        return inventoryDao.getItem(id)
    }

    suspend fun saveInventoryItem(item: Inventory) {
        try {
            if (item.id.isBlank()) {
                val document = firestore.document()
                item.id = document.id
            }
            firestore.document(item.id).set(item).await()
            Log.d("FirestoreSuccess", "Successfully saved item: ${item.id}")
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error saving item to Firestore", e)
        }
    }

    fun getInventoryItems(): Flow<List<Inventory>> {
        CoroutineScope(Dispatchers.IO).launch {
            firestore.snapshots().collect { snapshot ->
                val items = snapshot.toObjects<Inventory>()
                inventoryDao.syncItems(items)
            }
        }
        return inventoryDao.getAllItems()
    }


    suspend fun updateInventoryItem(item: Inventory) {
        try {
            firestore.document(item.id).set(item).await()
            Log.d("FirestoreSuccess", "Successfully updated item: ${item.id}")
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error updating item in Firestore", e)
        }
    }

    suspend fun deleteInventoryItem(item: Inventory) {
        try {
            firestore.document(item.id).delete().await()
            Log.d("FirestoreSuccess", "Successfully deleted item: ${item.id}")
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error deleting item from Firestore", e)
        }
    }
}
