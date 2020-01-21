package com.riske.remindeer.Tasks


import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.riske.remindeer.Models.Task
import com.riske.remindeer.R
import kotlinx.android.synthetic.main.item_task.view.*
import kotlinx.android.synthetic.main.item_task.view.imgTarea
import kotlinx.android.synthetic.main.item_task.view.isDone
import kotlinx.android.synthetic.main.item_task.view.tvDescripcion
import kotlinx.android.synthetic.main.item_task.view.tvHora
import kotlinx.android.synthetic.main.item_task.view.tvTitulo
import kotlinx.android.synthetic.main.item_taskdone.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class TaskDoneFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    private lateinit var adapter: FirebaseRecyclerAdapter<*, *>
    private var recycler: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.taskdone_fragment, container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler = view.findViewById(R.id.recyclerTaskDone) as RecyclerView

        //Funciones BottomAppBar
        val button = activity!!.findViewById<View>(R.id.button1) as FloatingActionButton
        val bar = activity!!.findViewById<View>(R.id.bar) as BottomAppBar
        button.hide()
        //bar.replaceMenu(R.menu.bar_editprofile)

        initialise()
    }

    fun initialise(){


        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference

        setUpRecycler()
    }

    private fun setUpRecycler() {
        val query = FirebaseDatabase.getInstance()
            .reference
            .child("task_table")
            .child(mAuth?.currentUser?.uid.toString())
            .orderByChild("done").equalTo(true)
        val options = FirebaseRecyclerOptions.Builder<Task>()
            .setQuery(query, Task::class.java)
            .build()
        recycler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapter = object : FirebaseRecyclerAdapter<Task?, ViewHolder?>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { // Create a new instance of the ViewHolder, in this case we are using a custom
                val view: View = LayoutInflater.from(context).inflate(R.layout.item_taskdone, parent, false)
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
                holder.titleTodo?.text = item.titulo
                holder.descripcionTodo?.text = item.descripcion
                holder.dateTimeStamp?.text = getDateTime(item.timeStamp!!)
                holder.deleteTask.setOnClickListener {
                    removeItem(snapshots[holder.adapterPosition])
                }


                when (item.categoria) {
                    0 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_comidaoff)
                    1 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_reunionoff)
                    2 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_friends11off)
                    3 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_gymdeporteoff)
                    4 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_descansooff)
                    5 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_trabajooff)
                    6 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_ociooff)
                    7 -> holder.categoriaTodo.setImageResource(R.drawable.ic_familyoff)
                    8 -> holder.categoriaTodo.setImageResource(R.drawable.ic_svg_otrosoff)
                }

            }
        }
        recycler?.adapter = adapter
    }


    private fun removeItem(task: Task){
        mDatabaseReference!!.child("task_table").child(mAuth?.currentUser?.uid.toString()).child(task.objectId!!).removeValue()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }
    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTodo = view.tvTitulo
        val descripcionTodo = view.tvDescripcion
        val categoriaTodo = view.imgTarea
        val dateTimeStamp: TextView? = view.tvHora
        val deleteTask = view.delete
    }

    }




