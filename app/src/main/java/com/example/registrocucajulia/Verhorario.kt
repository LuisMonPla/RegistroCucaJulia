package com.example.registrocucajulia

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Verhorario : AppCompatActivity() {

    lateinit var txtbuscarporsemana: EditText
    lateinit var adaptador: Adaptador // Declarar el adaptador como propiedad de la clase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verhorario)

        txtbuscarporsemana = findViewById(R.id.txtbuscarporsemana)

        // Suponiendo que tienes acceso al ID del empleado que deseas mostrar
        //val idEmpleado: Long = obtenerIdEmpleado() // Reemplaza esto con la lógica para obtener el ID del empleado
        val idEmpleado: Long = intent.getLongExtra("ID_EMPLEADO", -1)

        val listaEmpleados: LiveData<List<empleadoentity>> = obtenerListaEmpleados()
        val listaAsistencias: LiveData<List<Asistencia>> = obtenerListaAsistencias(idEmpleado, "")
        val listaPagos: LiveData<List<Pago>> = obtenerListaPagos(idEmpleado)

        adaptador = Adaptador(listaEmpleados, listaAsistencias, listaPagos) // Inicializar el adaptador

        val recyclerView = findViewById<RecyclerView>(R.id.rvMostrardatos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adaptador

        // Agregar TextWatcher al EditText
        txtbuscarporsemana.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No es necesario hacer algo aquí
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val fechaIngresada = s?.toString() ?: ""
                // Actualizar la lista de asistencias cuando se ingresa una fecha
                actualizarListaAsistencias(idEmpleado, fechaIngresada)
            }

            override fun afterTextChanged(s: Editable?) {
                // No es necesario hacer algo aquí
            }
        })
    }

    // Funciones para obtener listas de empleados, asistencias y pagos
    private fun obtenerListaEmpleados(): LiveData<List<empleadoentity>> {
        val daoEmpleado = DataBase.getInstance(applicationContext).empleadoDao()
        return daoEmpleado.getEmpleadosActivosLiveData()
    }

    private fun actualizarListaAsistencias(idEmpleado: Long, fecha: String) {
        val listaAsistencias: LiveData<List<Asistencia>> = obtenerListaAsistencias(idEmpleado, fecha)
        listaAsistencias.observe(this, { asistencias ->
            adaptador.actualizarListaAsistencias(asistencias)
        })
    }

    private fun obtenerListaAsistencias(idEmpleado: Long, fecha: String): LiveData<List<Asistencia>> {
        val daoAsistencia = DataBase.getInstance(applicationContext).asistenciaDao()
        return daoAsistencia.getAsistenciasPorEmpleadoYFecha(idEmpleado, fecha)
    }

    private fun obtenerListaPagos(idEmpleado: Long): LiveData<List<Pago>> {
        val daoPago = DataBase.getInstance(applicationContext).pagoDao()
        return daoPago.getAllPagosLiveData(idEmpleado)
    }
}