package com.example.registrocucajulia
import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.registrocucajulia.DaoEmpleado
import com.example.registrocucajulia.Empleado


@Database(entities = [empleadoentity::class, Asistencia::class, Pago::class], version = 2, exportSchema = false)
abstract class DataBase : RoomDatabase() {
    abstract fun empleadoDao(): DaoEmpleado
    abstract fun pagoDao(): DaoPago
    abstract fun asistenciaDao(): DaoAsistencia
    companion object {
        @Volatile
        private var INSTANCE: DataBase? = null

        fun getInstance(context: Context): DataBase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): DataBase {
            return Room.databaseBuilder(context, DataBase::class.java, "registro").build()
        }
    }
}




