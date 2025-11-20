package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inventory.model.Inventory
import com.example.inventory.utils.Constants.NAME_BD

@Database(entities = [Inventory::class], version = 2, exportSchema = false)
abstract class InventoryDB : RoomDatabase() {
    abstract fun inventoryDao(): InventoryDao

    companion object{
        @Volatile
        private var INSTANCE: InventoryDB? = null

        fun getDatabase(context: Context): InventoryDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InventoryDB::class.java,
                    NAME_BD
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}