package com.example.inventory.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.inventory.R
import com.example.inventory.data.InventoryDB
import com.example.inventory.data.InventoryRepository
import com.example.inventory.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

// Preferencias para recordar si el saldo está visible u oculto por widget
private const val PREFS_NAME = "com.example.inventory.ui.widget.Inventory"
private const val PREF_PREFIX_KEY = "appwidget_"

// Acción que se dispara al tocar el ojo
const val ACTION_TOGGLE_VISIBILITY =
    "com.example.inventory.ui.widget.ACTION_TOGGLE_VISIBILITY"

class Inventory : AppWidgetProvider() {

    // Se llama cuando el widget se crea o se actualiza
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    // Se llama cuando recibimos un broadcast (por ejemplo, del PendingIntent del ojo)
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (ACTION_TOGGLE_VISIBILITY == intent.action) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )

            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                // Cambiar el estado visible/oculto en SharedPreferences
                toggleVisibility(context, appWidgetId)
                // Volver a dibujar el widget
                updateAppWidget(
                    context,
                    AppWidgetManager.getInstance(context),
                    appWidgetId
                )
            }
        }
    }

    // Limpia las preferencias cuando se elimina el widget
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            onWidgetDeleted(context, appWidgetId)
        }
    }
}

// Actualiza el contenido del widget (texto, iconos y click listeners)
fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.inventory)

    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val isVisible = prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId, false)

    val auth = FirebaseAuth.getInstance()
    val userLoggedIn = auth.currentUser != null

    CoroutineScope(Dispatchers.Main).launch {
        // Obtenemos el total del inventario desde Room
        val dao = InventoryDB.getDatabase(context).inventoryDao()
        val repository = InventoryRepository(dao)
        val items = repository.getInventoryItems().first()
        val totalValue = items.sumOf { it.price * it.quantity }

        // Formateamos el saldo con separadores 1.234,56
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,##0.00", symbols)
        val formattedTotal = "$" + formatter.format(totalValue)

        // Lógica de visibilidad:
        // - Si NO está logueado → siempre "****"
        // - Si está logueado y visible → muestra el saldo
        // - Si está logueado y NO visible → "****"
        val displayText =
            if (userLoggedIn && isVisible) formattedTotal else "****"

        views.setTextViewText(R.id.inventory_amount_text, displayText)

        // Icono del ojo según estado (usando drawables del sistema)
        val iconRes =
            if (userLoggedIn && isVisible)
                android.R.drawable.ic_menu_close_clear_cancel  // modo ocultar
            else
                android.R.drawable.ic_menu_view                // modo mostrar

        views.setImageViewResource(R.id.visibility_toggle_button, iconRes)

        // PendingIntent para el botón del ojo (broadcast al mismo AppWidgetProvider)
        val toggleIntent = Intent(context, Inventory::class.java).apply {
            action = ACTION_TOGGLE_VISIBILITY
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }

        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.visibility_toggle_button, togglePendingIntent)

        // PendingIntent para el botón de settings (abre MainActivity)
        val launchIntent = Intent(context, MainActivity::class.java)
        val launchPendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.settings_button, launchPendingIntent)

        // Finalmente, actualizamos el widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

// Invierte el estado visible/oculto en SharedPreferences
fun toggleVisibility(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val current = prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId, false)
    prefs.edit().putBoolean(PREF_PREFIX_KEY + appWidgetId, !current).apply()
}

// Limpia las preferencias cuando el widget se borra
fun onWidgetDeleted(context: Context, appWidgetId: Int) {
    context.getSharedPreferences(PREFS_NAME, 0)
        .edit()
        .remove(PREF_PREFIX_KEY + appWidgetId)
        .apply()
}
