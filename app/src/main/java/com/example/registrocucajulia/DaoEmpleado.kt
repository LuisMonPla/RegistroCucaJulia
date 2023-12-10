package com.example.registrocucajulia
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DaoEmpleado {
    @Insert
    fun insertEmpleado(empleado: empleadoentity): Long

    @Update
    fun updateEmpleado(empleado: empleadoentity)

    @Query("SELECT * FROM empleados WHERE id = :id")
    fun getEmpleadoById(id: Int): empleadoentity?

    @Query("SELECT * FROM empleados ORDER BY id DESC LIMIT 1")
    fun obtenerUltimoEmpleado(): empleadoentity?

    @Query("SELECT * FROM empleados WHERE id = :id")
    fun getEmpleadoByIdLiveData(id: Long): LiveData<empleadoentity>

    @Query("SELECT * FROM empleados WHERE estatus = 1")
    fun getEmpleadosActivos(): List<empleadoentity>

    @Query("SELECT * FROM empleados WHERE estatus = 1")
    fun getEmpleadosActivosLiveData(): LiveData<List<empleadoentity>>
}

@Dao
interface DaoAsistencia {
    @Insert
    fun insertAsistencia(asistencia: Asistencia): Long

    //@Update
    //fun updateAsistencia(asistencia: Asistencia)

    @Query("SELECT * FROM asistencias WHERE IDEmpleado = :idEmpleado AND Fecha = :fecha")
    fun getAsistenciaByEmpleadoIdYFecha(idEmpleado: Int, fecha: String): Asistencia?

    @Query("UPDATE asistencias SET HoraSalida = :horaSalida WHERE ID = :asistenciaId")
    fun updateHoraSalida(asistenciaId: Long, horaSalida: Long)

    @Query("SELECT * FROM asistencias WHERE IDEmpleado = :idEmpleado")
    fun getAllAsistenciasLiveData(idEmpleado: Long): LiveData<List<Asistencia>>

    @Query("SELECT * FROM asistencias WHERE IDEmpleado = :idEmpleado AND Fecha >= :fecha")
    fun getAsistenciasPorEmpleadoYFecha(idEmpleado: Long, fecha: String): LiveData<List<Asistencia>>
}

@Dao
interface DaoPago {
    @Insert
    fun insertPago(pago: Pago): Long

    @Update
    fun updatePago(pago: Pago)

    @Query("SELECT * FROM pagos WHERE idEmpleado = :idEmpleado")
    fun getAllPagosLiveData(idEmpleado: Long): LiveData<List<Pago>>

    @Query("SELECT * FROM pagos WHERE IDEmpleado = :idEmpleado AND FechaPago = :fechaPago")
    fun getPagoByEmpleadoIdYFecha(idEmpleado: Long, fechaPago: String): Pago?

    @Query("SELECT * FROM pagos WHERE idEmpleado = :idEmpleado")
    fun getPagosByIdEmpleado(idEmpleado: Long): List<Pago>

}