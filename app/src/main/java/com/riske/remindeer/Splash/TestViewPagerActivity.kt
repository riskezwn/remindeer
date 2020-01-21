package com.riske.remindeer.Splash

import android.content.Context
import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.riske.remindeer.R
import com.riske.remindeer.Splash_Sliders.PrimerFragment
import com.riske.remindeer.Splash_Sliders.SegundoFragment
import com.riske.remindeer.Splash_Sliders.TercerFragment
import com.riske.remindeer.Welcome.BienvenidaActivity
import kotlinx.android.synthetic.main.activity_pager.*

var prevStarted: String = "prevStarted"

class TestViewPagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager)
        val sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
        if (!sharedpreferences.getBoolean(prevStarted, false))
        {
            val editor = sharedpreferences.edit()
            editor.putBoolean(prevStarted, java.lang.Boolean.TRUE)
            editor.apply()
        }
        else
        {
            val homeIntent = Intent(this, BienvenidaActivity::class.java)
            startActivity(homeIntent)
            finish()
        }

        init()
        btnSalir.setOnClickListener {
            val homeIntent = Intent(this, BienvenidaActivity::class.java)
            startActivity(homeIntent)
            finish()
        }


    }

    private fun init() {
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        PrimerFragment()
                    }

                    1 -> {
                        SegundoFragment()
                    }
                    else -> {
                        TercerFragment()
                    }
                }
            }

            override fun getItemCount(): Int {
                return 3
            }
        }
        indicator.setViewPager(viewPager)

    }

}

