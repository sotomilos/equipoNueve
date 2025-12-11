package com.example.inventory.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.inventory.data.InventoryRepository
import com.example.inventory.model.Inventory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.Description
import org.junit.rules.TestWatcher
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var application: Application

    @Mock
    lateinit var repository: InventoryRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        // Previene NPE en cualquier instanciación
        whenever(repository.getInventoryItems()).thenReturn(MutableStateFlow(emptyList()))
    }

    @Test
    fun `inventoryItems expone la lista que viene del repository`() = runTest {
        val item1: Inventory = mock()
        val item2: Inventory = mock()
        val fakeList = listOf(item1, item2)

        whenever(repository.getInventoryItems()).thenReturn(MutableStateFlow(fakeList))

        val viewModel = InventoryViewModel(application, repository)
        advanceUntilIdle()

        val result = viewModel.inventoryItems.getOrAwaitValue()
        assertEquals(fakeList, result)
    }

    @Test
    fun `getItem devuelve el item del repository`() = runTest {
        val item: Inventory = mock()

        whenever(repository.getInventoryItem("123")).thenReturn(MutableStateFlow(item))

        val viewModel = InventoryViewModel(application, repository)
        advanceUntilIdle()

        val result = viewModel.getItem("123").getOrAwaitValue()

        assertEquals(item, result)
    }

    @Test
    fun `saveInventoryItem llama a repository_saveInventoryItem`() = runTest {
        val item: Inventory = mock()
        val viewModel = InventoryViewModel(application, repository)

        viewModel.saveInventoryItem(item)
        advanceUntilIdle()

        verify(repository).saveInventoryItem(item)
    }

    @Test
    fun `updateInventoryItem llama a repository_updateInventoryItem`() = runTest {
        val item: Inventory = mock()
        val viewModel = InventoryViewModel(application, repository)

        viewModel.updateInventoryItem(item)
        advanceUntilIdle()

        verify(repository).updateInventoryItem(item)
    }

    @Test
    fun `deleteInventoryItem llama a repository_deleteInventoryItem`() = runTest {
        val item: Inventory = mock()
        val viewModel = InventoryViewModel(application, repository)

        viewModel.deleteInventoryItem(item)
        advanceUntilIdle()

        verify(repository).deleteInventoryItem(item)
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}


fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)

    val observer = object : Observer<T> {
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }

    this.observeForever(observer)

    if (!latch.await(time, timeUnit)) {
        this.removeObserver(observer)
        throw RuntimeException("LiveData no emitió valor a tiempo")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

