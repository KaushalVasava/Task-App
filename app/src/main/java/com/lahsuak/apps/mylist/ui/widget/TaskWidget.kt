package com.lahsuak.apps.mylist.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.ui.MainActivity

class TaskWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        for (appWidgetId in appWidgetIds!!) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("key", true)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

            val pendingIntent = PendingIntent.getActivity (context, 0, intent, 0)
            val views = RemoteViews(context!!.packageName, R.layout.example_widget)
            views.setOnClickPendingIntent(R.id.example_widget_button, pendingIntent)

            appWidgetManager!!.updateAppWidget(appWidgetId, views)

        }
    }

}