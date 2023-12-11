package com.example.registrocucajulia

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import com.google.android.datatransport.BuildConfig

class Verhorario : AppCompatActivity() {

    lateinit var txtbuscarporsemana: EditText
    lateinit var adaptador: Adaptador // Declarar el adaptador como propiedad de la clase
    var idEmpleado: Long = -1 // Inicializar idEmpleado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verhorario)

        txtbuscarporsemana = findViewById(R.id.txtbuscarporsemana)

        val btnBuscarPorFecha = findViewById<FloatingActionButton>(R.id.btnbuscarporfecha)
        btnBuscarPorFecha.setOnClickListener {
            val fechaIngresada = txtbuscarporsemana.text.toString()
            actualizarListaAsistencias(idEmpleado, fechaIngresada)
        }

        // Suponiendo que tienes acceso al ID del empleado que deseas mostrar
        idEmpleado = intent.getLongExtra("ID_EMPLEADO", -1)

        val listaEmpleados: LiveData<List<empleadoentity>> = obtenerListaEmpleados()
        val listaAsistencias: LiveData<List<Asistencia>> = obtenerListaAsistencias(idEmpleado, "")
        val listaPagos: LiveData<List<Pago>> = obtenerListaPagos(idEmpleado)

        adaptador = Adaptador(listaEmpleados, listaAsistencias, listaPagos) // Inicializar el adaptador

        val recyclerView = findViewById<RecyclerView>(R.id.rvMostrardatos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adaptador

        val btnvolvera3: Button = findViewById(R.id.btnvolvera3)
        btnvolvera3.setOnClickListener {
            val intent = Intent(this, Menuopciones::class.java)
            startActivity(intent)
        }

        val btnCompartir = findViewById<FloatingActionButton>(R.id.btncompartir)
        btnCompartir.setOnClickListener {
            val listaAsistencias: LiveData<List<Asistencia>> = obtenerListaAsistencias(idEmpleado, "")
            listaAsistencias.observe(this, { asistencias ->
                val filePath = generarPDF(asistencias)
                compartirPDF(filePath)
            })
        }

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

    private fun generarPDF(datos: List<Asistencia>): String {
        val document = Document()
        val fileName = "reporte_asistencias.pdf"
        val filePath = "${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/$fileName"

        try {
            PdfWriter.getInstance(document, FileOutputStream(filePath))
            document.open()

            // Crear una tabla para organizar los datos
            val table = PdfPTable(4) // Ajusta el número de columnas según tus necesidades
            val cell = PdfPCell(Paragraph("Reporte de Asistencias"))
            cell.colspan = 4
            cell.horizontalAlignment = PdfPCell.ALIGN_CENTER
            table.addCell(cell)

            // Encabezados de columna
            table.addCell("ID")
            table.addCell("Fecha")
            table.addCell("Hora Entrada")
            table.addCell("Hora Salida")

            // Agregar datos a la tabla
            for (asistencia in datos) {
                table.addCell(asistencia.ID.toString())
                table.addCell(asistencia.Fecha)
                table.addCell(asistencia.HoraEntrada.toString())
                table.addCell(asistencia.HoraSalida.toString())
            }

            // Agregar la tabla al documento
            document.add(table)

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            document.close()
        }

        return filePath
    }

    private fun compartirPDF(filePath: String) {
        val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", File(filePath))

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(Intent.createChooser(intent, "Compartir PDF"))
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