package com.example.registrocucajulia
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream

class Empleado : AppCompatActivity() {

    lateinit var txtid: EditText
    lateinit var txtnombre: EditText
    lateinit var txtmiercoles: EditText
    lateinit var txtjueves: EditText
    lateinit var txtviernes: EditText
    lateinit var txtsabado: EditText
    lateinit var txtdomingo: EditText
    lateinit var txtlunes: EditText
    lateinit var txthoraextra: EditText
    private val REQUEST_IMAGE_CAPTURE = 1
    var txtRostro:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregarempleado)

        txtid = findViewById(R.id.txtid)
        txtid.keyListener = null
        txtnombre = findViewById(R.id.txtnombre)
        txtmiercoles = findViewById(R.id.txtmiercoles)
        txtjueves = findViewById(R.id.txtjueves)
        txtviernes = findViewById(R.id.txtviernes)
        txtsabado = findViewById(R.id.txtsabado)
        txtdomingo = findViewById(R.id.txtdomingo)
        txtlunes = findViewById(R.id.txtlunes)
        txthoraextra = findViewById(R.id.txthoraextra)

        // Obtener el último ID y establecer el siguiente ID
        val ultimoID = obtenerUltimoID()
        txtid.setText((ultimoID + 1).toString())

        val btnGuardar = findViewById<FloatingActionButton>(R.id.btnguardar)

        btnGuardar.setOnClickListener {
            if (validarCampos()) {
                guardarEmpleadoYPagos()   //agregarDatos(it)
            }
        }




        val btnvolvera: Button = findViewById(R.id.btnvolvera)
        btnvolvera.setOnClickListener {
            val intent1 = Intent(this, Menuopciones::class.java)
            startActivity(intent1)
        }




        val btnTomarFoto: Button = findViewById(R.id.btnrostro)
        btnTomarFoto.setOnClickListener {
            // Lanzar la cámara para tomar una foto del rostro
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }



    // Función para validar si los campos están vacíos
    private fun validarCampos(): Boolean {
        val campos = listOf(
            txtnombre, txtmiercoles, txtjueves, txtviernes, txtsabado, txtdomingo, txtlunes, txthoraextra //
        )
        for (campo in campos) {
            if (campo.text.toString().isEmpty()) {
                campo.error = "Este campo no puede estar vacío"
                return false
            }
        }
        if (txtRostro.isEmpty()) {
            Toast.makeText(this, "Debes tomar una foto del rostro", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }




    //funcion para verificar si el id ya existe en la base de datos
    private fun esIdUnico(id: Int): Boolean {
        val db = Room.databaseBuilder(applicationContext, DataBase::class.java, "registro").allowMainThreadQueries().build()
        val empleado = db.empleadoDao().getEmpleadoById(id)
        return empleado == null
    }





    //codigo para guardar los atributos de empleado y pago en pago
    private fun guardarEmpleadoYPagos() {
        val idEmpleadoGenerado = guardarEmpleado()

        if (idEmpleadoGenerado != -1L) {
            guardarPago(idEmpleadoGenerado, "miercoles", txtmiercoles)
            guardarPago(idEmpleadoGenerado, "jueves", txtjueves)
            guardarPago(idEmpleadoGenerado, "viernes", txtviernes)
            guardarPago(idEmpleadoGenerado, "sabado", txtsabado)
            guardarPago(idEmpleadoGenerado, "domingo", txtdomingo)
            guardarPago(idEmpleadoGenerado, "lunes", txtlunes)

            // Limpiar campos después de guardar
            limpiarCampos()
        } else {
            // Manejar el caso en que no se pudo guardar el empleado
        }
    }



    private fun guardarEmpleado(): Long {
        val id = txtid.text.toString().toInt()
        val nombre = txtnombre.text.toString()
        val horaextra = txthoraextra.text.toString().toInt()
        val rostro = txtRostro

        // Aquí realizamos la validación de id único
        if (esIdUnico(id)) {
            val registro = empleadoentity(id.toLong(), nombre, horaextra, rostro)

            // Establecer el estatus como True al agregar un empleado
            val db = Room.databaseBuilder(applicationContext, DataBase::class.java, "registro").allowMainThreadQueries().build()
            val empleadoId = db.empleadoDao().insertEmpleado(registro.copy(estatus = true))

            Toast.makeText(this, "Empleado guardado con ID: $empleadoId", Toast.LENGTH_SHORT).show()

            // Limpiar campos
            val campos = listOf(
                txtnombre, txthoraextra //txtmiercoles, txtjueves, txtviernes, txtsabado, txtdomingo, txtlunes,
            )
            for (campo in campos) {
                campo.text.clear()
            }
            // Limpiar el atributo de rostro
            txtRostro = ""

            // Actualizar el ID
            val ultimoID = obtenerUltimoID()
            txtid.setText((ultimoID + 1).toString())

            // Establecer la imagen predeterminada en el ImageView
            val imageViewRostro = findViewById<ImageView>(R.id.imgrostro)
            imageViewRostro.setImageResource(R.drawable.fotoperfil)

            // Devolver el ID del empleado generado
            return empleadoId
        } else {
            // Manejar el caso en que no se pudo guardar el empleado
            return -1L
        }
    }


    private fun guardarPago(idEmpleado: Long, dia: String, txtMonto: EditText) {
        val fechaPago = dia
        val montoPago = txtMonto.text.toString().toDouble()

        val pago = Pago(IDEmpleado = idEmpleado, FechaPago = fechaPago, MontoPago = montoPago)

        // Lógica para guardar el pago en la base de datos
        val db = Room.databaseBuilder(applicationContext, DataBase::class.java, "registro").allowMainThreadQueries().build()
        val pagoId = db.pagoDao().insertPago(pago)



            }


    private fun limpiarCampos() {
        // Limpiar campos de pagos
        txtmiercoles.text.clear()
        txtjueves.text.clear()
        txtviernes.text.clear()
        txtsabado.text.clear()
        txtdomingo.text.clear()
        txtlunes.text.clear()
    }




    // Función para obtener el último ID
    private fun obtenerUltimoID(): Long {
        val db = Room.databaseBuilder(applicationContext, DataBase::class.java, "registro").allowMainThreadQueries().build()
        val ultimoEmpleado = db.empleadoDao().obtenerUltimoEmpleado()
        return ultimoEmpleado?.id ?: 0
    }








    //codigos de manipulacion de la imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            if (imageBitmap != null) {
                // Convierte el bitmap a una representación de cadena (Base64) y guárdalo en el atributo rostro
                val rostroBase64 = convertBitmapToBase64(imageBitmap)
                txtRostro = rostroBase64

                // Muestra la imagen en el ImageView
                val imageViewRostro = findViewById<ImageView>(R.id.imgrostro)
                imageViewRostro.setImageBitmap(imageBitmap)
            }
        }
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }






}
