package com.example.inventory.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "inventory")
data class Inventory(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val quantity: Int
) : Serializable