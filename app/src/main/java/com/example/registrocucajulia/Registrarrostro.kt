package com.example.registrocucajulia
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale


class Registrarrostro : AppCompatActivity() {

    /*private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var txtid: EditText
    //private lateinit var txtnombre: EditText
    //private lateinit var txthoraextra: EditText
    private lateinit var txtRostro: String
    private lateinit var db: DataBase*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*txtid = findViewById(R.id.txtid)
        //txtnombre = findViewById(R.id.txtnombre)
        //txthoraextra = findViewById(R.id.txthoraextra)

        // Inicializa la base de datos
        db = Room.databaseBuilder(applicationContext, DataBase::class.java, "registro").allowMainThreadQueries().build()

        // Otener el último ID y establecer el siguiente ID
        val ultimoID = obtenerUltimoID()
        txtid.setText((ultimoID + 1).toString())

        val btnTomarFoto: Button = findViewById(R.id.btnrostro)
        btnTomarFoto.setOnClickListener {
            // Lanzar la cámara para tomar una foto del rostro
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }

        val btnRegistrar: Button = findViewById(R.id.btnregistrar)
        btnRegistrar.setOnClickListener {
            if (validarCampos()) {
                registrarRostro()
            }
        }
    }

    private fun obtenerUltimoID(): Int {
        val ultimoEmpleado = db.empleadoDao().obtenerUltimoEmpleado()
        return ultimoEmpleado?.id ?: 0
    }

    private fun validarCampos(): Boolean {
        val campos = listOf(txtid) //, txtnombre, txthoraextra

        for (campo in campos) {
            if (campo.text.toString().isEmpty()) {
                campo.error = "Este campo no puede estar vacío"
                return false
            }
        }

        if (!this::txtRostro.isInitialized || txtRostro.isEmpty()) {
            Toast.makeText(this, "Debes tomar una foto del rostro", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun registrarRostro() {
        // Obtén los valores de los campos
        val id = txtid.text.toString().toInt()
        //val nombre = txtnombre.text.toString()
        //val horaextra = txthoraextra.text.toString().toInt()

        // Insertar registro en la tabla Empleado
        val rostro = txtRostro
        val empleado = empleadoentity(id, rostro) //nombre, horaextra,
        val empleadoId = db.empleadoDao().insertEmpleado(empleado.copy(estatus = true))

        Toast.makeText(this, "Empleado registrado con ID: $empleadoId", Toast.LENGTH_SHORT).show()

        // Limpiar campos
        val campos = listOf(txtid) //, txtnombre, txthoraextra
        for (campo in campos) {
            campo.text.clear()
        }

        // Actualizar el ID
        txtid.setText((obtenerUltimoID() + 1).toString())

        // Limpiar el atributo de rostro
        txtRostro = ""

        // Establecer la imagen predeterminada en el ImageView
        val imageViewRostro = findViewById<ImageView>(R.id.imgrostro)
        imageViewRostro.setImageResource(R.drawable.fotoperfil)

        // Registrar asistencia y pago
        registrarAsistencia(id)
        //registrarPago(id)
    }

    private fun registrarAsistencia(idEmpleado: Int) {
        // Verificar si el ID ya ha alcanzado el límite de intentos para hoy
        if (puedeRegistrarAsistencia(idEmpleado)) {
            // Insertar registro en la tabla Asistencia
            val asistencia = Asistencia(IDEmpleado = idEmpleado, Fecha = obtenerFechaActual(), HoraEntrada = obtenerHoraActual())
            db.asistenciaDao().insertAsistencia(asistencia)

            // Incrementar el contador de intentos
            incrementarContadorIntentos(idEmpleado)
        } else {
            Toast.makeText(this, "Solo puedes registrar dos veces al día por ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun puedeRegistrarAsistencia(idEmpleado: Int): Boolean {
        // Verificar si el ID ha alcanzado el límite de intentos para hoy
        val intentosHoy = idIntentosMap[idEmpleado] ?: 0
        return intentosHoy < MAX_INTENTOS_DIARIOS
    }

    private fun incrementarContadorIntentos(idEmpleado: Int) {
        // Incrementar el contador de intentos para el ID
        idIntentosMap[idEmpleado] = (idIntentosMap[idEmpleado] ?: 0) + 1
    }

    /*private fun registrarPago(idEmpleado: Int) {
        // Obtener los montos de los días de la semana (excepto martes)
        val montos = listOf(50.0, 30.0, 20.0, 40.0, 60.0, 25.0)

        // Obtener la fecha actual
        val fechaActual = obtenerFechaActual()

        // Insertar registros en la tabla Pago
        for ((index, monto) in montos.withIndex()) {
            val diaSemana = obtenerDiaSemana(index + 1) // Los días de la semana comienzan desde 1

            val pago = Pago(IDEmpleado = idEmpleado, FechaPago = fechaActual, MontoPago = monto)
            db.pagoDao().insertPago(pago)
        }
    }*/

    private fun obtenerFechaActual(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun obtenerHoraActual(): String {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return timeFormat.format(Date())
    }

    private fun obtenerDiaSemana(numeroDia: Int): String {
        // Devuelve el nombre del día de la semana basado en el número proporcionado
        return when (numeroDia) {
            1 -> "lunes"
            2 -> "martes"
            3 -> "miercoles"
            4 -> "jueves"
            5 -> "viernes"
            6 -> "sabado"
            7 -> "domingo"
            else -> "desconocido"
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

    companion object {
        private const val MAX_INTENTOS_DIARIOS = 2
        private val idIntentosMap = mutableMapOf<Int, Int>()
    }*/
}}