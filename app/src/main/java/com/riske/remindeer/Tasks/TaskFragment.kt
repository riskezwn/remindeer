package com.riske.remindeer.Tasks


import android.app.PendingIntent.getActivity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.riske.remindeer.Models.Task
import com.riske.remindeer.Notifications.NotificationUtils
import com.riske.remindeer.Profile.EditProfileFragment
import com.riske.remindeer.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.editar_tareas.*
import kotlinx.android.synthetic.main.editar_tareas.view.*
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.android.synthetic.main.item_task.view.*
import kotlinx.android.synthetic.main.nuevo_anadir_tareas.view.*
import kotlinx.android.synthetic.main.nuevo_anadir_tareas.view.dialogContinueBtn
import java.security.AccessController
import java.security.AccessController.getContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.contracts.contract
import kotlin.coroutines.coroutineContext



class TaskFragment : Fragment() {


    //Firebase
    private var mAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabaseReferenceTasks: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null


    //Fecha
    private val date: Date? = Calendar.getInstance().time
    private val formatter: DateFormat? = SimpleDateFormat.getDateInstance()
    private val formattedDate: String? = formatter?.format(date!!) // FECHA ACTUAL FORMATEADA
    private var fechaActual: TextView? = null

    //OnSwipe
    private lateinit var adapter: FirebaseRecyclerAdapter<*, *>
    private val p = Paint()
    private var recycler: RecyclerView? = null

