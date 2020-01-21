package com.riske.remindeer.Notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class AlarmReceiver1 : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {

        val service = Intent(context, NotificationService1::class.java)
        service.putExtra("reason", intent.getStringExtra("reason"))
        service.putExtra("timestamp", intent.getLongExtra("timestamp", 0))


        context.startService(service)
    }


}