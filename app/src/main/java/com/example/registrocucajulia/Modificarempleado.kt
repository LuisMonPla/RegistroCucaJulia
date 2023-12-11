package com.example.registrocucajulia

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream

class Modificarempleado : AppCompatActivity() {
    lateinit var txtidd: EditText
    lateinit var txtnombre: EditText
    lateinit var txtmiercoles: EditText
    lateinit var txtjueves: EditText
    lateinit var txtviernes: EditText
    lateinit var txtsabado: EditText
    lateinit var txtdomingo: EditText
    lateinit var txtlunes: EditText
    lateinit var txthoraextra: EditText
    //lateinit var txtrostro: EditText
    private val REQUEST_IMAGE_CAPTURE = 1
    var txtRostro:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificarempleado)

        // Inicializa los campos de EditText
        txtidd = findViewById(R.id.txtidd)
        txtnombre = findViewById(R.id.txtnombre)
        txtmiercoles = findViewById(R.id.txtmiercoles)
        txtjueves = findViewById(R.id.txtjueves)
        txtviernes = findViewById(R.id.txtviernes)
        txtsabado = findViewById(R.id.txtsabado)
        txtdomingo = findViewById(R.id.txtdomingo)
        txtlunes = findViewById(R.id.txtlunes)
        txthoraextra = findViewById(R.id.txthoraextra)

        // Recibe el ID del empleado que deseas modificar (por ejemplo, desde un intent extra)
        val empleadoId = intent.getLongExtra("empleadoId", -1)

        val db = Room.databaseBuilder(applicationContext, DataBase::class.java, "registro").allowMainThreadQueries().build()


        if (empleadoId != -1L) {
            // Carga los datos del empleado actual desde la base de datos y configura los campos de EditText

            val empleadoLiveData = db.empleadoDao().getEmpleadoByIdLiveData(empleadoId)

            empleadoLiveData.observe(this, Observer { empleado ->
                if (empleado != null) {
                    // Cargar los datos del empleado en los campos correspondientes
                    txtnombre.setText(empleado.nombre)
                    txthoraextra.setText(empleado.horaextra.toString())
                    txtRostro = (empleado.rostro)
                } else {
                    // Mostrar un mensaje si el empleado no se encuentra
                    Toast.makeText(this, "Empleado no encontrado", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val btnTomarFoto: Button = findViewById(R.id.btnrostro)
        btnTomarFoto.setOnClickListener {
            // Lanzar la cámara para tomar una foto del rostro
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }

        val btnSave = findViewById<FloatingActionButton>(R.id.btnguardar)
        btnSave.setOnClickListener {
            // Obtén los nuevos valores de los campos de EditText
            val nuevoNombre = txtnombre.text.toString()
            val nuevoHoraextra = txthoraextra.text.toString().toIntOrNull() ?: 0
            val nuevoRostro = txtRostro

            val empleadoId = txtidd.text.toString().toIntOrNull() ?: 0

            // Verifica si algún EditText está vacío
            if (empleadoId == 0 || nuevoNombre.isEmpty() || nuevoHoraextra == 0 || nuevoRostro.isEmpty()) {  //|| nuevoMiercoles == 0 || nuevoJueves == 0 || nuevoViernes == 0 || nuevoSabado == 0 || nuevoDomingo == 0 || nuevoLunes == 0
                // Muestra un mensaje de error si algún campo está vacío
                Toast.makeText(this, "Todos los campos deben estar llenos", Toast.LENGTH_SHORT).show()
            } else {
                // Verifica si el empleado existe en la base de datos
                val empleadoExistente = db.empleadoDao().getEmpleadoById(empleadoId)
                if (empleadoExistente != null) {
                    // Actualiza el registro del empleado en la base de datos
                    val empleadoModificado = empleadoentity(empleadoId.toLong(), nuevoNombre,  nuevoHoraextra, nuevoRostro, true) //nuevoMiercoles, nuevoJueves, nuevoViernes, nuevoSabado, nuevoDomingo, nuevoLunes,
                    db.empleadoDao().updateEmpleado(empleadoModificado)

                    // Muestra un mensaje de éxito
                    Toast.makeText(this, "Empleado actualizado con éxito", Toast.LENGTH_SHORT).show()

                } else {
                    // Muestra un mensaje de error si el empleado no existe
                    Toast.makeText(this, "El empleado no existe en la base de datos", Toast.LENGTH_SHORT).show()
                }
                // Actualiza los pagos en la base de datos
                actualizarPago(empleadoId.toLong(), "miercoles", txtmiercoles)
                actualizarPago(empleadoId.toLong(), "jueves", txtjueves)
                actualizarPago(empleadoId.toLong(), "viernes", txtviernes)
                actualizarPago(empleadoId.toLong(), "sabado", txtsabado)
                actualizarPago(empleadoId.toLong(), "domingo", txtdomingo)
                actualizarPago(empleadoId.toLong(), "lunes", txtlunes)
            }
        }

        val btnEliminar = findViewById<FloatingActionButton>(R.id.btneliminar)
        btnEliminar.setOnClickListener {
            val empleadoId = txtidd.text.toString().toInt()

            if (empleadoId != -1) {
                val empleadoExistente = db.empleadoDao().getEmpleadoById(empleadoId)
                if (empleadoExistente != null) {
                    // Cambia el estatus a False al "eliminar" el empleado de la base de datos
                    db.empleadoDao().updateEmpleado(empleadoExistente.copy(estatus = false))

                    // Muestra un mensaje de éxito
                    Toast.makeText(this, "Empleado desactivado con éxito", Toast.LENGTH_SHORT).show()

                    // Limpia los campos después de desactivar el empleado
                    limpiarCampos()
                } else {
                    // Muestra un mensaje de error si el empleado no existe
                    Toast.makeText(this, "El empleado no existe en la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val btnActivar = findViewById<FloatingActionButton>(R.id.btnactivar)
        btnActivar.setOnClickListener {
            val empleadoId = txtidd.text.toString().toInt()

            if (empleadoId != -1) {
                val empleadoExistente = db.empleadoDao().getEmpleadoById(empleadoId)
                if (empleadoExistente != null) {
                    // Cambia el estatus a True al activar el empleado
                    db.empleadoDao().updateEmpleado(empleadoExistente.copy(estatus = true))

                    // Muestra un mensaje de éxito
                    Toast.makeText(this, "Empleado activado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    // Muestra un mensaje de error si el empleado no existe
                    Toast.makeText(this, "El empleado no existe en la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val btnvolvera: Button = findViewById(R.id.btnvolvera)
        btnvolvera.setOnClickListener {
            val intent1 = Intent(this, Menuopciones::class.java)
            startActivity(intent1)
        }

        val btnBuscarModi = findViewById<FloatingActionButton>(R.id.btnbuscarmodi)
        btnBuscarModi.setOnClickListener {
            val empleadoId = txtidd.text.toString().toInt()

            if (empleadoId != -1) {
                val empleado = db.empleadoDao().getEmpleadoById(empleadoId)
                if (empleado != null) {
                    // Cargar los datos del empleado en los campos correspondientes
                    txtnombre.setText(empleado.nombre)
                    txthoraextra.setText(empleado.horaextra.toString())
                    txtRostro = empleado.rostro

                    // Mostrar la imagen en el ImageView
                    val imageViewRostroModi = findViewById<ImageView>(R.id.imgrostroo)
                    mostrarImagenBase64(txtRostro, imageViewRostroModi)

                    // Cargar los pagos correspondientes
                    cargarPagos(empleadoId)
                } else {
                    // Mostrar un mensaje si el empleado no se encuentra
                    Toast.makeText(this, "Empleado no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //val dbd = Room.databaseBuilder(applicationContext, DataBase::class.java, "registro").allowMainThreadQueries().build()
    // Inicializa la base de datos y el DAO de Pago
    //val daoPago = db.pagoDao()

    private fun cargarPagos(idEmpleado: Int) {
        // Obtener la lista de pagos del empleado
        val db = Room.databaseBuilder(applicationContext, DataBase::class.java, "registro").allowMainThreadQueries().build()
        val pagos = db.pagoDao().getPagosByIdEmpleado(idEmpleado.toLong())

        // Mostrar los montos de pago en los campos correspondientes
        for (pago in pagos) {
            when (pago.FechaPago) {
                "miercoles" -> txtmiercoles.setText(pago.MontoPago.toString())
                "jueves" -> txtjueves.setText(pago.MontoPago.toString())
                "viernes" -> txtviernes.setText(pago.MontoPago.toString())
                "sabado" -> txtsabado.setText(pago.MontoPago.toString())
                "domingo" -> txtdomingo.setText(pago.MontoPago.toString())
                "lunes" -> txtlunes.setText(pago.MontoPago.toString())
            }
        }
    }

    private fun actualizarPago(empleadoId: Long, dia: String, txtMonto: EditText) {
        val monto = txtMonto.text.toString().toDouble()
        val db = Room.databaseBuilder(applicationContext, DataBase::class.java, "registro").allowMainThreadQueries().build()
        val pagoExistente = db.pagoDao().getPagoByEmpleadoIdYFecha(empleadoId, dia)

        if (pagoExistente != null) {
            // Actualiza el monto del pago existente
            db.pagoDao().updatePago(pagoExistente.copy(MontoPago = monto))
        } else {
            // Inserta un nuevo pago si no existe
            val nuevoPago = Pago(IDEmpleado = empleadoId, FechaPago = dia, MontoPago = monto)
            db.pagoDao().insertPago(nuevoPago)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            if (imageBitmap != null) {
                // Convierte el bitmap a una representación de cadena (Base64) y guárdalo en el atributo rostro
                txtRostro = convertBitmapToBase64(imageBitmap)

                // Muestra la imagen en el ImageView
                val imageViewRostroModi = findViewById<ImageView>(R.id.imgrostroo)
                imageViewRostroModi.setImageBitmap(imageBitmap)
            }
        }
    }

    private fun mostrarImagenBase64(base64: String, imageView: ImageView) {
        // Convierte la cadena Base64 de la imagen y la muestra en el ImageView
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        imageView.setImageBitmap(decodedBitmap)
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun limpiarCampos() {
        // Limpia los campos de EditText
        txtnombre.text.clear()
        txtmiercoles.text.clear()
        txtjueves.text.clear()
        txtviernes.text.clear()
        txtsabado.text.clear()
        txtdomingo.text.clear()
        txtlunes.text.clear()
        txthoraextra.text.clear()
        txtRostro = ""

        // Establece la imagen predeterminada en el ImageView
        val imageViewRostroModi = findViewById<ImageView>(R.id.imgrostroo)
        imageViewRostroModi.setImageResource(R.drawable.fotoperfil)
    }
}