    //UI
    private var rootLayout: View? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_task, container, false)
        rootLayout = activity!!.findViewById(R.id.snackbar) as View
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler = view.findViewById(R.id.recyclerTask) as RecyclerView
        fechaActual = view.findViewById(R.id.tvFechaActual) as? TextView

        //Funciones BottomAppBar
        val button = activity!!.findViewById<View>(R.id.button1) as FloatingActionButton
        val bar = activity!!.findViewById<View>(R.id.bar) as BottomAppBar
        button.show()
        bar.replaceMenu(R.menu.empty_menu)
        button.setImageResource(R.drawable.ic_add)

        button.setOnClickListener {
            alertTaskDialog()
        }
        initialise()
        setUpRecycler()
        actualDate()

    }



    private fun initialise() {

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mDatabaseReferenceTasks = mDatabase!!.reference

        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

        mUserReference.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                Log.d("MyApp", "Error $p0")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                var photo = snapshot.child("photo").value as? String
                tvNombreCliente?.text = snapshot.child("firstName").value as String
                if (photo.isNullOrBlank()) {
                } else {
                    Log.i("myApp", "$photo")
                    if (profile_image != null)
                        Picasso.get().load(photo).into(profile_image)
                }
                numeroTareas()
            }
        })

    }

    private fun alertEditTask(task: Task) {
        val mDialogView2 =
            LayoutInflater.from(context).inflate(R.layout.editar_tareas, null)
        //AlertDialogBuilder
        val mBuilder =
            AlertDialog.Builder(context!!, R.style.CustomAlertDialog)
                .setView(mDialogView2)
        //show dialog
        val mAlertDialog2: AlertDialog = mBuilder.show()
        val categoriesSpinnerEditar =
            mDialogView2.findViewById<Spinner>(R.id.categoriesSpinnerEditar)
        val image2 = mDialogView2.findViewById<ImageView>(R.id.imgCategoriaAlertEditar)
        mAlertDialog2.tareaTituloEditar.setText(task.titulo)
        mAlertDialog2.tareaDescripcionEditar.setText(task.descripcion)
        categoriesSpinnerEditar.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        0 -> image2.setImageResource(R.drawable.ic_svg_comida)
                        1 -> image2.setImageResource(R.drawable.ic_svg_reunion)
                        2 -> image2.setImageResource(R.drawable.ic_svg_friends11)
                        3 -> image2.setImageResource(R.drawable.ic_svg_gymdeporte)
                        4 -> image2.setImageResource(R.drawable.ic_svg_descanso)
                        5 -> image2.setImageResource(R.drawable.ic_svg_trabajo)
                        6 -> image2.setImageResource(R.drawable.ic_svg_ocio)
                        7 -> image2.setImageResource(R.drawable.ic_family)
                        8 -> image2.setImageResource(R.drawable.ic_svg_otros)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        mDialogView2.dialogContinueBtn.setOnClickListener {
            mAuth = FirebaseAuth.getInstance()
            mDatabase = FirebaseDatabase.getInstance()
            mDatabaseReference = mDatabase!!.reference.child("task_table")
            val mUser = mAuth!!.currentUser
            val mUserReferenceTasks = mDatabaseReference!!.child(mUser!!.uid).child(task.objectId.toString())
            val newTitleTarea =  mAlertDialog2.tareaTituloEditar.text.toString()
            val newDescriptionTarea =  mAlertDialog2.tareaDescripcionEditar.text.toString()
            val newtaskCategory2 = mDialogView2.categoriesSpinnerEditar.selectedItemPosition
            val data = HashMap<String, Any>()
            data["titulo"] = newTitleTarea
            data["descripcion"] = newDescriptionTarea
            data["categoria"] = newtaskCategory2
            mUserReferenceTasks.updateChildren(data).addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Has actualizado correctamente la tarea",
                    Toast.LENGTH_LONG
                ).show()
                mAlertDialog2.dismiss()
            }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Error al actualizar la tarea, prueba de nuevo :-)",
                        Toast.LENGTH_LONG
                    ).show()
                }

        }

    }

    fun alertTaskDialog() {
        //Inflate the dialog with custom view
        val mDialogView =
            LayoutInflater.from(context).inflate(R.layout.nuevo_anadir_tareas, null)
        //AlertDialogBuilder
        val mBuilder =
            android.app.AlertDialog.Builder(context, R.style.CustomAlertDialog)
                .setView(mDialogView)
        //show dialog
        val mAlertDialog = mBuilder.show()
        //login button click of custom layout
        val categoriesSpinner = mDialogView.findViewById<Spinner>(R.id.categoriesSpinner)
        val image = mDialogView.findViewById<ImageView>(R.id.imgCategoriaAlert)
        categoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> image.setImageResource(R.drawable.ic_svg_comida)
                    1 -> image.setImageResource(R.drawable.ic_svg_reunion)
                    2 -> image.setImageResource(R.drawable.ic_svg_friends11)
                    3 -> image.setImageResource(R.drawable.ic_svg_gymdeporte)
                    4 -> image.setImageResource(R.drawable.ic_svg_descanso)
                    5 -> image.setImageResource(R.drawable.ic_svg_trabajo)
                    6 -> image.setImageResource(R.drawable.ic_svg_ocio)
                    7 -> image.setImageResource(R.drawable.ic_family)
                    8 -> image.setImageResource(R.drawable.ic_svg_otros)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        mDialogView.dialogContinueBtn.setOnClickListener {

            val task = Task()
            val taskCategory = mDialogView.categoriesSpinner.selectedItemPosition
            Log.i("myApp", "$taskCategory")
            val taskTitle = mDialogView.findViewById<EditText>(R.id.tareaTítulo)
            val taskDescription = mDialogView.findViewById<EditText>(R.id.tareaDescripcion)
            val updatedYear = mDialogView.datePickerTarea.year
            val updatedMonth = mDialogView.datePickerTarea.month + 1
            val updatedDayofMonth = mDialogView.datePickerTarea.dayOfMonth
            val updatedHour = mDialogView.timePickerTarea.hour
            val updatedMinute = mDialogView.timePickerTarea.minute
            var updatedDateTime =
                "$updatedYear-$updatedMonth-$updatedDayofMonth, $updatedHour:$updatedMinute"
            val dateAndTimeStamp = SimpleDateFormat("yyyy-MM-dd, HH:mm").parse(updatedDateTime)
           // Log.v("myapp", "hola ${dateAndTimeStamp?}")
            val time20 = dateAndTimeStamp.time - 1200000

            task.titulo = taskTitle.text.toString()
            task.descripcion = taskDescription.text.toString()
            task.categoria = taskCategory
            task.timeStamp = dateAndTimeStamp?.time.toString()

            if ("" == taskTitle.text.toString().trim() || "" == taskDescription.text.toString().trim()) {
                Toast.makeText(
                    context,
                    "Rellena todos los campos de tu tarea porfa! ;-)",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //We first make a push so that a new item is made with a unique ID
                val newItem = mDatabaseReferenceTasks!!.child("task_table")
                    .child(mAuth?.currentUser?.uid.toString()).push()
                task.objectId = newItem.key
                newItem.setValue(task)
                Toast.makeText(context, "Tu nueva tarea ya se ha guardado :-)", Toast.LENGTH_SHORT)
                    .show()
                mAlertDialog.dismiss()
                NotificationUtils().setNotificationTask(time20, activity!!)
                initialise()
            }
        }

    }


    private fun setUpRecycler() {

        val query = FirebaseDatabase.getInstance()
            .reference
            .child("task_table")
            .child(mAuth?.currentUser?.uid.toString())
            .orderByChild("done").equalTo(false)


        val options = FirebaseRecyclerOptions.Builder<Task>()
            .setQuery(query, Task::class.java)
            .build()


        val simpleItemTouchCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val prueba = viewHolder.itemId

                    Log.i("myApp", "$prueba")

                    // val layoutSnackbar = rootLayout
                    //layoutSnackbar?.foregroundGravity = Gravity.TOP

                    if (direction == ItemTouchHelper.LEFT) {

                        Log.i("myApp", "Left")

                        updateItem(position)


                        val snackbar = Snackbar.make(
                            rootLayout!!, "Tarea completada",
                            Snackbar.LENGTH_LONG
                        )
                        snackbar.setActionTextColor(Color.YELLOW)
                        snackbar.show()
                    } else {


                        Log.i("myApp", "Right")

                        removeItem(position)

                        val snackbar = Snackbar.make(
                            rootLayout!!,
                            "Tarea eliminada",
                            Snackbar.LENGTH_LONG
                        )
                        snackbar.setActionTextColor(Color.YELLOW)
                        snackbar.show()
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {

                    val icon: Bitmap
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                        val itemView = viewHolder.itemView
                        val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                        val width = height / 3

                        if (dX > 0) {
                            p.color = Color.parseColor("#ff6659")
                            val background =
                                RectF(
                                    itemView.left.toFloat(),
                                    itemView.top.toFloat(),
                                    dX,
                                    itemView.bottom.toFloat()
                                )
                            c.drawRect(background, p)
                            icon = BitmapFactory.decodeResource(resources, R.drawable.ic_delete2)
                            val icon_dest = RectF(
                                itemView.left.toFloat() + width,
                                itemView.top.toFloat() + width,
                                itemView.left.toFloat() + 2 * width,
                                itemView.bottom.toFloat() - width
                            )
                            c.drawBitmap(icon, null, icon_dest, p)
                        } else {
                            p.color = Color.parseColor("#6abf69")
                            val background = RectF(
                                itemView.right.toFloat() + dX,
                                itemView.top.toFloat(),
                                itemView.right.toFloat(),
                                itemView.bottom.toFloat()
                            )
                            c.drawRect(background, p)
                            icon = BitmapFactory.decodeResource(resources, R.drawable.ic_check2)
                            val icon_dest = RectF(
                                itemView.right.toFloat() - 2 * width,
                                itemView.top.toFloat() + width,
                                itemView.right.toFloat() - width,
                                itemView.bottom.toFloat() - width
                            )
                            c.drawBitmap(icon, null, icon_dest, p)
                        }
                    }
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recycler)

        adapter = MainTaskAdapter(options){
            alertEditTask(it)
        }
        recycler?.layoutManager = LinearLayoutManager(context)
        recycler?.adapter = adapter


    }

    fun removeItem(position: Int) {
        val task = DataHolder.allData[position]

        Log.i("myApp", "${task.titulo}")
        mDatabaseReferenceTasks!!.child("task_table").child(mAuth?.currentUser?.uid.toString())
            .child(task.objectId!!)
            .removeValue()
        DataHolder.allData.remove(task)

        DataHolder.allData.forEach {
            Log.i("myApp", "${it.titulo}")
        }

        initialise()
    }

    fun updateItem(position: Int) {
        val task = DataHolder.allData[position]


        Log.i("myApp", "${task.done}")
        mDatabaseReferenceTasks!!.child("task_table").child(mAuth?.currentUser?.uid.toString())
            .child(task.objectId!!).child("done").setValue(true)
        DataHolder.allData.remove(task)

        DataHolder.allData.forEach {
            Log.i("myApp", "${it.done}")
        }

        initialise()

    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        adapter.startListening()
        initialise()
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


    private fun actualDate() {
        fechaActual?.text = formattedDate
        Log.i("myApp", "$formattedDate")
    }

    private fun numeroTareas() {
        if (recyclerTask != null) {
            val itemsRecycler = recyclerTask.adapter!!.itemCount
            when {
                itemsRecycler == 1 -> {
                    tvTareasPendientes?.text = "1 tarea pendiente para hoy"
                }
                itemsRecycler != 0 -> {
                    tvTareasPendientes?.text = "${itemsRecycler} tareas pendientes para hoy"
                }
                else -> {
                    tvTareasPendientes?.text = "Añade tu primera tarea ;-)"
                }
            }
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTodo = view.tvTitulo
        val descripcionTodo = view.tvDescripcion
        val categoriaTodo = view.imgTarea
        val dateTimeStamp: TextView? = view.tvHora

    }

    class MainTaskAdapter(options: FirebaseRecyclerOptions<Task>, var editTask: (tarea: Task) -> Unit) :
        FirebaseRecyclerAdapter<Task, ViewHolder?>(options) {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task, parent, false)
            return ViewHolder(view)
        }

        private fun getDateTime(s: String): String {
            return try {
                val sdf = SimpleDateFormat("HH:mm")
                val netDate = Date(s.toLong())
                sdf.format(netDate)
            } catch (e: Exception) {
                e.toString()
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, p1: Int, item: Task) {
            DataHolder.allData.add(item)

            holder.titleTodo?.text = item.titulo
            holder.descripcionTodo?.text = item.descripcion
            holder.dateTimeStamp?.text = getDateTime(item.timeStamp!!)

            holder.itemView.setOnClickListener {
                editTask.invoke(item)
            }

            when (item.categoria) {
                0 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_comida)
                1 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_reunion)
                2 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_friends11)
                3 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_gymdeporte)
                4 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_descanso)
                5 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_trabajo)
                6 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_ocio)
                7 -> holder.categoriaTodo.setImageResource(R.drawable.ic_family)
                8 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_otros)
            }


        }


    }

    object DataHolder {
        var allData: MutableList<Task> = arrayListOf()

    }

}