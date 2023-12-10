package com.example.registrocucajulia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    lateinit var txtbuscaridregistro: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtbuscaridregistro = findViewById(R.id.txtbuscaridregistro)

        val passwordEditText = findViewById<EditText>(R.id.txtpassword)

        // Reemplaza "1234" con tu clave correcta
        val claveCorrecta = "1234"

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No necesitas hacer nada aquí
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Verifica la contraseña cuando el texto cambia
                val inputClave = s.toString()

                if (inputClave == claveCorrecta) {
                    // La clave es correcta, abre la nueva ventana
                    val intent = Intent(this@MainActivity, Menuopciones::class.java)
                    startActivity(intent)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // No necesitas hacer nada aquí
            }
        })


        val db = Room.databaseBuilder(applicationContext, DataBase::class.java, "registro")
            .fallbackToDestructiveMigration()
            .build()

        val asistenciaDao = db.asistenciaDao()


        val btnRegistrar = findViewById<Button>(R.id.btnregistrar)
        btnRegistrar.setOnClickListener {
            val idEmpleado = txtbuscaridregistro.text.toString().toIntOrNull()

            if (idEmpleado == null) {
                // Manejar el caso en que el ID no sea un número válido
                Toast.makeText(this, "ID de empleado no válido", Toast.LENGTH_SHORT).show()
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    // Verificar si el empleado existe
                    //val empleadoExistente = empleadoDao.getEmpleadoById(idEmpleado)
                    val empleadoExistente = db.empleadoDao().getEmpleadoByIdLiveData(idEmpleado.toLong())

                    if (empleadoExistente != null) {
                        val calendario = Calendar.getInstance().timeInMillis
                        val fecha = obtenerNombreDia(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
                        val horaEntrada = Calendar.getInstance().timeInMillis

                        val asistenciaExistente =
                            asistenciaDao.getAsistenciaByEmpleadoIdYFecha(idEmpleado, fecha)

                        if (asistenciaExistente != null) {
                            // Si ya existe una asistencia para este empleado en este día, actualiza la hora de salida
                            if (asistenciaExistente.HoraSalida == 0L) {
                                // Solo actualiza la hora de salida si aún no se ha registrado
                                asistenciaDao.updateHoraSalida(asistenciaExistente.ID, horaEntrada)
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Hora de salida registrada",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Ya se registró la salida para este empleado hoy",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            // Si no existe una asistencia, realiza una nueva inserción con la hora de entrada
                            val nuevaAsistencia = Asistencia(
                                IDEmpleado = idEmpleado,
                                Calendario = calendario,
                                Fecha = fecha,
                                HoraEntrada = horaEntrada,
                                HoraSalida = 0
                            )
                            asistenciaDao.insertAsistencia(nuevaAsistencia)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Hora de entrada registrada",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        // Limpiar el campo txtbuscaridregistro después del registro
                        withContext(Dispatchers.Main) {
                            txtbuscaridregistro.text.clear()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@MainActivity,
                                "No existe un empleado con este ID",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun obtenerNombreDia(diaDeLaSemana: Int): String {
        return when (diaDeLaSemana) {
            Calendar.SUNDAY -> "domingo"
            Calendar.MONDAY -> "lunes"
            Calendar.WEDNESDAY -> "miercoles"
            Calendar.THURSDAY -> "jueves"
            Calendar.FRIDAY -> "viernes"
            Calendar.SATURDAY -> "sabado"
            else -> ""
        }
    }
}