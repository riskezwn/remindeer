package com.riske.remindeer.Profile


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.riske.remindeer.R
import com.riske.remindeer.Tasks.TaskFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.photo
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class EditProfileFragment : Fragment() {

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null

    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var mAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    private lateinit var imageView: ImageView
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var tvEmail: TextView

    lateinit var photoUrl: String

    private var toolbar: BottomAppBar? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.bar)

        //Funciones BottomAppBar
        val button = activity!!.findViewById<View>(R.id.button1) as FloatingActionButton
        val bar = activity!!.findViewById<View>(R.id.bar) as BottomAppBar
        button.hide()
        bar.replaceMenu(R.menu.bar_editprofile)

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
        }

        photo.setOnClickListener { launchGallery()}

        initialise()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bar_editprofile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.confirm -> {
                uploadImage()
            }
            R.id.cancel -> {
                val backFg: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
                backFg.replace(R.id.homeContainer, ProfileFragment()).commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun initialise(){
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

        imageView = view!!.findViewById(R.id.ivProfilePhoto)
        etFirstName = view!!.findViewById(R.id.etFirstName)
        etLastName = view!!.findViewById(R.id.etLastName)
        tvEmail = view!!.findViewById(R.id.tvEmail)


        mUserReference.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                Log.d("MyApp", "Error $p0")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                photoUrl = snapshot.child("photo").value as String
                etFirstName.setText(snapshot.child("firstName").value as? String)
                etLastName.setText(snapshot.child("lastName").value as? String)
                tvEmail.text = snapshot.child("email").value as? String
                if (photoUrl.isNullOrBlank()){
                    imageView.setImageResource(R.drawable.defaultprofilephoto)
                }else {
                    Picasso.get().load(photoUrl).into(imageView)
                }
            }
        })


        Picasso.get().load(mUserReference.child("photo").toString()).into(imageView)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference


    }


    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }

            filePath = data.data
            try {
                var resolver = activity?.contentResolver
                val bitmap = MediaStore.Images.Media.getBitmap(resolver, filePath)
                imageView = view!!.findViewById(R.id.ivProfilePhoto)
                imageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            val ref = storageReference?.child("profilephotos/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

            Log.i("myApp", "FilePath $filePath")

                val urlTask =
                uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation ref.downloadUrl
                })?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        Log.i("myApp", "Uri ${downloadUri.toString()}")
                        addUploadRecordToDb(downloadUri.toString())
                    } else {
                        // Handle failures
                    }
                }?.addOnFailureListener {

                }
        } else {
            addUploadRecordToDb(photoUrl)
        }
    }

    private fun addUploadRecordToDb(uri: String) {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

        etFirstName = view!!.findViewById(R.id.etFirstName)
        etLastName = view!!.findViewById(R.id.etLastName)

        val newName = etFirstName.text.toString()
        val newApellido = etLastName.text.toString()

        val data = HashMap<String, Any>()
        data["imageUrl"] = uri

        Log.i("myApp", "$data")

        mUserReference.child("photo").setValue(uri)
        mUserReference.child("firstName").setValue(newName)
        mUserReference.child("lastName").setValue(newApellido)

            .addOnSuccessListener {
                Toast.makeText(context, "Has actualizado correctamente tus datos", Toast.LENGTH_LONG).show()
                backtoProfile()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
    }

    fun backtoProfile(){
        val backFg: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        backFg.replace(R.id.homeContainer, ProfileFragment()).commit()
    }


}
