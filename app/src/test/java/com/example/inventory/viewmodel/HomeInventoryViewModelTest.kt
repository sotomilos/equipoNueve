package com.example.inventory.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.inventory.data.InventoryRepository
import com.example.inventory.model.Inventory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * Test unitario para HomeInventoryViewModel.
 *
 * Verifica que el ViewModel exponga correctamente la lista de inventario
 * proveniente del InventoryRepository utilizando LiveData + Flow.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeInventoryViewModelTest {

    /**
     * Esta regla permite que LiveData ejecute sus callbacks de manera síncrona.
     * Así evitamos problemas en tests cuando LiveData normalmente requiere un hilo principal.
     */
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Regla personalizada que reemplaza Dispatchers.Main por un dispatcher de pruebas.
     * Esto permite controlar las corrutinas dentro del test sin depender del hilo principal de Android.
     */
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**
     * Mock del Application necesario para construir el ViewModel.
     * No necesitamos su comportamiento real, solo su referencia.
     */
    @Mock
    lateinit var application: Application

    /**
     * Mock del repositorio. Este objeto será controlado desde el test
     * para simular distintos valores que devolvería la base de datos.
     */
    @Mock
    lateinit var repository: InventoryRepository

    /**
     * Método que se ejecuta antes de cada test.
     * Inicializa los mocks y define un valor por defecto para evitar NullPointerExceptions.
     */
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Por defecto, el repositorio devuelve una lista vacía
        // Esto evita que el ViewModel reciba null al transformar el Flow a LiveData.
        whenever(repository.getInventoryItems()).thenReturn(MutableStateFlow(emptyList()))
    }

    /**
     * Test principal:
     *
     * Verifica que:
     * - Cuando el repositorio devuelve una lista de items,
     * - El ViewModel expone esa lista correctamente mediante LiveData.
     */
    @Test
    fun `inventoryItems expone los datos del repository`() = runTest {
        // ---------- ARRANGE ----------
        // Creamos dos objetos Inventory mockeados
        val item1: Inventory = mock()
        val item2: Inventory = mock()

        // Lista esperada que debería retornar el ViewModel
        val expectedList = listOf(item1, item2)

        // Simulamos que el repositorio devuelve esta lista mediante un MutableStateFlow
        whenever(repository.getInventoryItems()).thenReturn(MutableStateFlow(expectedList))

        // ---------- ACT ----------
        // Construimos el ViewModel manualmente, inyectando el repositorio mockeado
        val viewModel = HomeInventoryViewModel(application, repository)

        // Avanzamos la ejecución de corrutinas para permitir que LiveData reciba los valores
        advanceUntilIdle()

        // Obtenemos el valor emitido por LiveData usando un helper de test
        val result = viewModel.inventoryItems.getOrAwaitValue()

        // ---------- ASSERT ----------
        // Comparamos que el resultado del ViewModel sea exactamente la lista simulada
        assertEquals(expectedList, result)
    }
}
