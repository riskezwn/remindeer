package com.riske.remindeer.Splash

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.riske.remindeer.MainActivity
import com.riske.remindeer.Notifications.NotificationUtils
import com.riske.remindeer.R
import kotlinx.android.synthetic.main.splash_screen.*
import java.util.*

class SplashScreen : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        persistanceData()

        val content = findViewById<View>(R.id.deerBlackSplash)

        deerBlackSplash.setImageResource(R.drawable.ic_remindeersplash)


        val timeOut = 2000
        val homeIntent = Intent(this@SplashScreen, TestViewPagerActivity::class.java)
        Handler().postDelayed({
            //Do some stuff here, like implement deep linking
            startActivity(homeIntent)
            finish()
        }, timeOut.toLong())

        ObjectAnimator.ofFloat(content, "alpha", 0f).apply{
            duration = 1000
            start()
        }
    }

    private fun persistanceData() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

}