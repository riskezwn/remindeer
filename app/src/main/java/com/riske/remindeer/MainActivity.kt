package com.riske.remindeer


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jakewharton.threetenabp.AndroidThreeTen
import com.riske.remindeer.Calendar.CalendarFragment
import com.riske.remindeer.Notifications.NotificationUtils
import com.riske.remindeer.Profile.ProfileFragment
import com.riske.remindeer.Tasks.TaskDoneFragment
import com.riske.remindeer.Tasks.TaskFragment
import com.riske.remindeer.Welcome.BienvenidaActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    //Notifications
    private val mNotificationTime = Calendar.getInstance().timeInMillis + 5000 //Set after 5 seconds from the current time.
    private val mNotificationTime2 = Calendar.getInstance().timeInMillis + 10000 //Set after 5 seconds from the current time.
    private var mNotified = false
    private var mNotified2 = false





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AndroidThreeTen.init(this)



        backgroundAlarm1()
        backgroundAlarm2()
        backgroundAlarm3()



        initialise()

        toolbar = findViewById(R.id.bar)
        setSupportActionBar(toolbar)


        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        navView.setNavigationItemSelectedListener {
            var fragment: Fragment? = null
            when (it.itemId) {
               /* R.id.button_calendar -> {
                    fragment = CalendarFragment()

                }*/
                R.id.button_tasks -> {
                    fragment = TaskFragment()

                }
                R.id.button_tasksDone -> {
                    fragment = TaskDoneFragment()

                }
                R.id.button_profile -> {
                    fragment = ProfileFragment()

                }
                else -> {
                }
            }

            fragment?.let {
                supportFragmentManager.beginTransaction().replace(R.id.homeContainer, fragment).commit()

            }


            it.isChecked = true
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
        toggle.syncState()

        var fragment = TaskFragment()
        fragment.let {
            supportFragmentManager.beginTransaction().replace(R.id.homeContainer, fragment).commit()
        }

    }

    private fun backgroundAlarm1(){

        var hour3 = Calendar.getInstance()
        hour3.set(Calendar.HOUR_OF_DAY, 9)
        hour3.set(Calendar.MINUTE, 0)
        hour3.set(Calendar.SECOND, 0)


        Log.i("myApp", "hora3: ${hour3.timeInMillis}")

        NotificationUtils().setNotificationPr1(hour3.timeInMillis, this@MainActivity)


    }


    private fun backgroundAlarm2(){

        var hour3 = Calendar.getInstance()
        hour3.set(Calendar.HOUR_OF_DAY, 15)
        hour3.set(Calendar.MINUTE, 0)
        hour3.set(Calendar.SECOND, 0)

        Log.i("myApp", "hora3: ${hour3.timeInMillis}")

        NotificationUtils().setNotificationPr2(hour3.timeInMillis, this@MainActivity)


    }


    private fun backgroundAlarm3(){


        var hour3 = Calendar.getInstance()
            hour3.set(Calendar.HOUR_OF_DAY, 22)
            hour3.set(Calendar.MINUTE, 0)
            hour3.set(Calendar.SECOND, 0)


        Log.i("myApp", "hora3: ${hour3.timeInMillis}")


        NotificationUtils().setNotificationPr3(hour3.timeInMillis, this@MainActivity)


    }

    fun initialise(){
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)


        val header = nav_view.getHeaderView(0)

                    mUserReference.addValueEventListener(object : ValueEventListener {

                        override fun onCancelled(p0: DatabaseError) {
                            Log.d("MyApp", "Error $p0")
                        }
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var photo = snapshot.child("photo").value as? String
                            header.nombre.text = snapshot.child("firstName").value as String
                            header.apellido.text = snapshot.child("lastName").value as String
                            if (photo.isNullOrBlank()){
                            }else {
                                Picasso.get().load(snapshot.child("photo").value as String).into(header.ivProfile)
                            }
                        }
                    })

    }


}
