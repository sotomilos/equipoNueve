package com.example.inventory.webservice

import com.example.inventory.model.Product
import com.example.inventory.utils.Constants.END_POINT
import retrofit2.http.GET

interface ApiService {
    @GET(END_POINT)
    suspend fun getProducts(): MutableList<Product>
}