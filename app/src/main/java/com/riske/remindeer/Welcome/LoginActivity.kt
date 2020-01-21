package com.riske.remindeer.Welcome

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.riske.remindeer.R
import kotlinx.android.synthetic.main.activity_login.*
import com.riske.remindeer.MainActivity as MainActivity

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    //global variables
    private var email: String? = null
    private var password: String? = null
    //UI elements
    private var tvForgotPassword: TextView? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

       initialise()
    }


    private fun initialise() {
        tvForgotPassword = findViewById<View>(R.id.tvForgotPassword) as TextView
        etEmail = findViewById<View>(R.id.etEmail) as EditText
        etPassword = findViewById<View>(R.id.etPassword) as EditText
        btnLogin = findViewById<View>(R.id.btnLogin) as Button
        mAuth = FirebaseAuth.getInstance()
        btnLogin!!.setOnClickListener { loginUser() }
        tvForgotPassword!!
            .setOnClickListener {
                startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
            }
    }

    private fun loginUser() {
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressbar.visibility = View.VISIBLE

            mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                  progressbar.visibility = View.INVISIBLE
                    if (task.isSuccessful) {
                        // Sign in success, update UI with signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show()
                        updateUI()
                        onStop()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this, "Los datos de usuario no coinciden",
                            Toast.LENGTH_SHORT).show()
                    } }

        } else {
            Toast.makeText(this, "Introduce tus datos", Toast.LENGTH_SHORT).show()
        }
    }


        private fun updateUI() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}