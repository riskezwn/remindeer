package com.riske.remindeer.Welcome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.riske.remindeer.MainActivity
import com.riske.remindeer.R
import kotlinx.android.synthetic.main.activity_bienvenida.*


class BienvenidaActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    //SignIn Google
    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private var mGoogleSignInAccount: GoogleSignInAccount? = null
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida)
        configureGoogleSignIn()

        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        firebaseAuth = FirebaseAuth.getInstance()
        initialise()


        btnGoogle.setOnClickListener {
            signIn()
        }
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                var account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.i("myApp", "Google sign in failed in OnActivityResult")
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Sesión iniciada con Google correctamente", Toast.LENGTH_LONG).show()
                saveToDb()
                updateUI()
            } else {
                Toast.makeText(this, "Ups, algo ha fallado", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun saveToDb(){
        val firstName:String = mGoogleSignInAccount?.givenName!!
        val lastName:String = mGoogleSignInAccount?.familyName!!
        val photoLowQuality = mGoogleSignInAccount?.photoUrl!!.toString()
        val email:String = mGoogleSignInAccount?.email!!

        //Resize photo
        val originalPieceOfUrl = "s96-c"
        val newPieceOfUrlToAdd = "s400-c"
        val photo = photoLowQuality.replace(originalPieceOfUrl, newPieceOfUrlToAdd)

        val userId = mAuth!!.currentUser!!.uid

                val currentUserDb = mDatabaseReference!!.child(userId)
                currentUserDb.child("firstName").setValue(firstName)
                currentUserDb.child("lastName").setValue(lastName)
                currentUserDb.child("email").setValue(email)
                if (photo.isNullOrBlank()) {
                    currentUserDb.child("photo").setValue("")
                } else {
                    currentUserDb.child("photo").setValue(photo)
                }
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            updateUI()
            finish()
        }
    }

    fun initialise(){
        btnIniciar.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        btnRegistrar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        mAuth = FirebaseAuth.getInstance()
    }

    private fun updateUI() {
    val intent = Intent(this@BienvenidaActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
    }
    }
