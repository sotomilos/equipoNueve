package com.example.inventory.di

import android.content.Context
import com.example.inventory.data.InventoryDB
import com.example.inventory.data.InventoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideInventoryDatabase(@ApplicationContext context: Context): InventoryDB {
        return InventoryDB.getDatabase(context)
    }

    @Provides
    fun provideInventoryDao(inventoryDB: InventoryDB): InventoryDao {
        return inventoryDB.inventoryDao()
    }
}
