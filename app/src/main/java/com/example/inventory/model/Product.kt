package com.example.inventory.model

import com.google.gson.annotations.SerializedName


data class Product(
    @SerializedName("id")
    val id:Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("price")
    val price: Double
)