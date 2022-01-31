package com.lahsuak.apps.mylist.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.ui.MainActivity
import com.lahsuak.apps.mylist.ui.fragments.AddUpdateFragmentArgs
import com.lahsuak.apps.mylist.util.Util.CHANNEL_ID
import com.lahsuak.apps.mylist.util.Util.requestCode

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        var textTitle: String = intent!!.getStringExtra("task").toString()
            //  val textId: Int = intent.getIntExtra("task_id",0)
        val textId =textTitle.substringBefore(",")
        textTitle = textTitle.substringAfter(",")
        Log.d("TAG", "onReceive: $textTitle and $textId")

        val intent2 = Intent(context, MainActivity::class.java)
        intent2.putExtra("task_name", "$textId ")

        intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //context.sendBroadcast(intent2)
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.main_nav_graph)
            .setDestination(R.id.addUpdateFragment)
            .setArguments(
                AddUpdateFragmentArgs.Builder(
                textId.toInt(),textTitle
            ).build().toBundle())
            .createPendingIntent()

        //val pendingIntent = PendingIntent.getActivity(context,textId.toInt() , intent2,0)

        val notification = NotificationCompat.Builder(context,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_reminder)
            .setContentTitle(textTitle)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setLights(Color.WHITE,200,200)
            .setContentIntent(pendingIntent)
            .build()
        val notificationCompat = NotificationManagerCompat.from(context)
        notificationCompat.notify(1,notification)

        Toast.makeText(context, "Alarm received! $textTitle", Toast.LENGTH_LONG).show()
    }
}