package com.example.tallerprisila

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditarProformaActivity : AppCompatActivity() {

    private lateinit var editCliente: EditText
    private lateinit var editObservaciones: EditText
    private lateinit var listViewItems: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var btnAgregarItem: Button
    private lateinit var btnGuardarCambios: Button
    private lateinit var listView: ListView


    private val db = FirebaseFirestore.getInstance()
    private var proformaId: String? = null
    private var items = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_proforma)

        editCliente = findViewById(R.id.editCliente)
        editObservaciones = findViewById(R.id.editObservaciones)
        listViewItems = findViewById(R.id.listViewItems)
        btnAgregarItem = findViewById(R.id.btnAgregarItem)
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios)
        listView = findViewById(R.id.listViewItems)


        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listViewItems.adapter = adapter

        proformaId = intent.getStringExtra("id")
        if (proformaId != null) {
            cargarDatos()
        }

        btnAgregarItem.setOnClickListener {
            mostrarDialogoAgregarItem()
        }

        //logica de edicion de items
        //listViewItems.setOnItemClickListener { _, _, position, _ ->
          //  mostrarDialogoEliminarItem(position)
        //}

        //nueva logica de edicion de items
        listView.setOnItemClickListener { _, _, position, _ ->
            val itemSeleccionado = items[position]

            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogo_item, null)
            val editDescripcion = dialogView.findViewById<EditText>(R.id.dialogDescripcion)
            val editCantidad = dialogView.findViewById<EditText>(R.id.dialogCantidad)
            val editPrecio = dialogView.findViewById<EditText>(R.id.dialogPrecio)

            // Llenar los campos con los datos actuales del ítem
            editDescripcion.setText(itemSeleccionado.descripcion)
            editCantidad.setText(itemSeleccionado.cantidad.toString())
            editPrecio.setText(itemSeleccionado.precio.toString())

            val dialog = AlertDialog.Builder(this)
                .setTitle("Editar o Eliminar Ítem")
                .setView(dialogView)
                .setPositiveButton("Guardar cambios") { _, _ ->
                    val nuevaDescripcion = editDescripcion.text.toString()
                    val nuevaCantidad = editCantidad.text.toString().toIntOrNull() ?: 0
                    val nuevoPrecio = editPrecio.text.toString().toDoubleOrNull() ?: 0.0

                    // Actualizar el ítem en la lista
                    items[position] = Item(nuevaDescripcion, nuevaCantidad, nuevoPrecio)
                    actualizarLista() // función que refresca la vista del ListView
                }
                .setNegativeButton("Eliminar") { _, _ ->
                    items.removeAt(position)
                    actualizarLista()
                }
                .setNeutralButton("Cancelar", null)
                .create()

            dialog.show()
        }


        btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }
    }
    private fun actualizarLista() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            items.map { "${it.descripcion} - ${it.cantidad} x ${it.precio} Bs" }
        )
        listView.adapter = adapter
    }




    private fun cargarDatos() {
        db.collection("proformas").document(proformaId!!)
            .get()
            .addOnSuccessListener { document ->
                editCliente.setText(document.getString("cliente"))
                editObservaciones.setText(document.getString("observaciones"))
                val itemsMap = document["items"] as? List<Map<String, Any>>
                itemsMap?.forEach {
                    val descripcion = it["descripcion"] as String
                    val cantidad = (it["cantidad"] as Long).toInt()
                    val precio = it["precio"] as Double
                    items.add(Item(descripcion, cantidad, precio))
                }
                actualizarLista()
            }
    }

    //private fun actualizarLista() {
      //  val itemStrings = items.map { "${it.descripcion} - ${it.cantidad} x ${it.precio} Bs" }
        //adapter.clear()
        //adapter.addAll(itemStrings)
        //adapter.notifyDataSetChanged()
    //}

    private fun mostrarDialogoAgregarItem() {
        val dialogView = layoutInflater.inflate(R.layout.dialogo_item, null)
        val descripcionEdit = dialogView.findViewById<EditText>(R.id.dialogDescripcion)
        val cantidadEdit = dialogView.findViewById<EditText>(R.id.dialogCantidad)
        val precioEdit = dialogView.findViewById<EditText>(R.id.dialogPrecio)

        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Agregar ítem")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val descripcion = descripcionEdit.text.toString()
                val cantidad = cantidadEdit.text.toString().toIntOrNull() ?: 1
                val precio = precioEdit.text.toString().toDoubleOrNull() ?: 0.0
                items.add(Item(descripcion, cantidad, precio))
                actualizarLista()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun mostrarDialogoEliminarItem(posicion: Int) {
        val item = items[posicion]
        android.app.AlertDialog.Builder(this)
            .setTitle("Eliminar ítem")
            .setMessage("¿Eliminar '${item.descripcion}'?")
            .setPositiveButton("Sí") { _, _ ->
                items.removeAt(posicion)
                actualizarLista()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun guardarCambios() {
        val cliente = editCliente.text.toString()
        val observaciones = editObservaciones.text.toString()
        val total = items.sumOf { it.cantidad * it.precio }

        val data = mapOf(
            "cliente" to cliente,
            "observaciones" to observaciones,
            "items" to items.map {
                mapOf(
                    "descripcion" to it.descripcion,
                    "cantidad" to it.cantidad,
                    "precio" to it.precio
                )
            },
            "total" to total
        )

        db.collection("proformas").document(proformaId!!)
            .update(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Proforma actualizada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
    }
}
