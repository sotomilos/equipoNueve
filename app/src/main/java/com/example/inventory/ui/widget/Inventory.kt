package com.example.inventory.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import com.example.inventory.viewmodel.ACTION_TOGGLE_VISIBILITY
import com.example.inventory.viewmodel.WidgetViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class Inventory : AppWidgetProvider() {

    @Inject
    lateinit var viewModel: WidgetViewModel
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            scope.launch {
                viewModel.updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (ACTION_TOGGLE_VISIBILITY == intent.action) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                viewModel.toggleVisibility(context, appWidgetId)
                scope.launch {
                    viewModel.updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId)
                }
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            viewModel.onWidgetDeleted(context, appWidgetId)
        }
    }
}
