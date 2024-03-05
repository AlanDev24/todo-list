package gonzalez.alan.todolist

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var et_tarea: EditText
    lateinit var btn_agregar: Button
    lateinit var listview_tareas: ListView
    lateinit var lista_tareas: ArrayList<String>
    lateinit var late_adapter: ArrayAdapter<String>
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        et_tarea = findViewById(R.id.et_tarea)
        btn_agregar = findViewById(R.id.btn_agregar)
        listview_tareas = findViewById(R.id.listview_tareas)

        // Obtener la instancia de SharedPreferences
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        // Cargar las tareas guardadas al iniciar la aplicación
        cargarTareasGuardadas()

        late_adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lista_tareas)
        listview_tareas.adapter = late_adapter

        btn_agregar.setOnClickListener {
            var tarea = et_tarea.text.toString()
            if (!tarea.isNullOrEmpty()) {
                lista_tareas.add(tarea)
                late_adapter.notifyDataSetChanged()
                et_tarea.setText("")
                // Guardar las tareas al agregar una nueva
                guardarTareas()
            } else {
                Toast.makeText(this, "Escribe una tarea", Toast.LENGTH_SHORT).show()
            }
        }

        listview_tareas.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                mostrarDialogoEditarEliminarTarea(position)
            }
    }

    private fun mostrarDialogoEditarEliminarTarea(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Editar/Eliminar Tarea")

        val input = EditText(this)
        input.setText(lista_tareas[position])
        builder.setView(input)

        builder.setPositiveButton("Guardar") { _, _ ->
            val nuevaTarea = input.text.toString()
            if (!nuevaTarea.isNullOrEmpty()) {
                lista_tareas[position] = nuevaTarea
                late_adapter.notifyDataSetChanged()
                guardarTareas()
            } else {
                Toast.makeText(this, "Escribe una tarea válida", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Eliminar") { _, _ ->
            lista_tareas.removeAt(position)
            late_adapter.notifyDataSetChanged()
            guardarTareas()
        }

        builder.setNeutralButton("Cancelar") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun guardarTareas() {
        // Guardar la lista de tareas en SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putStringSet("tareas", HashSet<String>(lista_tareas))
        editor.apply()
    }

    private fun cargarTareasGuardadas() {
        // Cargar las tareas guardadas desde SharedPreferences
        val tareasGuardadas = sharedPreferences.getStringSet("tareas", HashSet<String>())
        lista_tareas = ArrayList(tareasGuardadas)
    }
}