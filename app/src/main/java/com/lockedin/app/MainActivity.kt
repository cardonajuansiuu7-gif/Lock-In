package com.lockedin.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        recycler = findViewById(R.id.recyclerTasks)
        recycler.layoutManager = LinearLayoutManager(this)

        // Animación de entrada: todo el contenido aparece con fade-in
        findViewById<android.view.View>(R.id.rootContent)
            .animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(100)
            .start()

        val tasks = Prefs.getTasks(this)
        adapter = TaskAdapter(tasks) { saveAndRefresh() }
        recycler.adapter = adapter

        findViewById<Button>(R.id.btnAddTask).setOnClickListener {
            val input = EditText(this)
            AlertDialog.Builder(this)
                .setTitle("Nueva tarea")
                .setView(input)
                .setPositiveButton("Agregar") { _, _ ->
                    val text = input.text.toString().trim()
                    if (!TextUtils.isEmpty(text)) {
                        adapter.tasks.add(Task(text))
                        saveAndRefresh()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        findViewById<Button>(R.id.btnSelectApps).setOnClickListener {
            startActivity(Intent(this, AppSelectActivity::class.java))
        }

        findViewById<Button>(R.id.btnAccessibility).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            Toast.makeText(
                this,
                "Buscá 'Locked In' en la lista y activalo",
                Toast.LENGTH_LONG
            ).show()
        }

        findViewById<Button>(R.id.btnLock).setOnClickListener {
            val blocked = Prefs.getBlockedApps(this)
            if (blocked.isEmpty()) {
                Toast.makeText(this, "Primero seleccioná apps a bloquear", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (Prefs.getTasks(this).isEmpty()) {
                Toast.makeText(this, "Agregá al menos una tarea", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Prefs.setLocked(this, true)
            updateStatus()
            Toast.makeText(this, "Bloqueo activado. ¡A completar tareas!", Toast.LENGTH_SHORT).show()
        }

        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun saveAndRefresh() {
        Prefs.saveTasks(this, adapter.tasks)
        adapter.notifyDataSetChanged()
        updateStatus()
    }

    private fun updateStatus() {
        val locked = Prefs.isLocked(this)
        val blockedCount = Prefs.getBlockedApps(this).size
        tvStatus.text = if (locked) {
            "🔒 BLOQUEADO — $blockedCount app(s) bloqueadas hasta terminar la lista"
        } else {
            "🔓 Libre — $blockedCount app(s) seleccionadas"
        }
    }

    inner class TaskAdapter(
        val tasks: MutableList<Task>,
        val onChange: () -> Unit
    ) : RecyclerView.Adapter<TaskAdapter.VH>() {

        inner class VH(val view: android.view.View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): VH {
            val v = layoutInflater.inflate(R.layout.item_task, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val task = tasks[position]
            val cb = holder.view.findViewById<CheckBox>(R.id.cbTask)
            cb.setOnCheckedChangeListener(null)
            cb.text = task.text
            cb.isChecked = task.done
            cb.setOnCheckedChangeListener { _, checked ->
                task.done = checked
                if (checked) {
                    holder.view.animate()
                        .scaleX(1.03f).scaleY(1.03f)
                        .setDuration(120)
                        .withEndAction {
                            holder.view.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                        }.start()
                }
                onChange()
            }
            holder.view.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener {
                tasks.removeAt(holder.adapterPosition)
                onChange()
            }
        }

        override fun getItemCount() = tasks.size
    }
}
