package com.example.inventory.viewmodel

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.inventory.R
import com.example.inventory.repository.InventoryRepository
import com.example.inventory.ui.MainActivity
import com.example.inventory.ui.widget.Inventory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

private const val PREFS_NAME = "com.example.inventory.ui.widget.Inventory"
private const val PREF_PREFIX_KEY = "appwidget_"
const val ACTION_TOGGLE_VISIBILITY = "com.example.inventory.viewmodel.ACTION_TOGGLE_VISIBILITY"

fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val views = RemoteViews(context.packageName, R.layout.inventory)
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val isVisible = prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId, true)

    CoroutineScope(Dispatchers.Main).launch {
        val repository = InventoryRepository(context)
        val items = repository.getListInventory()
        val totalValue = items.sumOf { it.price * it.quantity }

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
        val formattedTotal = currencyFormat.format(totalValue)

        if (isVisible) {
            views.setTextViewText(R.id.inventory_amount_text, formattedTotal)
            views.setImageViewResource(R.id.visibility_toggle_button, android.R.drawable.ic_menu_view)
        } else {
            views.setTextViewText(R.id.inventory_amount_text, "***")
            views.setImageViewResource(R.id.visibility_toggle_button, android.R.drawable.ic_menu_close_clear_cancel)
        }

        val toggleIntent = Intent(context, Inventory::class.java).apply {
            action = ACTION_TOGGLE_VISIBILITY
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val togglePendingIntent = PendingIntent.getBroadcast(context, appWidgetId, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.visibility_toggle_button, togglePendingIntent)

        val launchIntent = Intent(context, MainActivity::class.java)
        val launchPendingIntent = PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.settings_button, launchPendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

fun toggleVisibility(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val isVisible = prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId, true)
    prefs.edit().putBoolean(PREF_PREFIX_KEY + appWidgetId, !isVisible).apply()
}

fun onWidgetDeleted(context: Context, appWidgetId: Int) {
    context.getSharedPreferences(PREFS_NAME, 0).edit().apply {
        remove(PREF_PREFIX_KEY + appWidgetId)
        apply()
    }
}
