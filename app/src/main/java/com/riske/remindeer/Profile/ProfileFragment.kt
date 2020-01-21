package com.riske.remindeer.Profile


import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.contentValuesOf
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.riske.remindeer.Calendar.makeInVisible
import com.riske.remindeer.MainActivity
import com.riske.remindeer.R
import com.riske.remindeer.Welcome.BienvenidaActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.nav_header.view.*
import java.io.IOException
import java.util.*
import java.util.zip.Inflater
import kotlin.collections.HashMap

class ProfileFragment : Fragment() {

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null

    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    private lateinit var imageView: ImageView
    private lateinit var etFirstName: TextView
    private lateinit var etLastName: TextView
    private lateinit var tvEmail: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Funciones BottomAppBar
        val button = activity!!.findViewById<View>(R.id.button1) as FloatingActionButton
        val bar = activity!!.findViewById<View>(R.id.bar) as BottomAppBar
        bar.replaceMenu(R.menu.empty_menu)
        button.show()
        button.setImageResource(R.drawable.ic_edit)
        button.setOnClickListener {
            var fragment = EditProfileFragment()
            fragment.let {
                fragmentManager?.beginTransaction()?.replace(R.id.homeContainer, fragment)?.commit()
            }
        }

        initialise()
    }


    fun initialise() {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

        imageView = view!!.findViewById(R.id.ivProfilePhoto)
        etFirstName = view!!.findViewById(R.id.tvName)
        etLastName = view!!.findViewById(R.id.tvApellido)
        tvEmail = view!!.findViewById(R.id.tvEmail)


            mUserReference.addValueEventListener(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("MyApp", "Error $p0")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var photo = snapshot.child("photo").value as? String
                    etFirstName.text = snapshot.child("firstName").value as? String
                    etLastName.text = snapshot.child("lastName").value as? String
                    tvEmail.text = snapshot.child("email").value as? String
                    if (photo.isNullOrBlank()) {
                       // Picasso.get().load(R.drawable.defaultprofilephoto).into(imageView)
                        imageView.setImageResource(R.drawable.defaultprofilephoto)
                    } else {
                        Picasso.get().load(snapshot.child("photo").value as String).into(imageView)
                    }
                }
            })


            Picasso.get().load(mUserReference.child("photo").toString()).into(imageView)

            firebaseStore = FirebaseStorage.getInstance()
            storageReference = FirebaseStorage.getInstance().reference

        buttonSignOut.setOnClickListener {
            signOut()
        }

        }

    private fun signOut(){
        mAuth!!.signOut()
        val intent = Intent(context, BienvenidaActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}