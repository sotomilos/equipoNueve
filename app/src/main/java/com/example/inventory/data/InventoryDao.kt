package com.example.inventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.inventory.model.Inventory

@Dao
interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // En caso de elemento repetido, reemplaza
    suspend fun saveInventory(inventory: Inventory)

    @Query("SELECT * FROM Inventory")
    suspend fun getListInventory(): MutableList<Inventory>

    @Query("SELECT * FROM Inventory WHERE id = :id")
    suspend fun getInventoryItem(id: Int): MutableList<Inventory>

    @Delete
    suspend fun deleteInventory(inventory: Inventory)

    @Update
    suspend fun updateInventory(inventory: Inventory)
}