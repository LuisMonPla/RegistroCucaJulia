package com.example.registrocucajulia

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import java.time.Duration
import java.time.LocalTime

class Adaptador(private val listaEmpleados: LiveData<List<empleadoentity>>,
                private var listaAsistencias: LiveData<List<Asistencia>>,
                private val listaPagos: LiveData<List<Pago>>
) :
    RecyclerView.Adapter<Adaptador.ViewHolder>() {

    fun actualizarListaAsistencias(asistencias: List<Asistencia>) {
        val nuevaListaAsistencias = MutableLiveData<List<Asistencia>>()
        nuevaListaAsistencias.value = asistencias
        this.listaAsistencias = nuevaListaAsistencias
        notifyDataSetChanged()
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencias a los TextView en itemview.xml
        //TextView para el nombre del mismo empleado
        val txtNombre1: TextView = itemView.findViewById(R.id.txtnombre1)
        val txtNombre2: TextView = itemView.findViewById(R.id.txtnombre2)
        val txtNombre3: TextView = itemView.findViewById(R.id.txtnombre3)
        val txtNombre4: TextView = itemView.findViewById(R.id.txtnombre4)
        val txtNombre5: TextView = itemView.findViewById(R.id.txtnombre5)
        val txtNombre6: TextView = itemView.findViewById(R.id.txtnombre6)
        val txtFechaMiercoles: TextView = itemView.findViewById(R.id.txtfmiercoles)
        val txtFechaJueves: TextView = itemView.findViewById(R.id.txtfjueves)
        val txtFechaViernes: TextView = itemView.findViewById(R.id.txtfviernes)
        val txtFechaSabado: TextView = itemView.findViewById(R.id.txtfsabado)
        val txtFechaDomingo: TextView = itemView.findViewById(R.id.txtfdomingo)
        val txtFechaLunes: TextView = itemView.findViewById(R.id.txtflunes)
        //TextViews para las entradas
        val txtEntrada1: TextView = itemView.findViewById(R.id.txtentrada1)
        val txtEntrada2: TextView = itemView.findViewById(R.id.txtentrada2)
        val txtEntrada3: TextView = itemView.findViewById(R.id.txtentrada3)
        val txtEntrada4: TextView = itemView.findViewById(R.id.txtentrada4)
        val txtEntrada5: TextView = itemView.findViewById(R.id.txtentrada5)
        val txtEntrada6: TextView = itemView.findViewById(R.id.txtentrada6)
        //TextViews para las salidas
        val txtSalida1: TextView = itemView.findViewById(R.id.txtsalida1)
        val txtSalida2: TextView = itemView.findViewById(R.id.txtsalida2)
        val txtSalida3: TextView = itemView.findViewById(R.id.txtsalida3)
        val txtSalida4: TextView = itemView.findViewById(R.id.txtsalida4)
        val txtSalida5: TextView = itemView.findViewById(R.id.txtsalida5)
        val txtSalida6: TextView = itemView.findViewById(R.id.txtsalida6)
        // TextViews para los pagos
        val txtPago1: TextView = itemView.findViewById(R.id.txtpago1)
        val txtPago2: TextView = itemView.findViewById(R.id.txtpago2)
        val txtPago3: TextView = itemView.findViewById(R.id.txtpago3)
        val txtPago4: TextView = itemView.findViewById(R.id.txtpago4)
        val txtPago5: TextView = itemView.findViewById(R.id.txtpago5)
        val txtPago6: TextView = itemView.findViewById(R.id.txtpago6)
        // TextView para el pago total
        val txtPagoTotal: TextView = itemView.findViewById(R.id.txtpagototal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Obtén el empleado actual
        val empleado = listaEmpleados.value?.get(position)

        // Muestra el nombre del empleado
        if (empleado != null) {
            holder.txtNombre1.text = empleado.nombre
        }
        if (empleado != null) {
            holder.txtNombre2.text = empleado.nombre
        }
        if (empleado != null) {
            holder.txtNombre3.text = empleado.nombre
        }
        if (empleado != null) {
            holder.txtNombre4.text = empleado.nombre
        }
        if (empleado != null) {
            holder.txtNombre5.text = empleado.nombre
        }
        if (empleado != null) {
            holder.txtNombre6.text = empleado.nombre
        }

        // Filtra las asistencias y pagos correspondientes al empleado actual
        val asistenciasEmpleado = listaAsistencias.value?.filter { it.IDEmpleado.toLong() == empleado?.id }
        val pagosEmpleado = listaPagos.value?.filter { it.IDEmpleado == empleado?.id }

        // Configura las fechas y horarios en los TextViews correspondientes
        if (asistenciasEmpleado != null) {
            for (i in 0 until asistenciasEmpleado.size) {
                val asistencia = asistenciasEmpleado[i]
                // Configura las fechas
                holder.txtFechaMiercoles.text = asistencia.Fecha
                holder.txtFechaJueves.text = asistencia.Fecha
                holder.txtFechaViernes.text = asistencia.Fecha
                holder.txtFechaSabado.text = asistencia.Fecha
                holder.txtFechaDomingo.text = asistencia.Fecha
                holder.txtFechaLunes.text = asistencia.Fecha

                // Configura las entradas y salidas
                holder.txtEntrada1.text = asistencia.HoraEntrada.toString()
                holder.txtEntrada2.text = asistencia.HoraEntrada.toString()
                holder.txtEntrada3.text = asistencia.HoraEntrada.toString()
                holder.txtEntrada4.text = asistencia.HoraEntrada.toString()
                holder.txtEntrada5.text = asistencia.HoraEntrada.toString()
                holder.txtEntrada6.text = asistencia.HoraEntrada.toString()

                holder.txtSalida1.text = asistencia.HoraSalida.toString()
                holder.txtSalida2.text = asistencia.HoraSalida.toString()
                holder.txtSalida3.text = asistencia.HoraSalida.toString()
                holder.txtSalida4.text = asistencia.HoraSalida.toString()
                holder.txtSalida5.text = asistencia.HoraSalida.toString()
                holder.txtSalida6.text = asistencia.HoraSalida.toString()

                // Calcula y configura los pagos
                val horasTrabajadas = calcularHorasTrabajadas(asistencia)
                val montoPago = obtenerMontoPago(pagosEmpleado?.get(i) ?: Pago(ID = 0, IDEmpleado = 0, FechaPago = "2023-12-05", MontoPago = 0.0 ))
                val pago = horasTrabajadas * montoPago
                when (i) {
                    0 -> holder.txtPago1.text = pago.toString()
                    1 -> holder.txtPago2.text = pago.toString()
                    2 -> holder.txtPago3.text = pago.toString()
                    3 -> holder.txtPago4.text = pago.toString()
                    4 -> holder.txtPago5.text = pago.toString()
                    5 -> holder.txtPago6.text = pago.toString()
                    // Configura más pagos según sea necesario
                }
            }
        }

        // Calcula y configura el pago total
        val pagoTotal = pagosEmpleado?.sumBy { it.MontoPago.toInt() } // Ajusta según tu modelo
        holder.txtPagoTotal.text = pagoTotal.toString()
    }


    override fun getItemCount(): Int {
        return listaEmpleados.value?.size ?: 0
    }

    @SuppressLint("NewApi")
    private fun calcularHorasTrabajadas(asistencia: Asistencia): Int {
        // Obtén las horas de entrada y salida como objetos LocalTime
        val horaEntrada = LocalTime.ofSecondOfDay(asistencia.HoraEntrada / 1000)
        val horaSalida = LocalTime.ofSecondOfDay(asistencia.HoraSalida / 1000)

        // Calcula la duración entre la entrada y la salida
        val duracion = Duration.between(horaEntrada, horaSalida)

        // Calcula las horas y minutos trabajados
        val horasTrabajadas = duracion.toHours().toInt()
        //val minutosTrabajados = duracion.toMinutesPart()

        // Puedes ajustar la lógica según tus necesidades, por ejemplo, redondeando hacia arriba si los minutos son mayores a 30
        //if (minutosTrabajados > 30) {
        //    return horasTrabajadas + 1
        //}

        return horasTrabajadas
    }

    private fun obtenerMontoPago(pago: Pago): Double {
        return pago.MontoPago
    }


}

private fun <T> LiveData<T>.filter(function: () -> Boolean): Any {
    TODO("Not yet implemented")
}

private operator fun <T> LiveData<T>.get(position: Int) {

}
