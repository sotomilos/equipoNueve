package com.example.inventory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel simple para manejar estado relacionado con el widget
 * dentro de la app (si algún Fragment/Activity lo necesita).
 *
 * No tiene ninguna relación directa con el AppWidgetProvider.
 * Toda la lógica del widget está en:
 *   com.example.inventory.ui.widget.Inventory
 */
class WidgetViewModel : ViewModel() {

    // Ejemplo de estado: si el saldo debería mostrarse o no dentro de la app
    private val _isBalanceVisible = MutableLiveData(false)
    val isBalanceVisible: LiveData<Boolean> get() = _isBalanceVisible

    fun toggleVisibility() {
        val current = _isBalanceVisible.value ?: false
        _isBalanceVisible.value = !current
    }
}
