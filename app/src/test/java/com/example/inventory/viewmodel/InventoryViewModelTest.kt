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

/**
 * Test unitarios para InventoryViewModel.
 *
 * Este archivo valida que el ViewModel:
 *  - Expone correctamente la lista de inventario desde el repositorio
 *  - Devuelve un item específico cuando se solicita
 *  - Llama a los métodos del repositorio (save, update, delete)
 *
 * Todo esto se prueba SIN base de datos real y SIN Firestore,
 * utilizando mocks del InventoryRepository.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModelTest {

    /**
     * Permite que LiveData ejecute callbacks inmediatamente en el hilo de test.
     * De lo contrario, LiveData necesita un "main thread" de Android.
     */
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Regla usada para reemplazar Dispatchers.Main por un TestDispatcher.
     * Así controlamos las corrutinas manualmente durante los tests.
     */
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**
     * Mock del Application requerido por AndroidViewModel.
     */
    @Mock
    lateinit var application: Application

    /**
     * Mock del repositorio. Los tests controlan qué valores devuelve.
     */
    @Mock
    lateinit var repository: InventoryRepository

    /**
     * Se ejecuta antes de cada test.
     * Inicializa los mocks y evita NullPointerExceptions devolviendo una lista vacía por defecto.
     */
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Si el ViewModel intenta observar inventoryItems al inicializarse,
        // esta línea asegura que al menos reciba un Flow no-nulo.
        whenever(repository.getInventoryItems()).thenReturn(MutableStateFlow(emptyList()))
    }

    // -------------------------------------------------------------------------
    // TEST 1: inventoryItems expone los datos del repositorio
    // -------------------------------------------------------------------------

    /**
     * Verifica que el ViewModel exponga exactamente la misma lista
     * que devuelve el InventoryRepository.
     */
    @Test
    fun `inventoryItems expone la lista que viene del repository`() = runTest {
        // ARRANGE: simulamos dos items
        val item1: Inventory = mock()
        val item2: Inventory = mock()
        val fakeList = listOf(item1, item2)

        // El repositorio devolverá esta lista
        whenever(repository.getInventoryItems()).thenReturn(MutableStateFlow(fakeList))

        // ACT: creamos el ViewModel e iniciamos corrutinas
        val viewModel = InventoryViewModel(application, repository)
        advanceUntilIdle()

        // Obtenemos el LiveData del ViewModel
        val result = viewModel.inventoryItems.getOrAwaitValue()

        // ASSERT: comparamos listas
        assertEquals(fakeList, result)
    }

    // -------------------------------------------------------------------------
    // TEST 2: getItem devuelve un item específico
    // -------------------------------------------------------------------------

    /**
     * Verifica que cuando pedimos un item por ID,
     * el ViewModel devuelva exactamente el objeto del repositorio.
     */
    @Test
    fun `getItem devuelve el item del repository`() = runTest {
        // ARRANGE
        val item: Inventory = mock()
        whenever(repository.getInventoryItem("123")).thenReturn(MutableStateFlow(item))

        // ACT
        val viewModel = InventoryViewModel(application, repository)
        advanceUntilIdle()

        val result = viewModel.getItem("123").getOrAwaitValue()

        // ASSERT
        assertEquals(item, result)
    }

    // -------------------------------------------------------------------------
    // TEST 3: saveInventoryItem delega al repositorio
    // -------------------------------------------------------------------------

    /**
     * Verifica que el ViewModel llame al método saveInventoryItem()
     * del repositorio cuando se solicita guardar un elemento.
     */
    @Test
    fun `saveInventoryItem llama a repository_saveInventoryItem`() = runTest {
        val item: Inventory = mock()
        val viewModel = InventoryViewModel(application, repository)

        viewModel.saveInventoryItem(item)
        advanceUntilIdle()

        // Verificamos la llamada directa al repositorio
        verify(repository).saveInventoryItem(item)
    }

    // -------------------------------------------------------------------------
    // TEST 4: updateInventoryItem delega al repositorio
    // -------------------------------------------------------------------------

    /**
     * Verifica que updateInventoryItem() del ViewModel llame al repositorio.
     */
    @Test
    fun `updateInventoryItem llama a repository_updateInventoryItem`() = runTest {
        val item: Inventory = mock()
        val viewModel = InventoryViewModel(application, repository)

        viewModel.updateInventoryItem(item)
        advanceUntilIdle()

        verify(repository).updateInventoryItem(item)
    }

    // -------------------------------------------------------------------------
    // TEST 5: deleteInventoryItem delega al repositorio
    // -------------------------------------------------------------------------

    /**
     * Verifica que deleteInventoryItem() llame a repository.deleteInventoryItem().
     */
    @Test
    fun `deleteInventoryItem llama a repository_deleteInventoryItem`() = runTest {
        val item: Inventory = mock()
        val viewModel = InventoryViewModel(application, repository)

        viewModel.deleteInventoryItem(item)
        advanceUntilIdle()

        verify(repository).deleteInventoryItem(item)
    }
}

//
// -----------------------------------------------------------------------------
// Reglas y helpers compartidos por múltiples tests
// -----------------------------------------------------------------------------

/**
 * Regla personalizada para reemplazar Dispatchers.Main durante los tests.
 *
 * Esto es obligatorio porque las ViewModels usan viewModelScope,
 * que por defecto ejecuta corrutinas en Dispatchers.Main.
 */
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

/**
 * Helper para obtener un valor de LiveData dentro de un test de forma síncrona.
 *
 * LiveData es asíncrono por naturaleza, pero este helper:
 *  - Observa los cambios temporalmente
 *  - Espera hasta 2 segundos por un valor
 *  - Devuelve el valor emitido
 *
 * Si no se emite nada, lanza excepción para que el test falle.
 */
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


