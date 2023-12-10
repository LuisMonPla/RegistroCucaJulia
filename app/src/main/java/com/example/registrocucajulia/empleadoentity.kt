package com.example.registrocucajulia

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "empleados")
data class empleadoentity(
    @PrimaryKey(autoGenerate = false)
    val id: Long = 0,
    val nombre: String,
    val horaextra: Int,
    val rostro: String,
    val estatus: Boolean = true
)

@Entity(tableName = "asistencias")
data class Asistencia(
    @PrimaryKey(autoGenerate = true)
    val ID: Long = 0,
    val IDEmpleado: Int,
    val Calendario: Long,
    val Fecha: String,
    val HoraEntrada: Long,
    val HoraSalida: Long
)

@Entity(tableName = "pagos")
data class Pago(
    @PrimaryKey(autoGenerate = true)
    val ID: Long = 0,
    val IDEmpleado: Long,
    val FechaPago: String,
    val MontoPago: Double
)