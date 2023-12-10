package com.example.registrocucajulia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Menuopciones : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menuopciones)

        val btnHorario = findViewById<Button>(R.id.btnhorario)
        val btnAgregar = findViewById<Button>(R.id.btnagregar)
        val btnModificar = findViewById<Button>(R.id.btnmodificar)
        val btnVolver = findViewById<Button>(R.id.btnvolver)

        // Configurar OnClickListener para el botón btnHorario
        btnHorario.setOnClickListener {
            val intent = Intent(this, Verhorario::class.java)
            startActivity(intent)
        }

        // Configurar OnClickListener para el botón btnAgregar
        btnAgregar.setOnClickListener {
            val intent = Intent(this, Empleado::class.java)
            startActivity(intent)
        }

        // Configurar OnClickListener para el botón btnModificar
        btnModificar.setOnClickListener {
            val intent = Intent(this, Modificarempleado::class.java)
            startActivity(intent)
        }

        // Configurar OnClickListener para el botón btnVolver
        btnVolver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}