package com.riske.remindeer.Notifications

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import java.util.*


class NotificationUtils {

    fun setNotificationPr1(timeInMilliSeconds: Long, activity: Activity) {

        //------------  alarm settings start  -----------------//

        if (timeInMilliSeconds > 0) {


            val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(activity.applicationContext, AlarmReceiver1::class.java)
            alarmIntent.putExtra("reason", "notification")
            alarmIntent.putExtra("timestamp", timeInMilliSeconds)


            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMilliSeconds
            val pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)


            Log.i("myApp2", "Este es el tiempo en Notification ${calendar.timeInMillis}")
            Log.i("myApp2", "${Date()}")

            if (calendar.time < Date()){
                Log.i("myApp", "La notificación ya ha pasado")
            }else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }



        }

        //------------ end of alarm settings  -----------------//


    }

    fun setNotificationPr2(timeInMilliSeconds: Long, activity: Activity) {

        //------------  alarm settings start  -----------------//

        if (timeInMilliSeconds > 0) {


            val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(activity.applicationContext, AlarmReceiver2::class.java)
            alarmIntent.putExtra("reason", "notification")
            alarmIntent.putExtra("timestamp", timeInMilliSeconds)


            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMilliSeconds
            val pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)


            Log.i("myApp2", "Este es el tiempo en Notification ${calendar.timeInMillis}")
            Log.i("myApp2", "${Date()}")

            if (calendar.time < Date()){
                Log.i("myApp", "La notificación ya ha pasado")
            }else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }



        }

        //------------ end of alarm settings  -----------------//


    }

    fun setNotificationPr3(timeInMilliSeconds: Long, activity: Activity) {

        //------------  alarm settings start  -----------------//

        if (timeInMilliSeconds > 0) {


            val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(activity.applicationContext, AlarmReceiver3::class.java)
            alarmIntent.putExtra("reason", "notification")
            alarmIntent.putExtra("timestamp", timeInMilliSeconds)


            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMilliSeconds
            val pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)


            Log.i("myApp2", "Este es el tiempo en Notification ${calendar.timeInMillis}")
            Log.i("myApp2", "${Date()}")

            if (calendar.time < Date()){
                Log.i("myApp", "La notificación ya ha pasado")
            }else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }



        }

        //------------ end of alarm settings  -----------------//


    }


    fun setNotificationTask(timeInMilliSeconds: Long, activity: Activity) {

        //------------  alarm settings start  -----------------//

        if (timeInMilliSeconds > 0) {


            val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(activity.applicationContext, AlarmReceiver::class.java) // AlarmReceiver1 = broadcast receiver

            alarmIntent.putExtra("reason", "notification")
            alarmIntent.putExtra("timestamp", timeInMilliSeconds)


            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMilliSeconds
            val pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)


            Log.i("myApp2", "Este es el tiempo en Notification ${calendar.timeInMillis}")
            Log.i("myApp2", "${Date()}")

            if (calendar.time < Date()){
                Log.i("myApp", "La notificación ya ha pasado")
            }else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }



        }

        //------------ end of alarm settings  -----------------//


    }

}