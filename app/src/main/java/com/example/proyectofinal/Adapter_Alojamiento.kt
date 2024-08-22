package construredes.net.appgescr.Controlles

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.proyectofinal.Alojamiento
import com.example.proyectofinal.Model.Model_Alojamiento
import com.example.proyectofinal.R

// Definición del adaptador personalizado para mostrar los datos de alojamiento en una lista
class Adapter_Alojamiento(private val mContext: Context, private val lista: MutableList<Model_Alojamiento>) : BaseAdapter() {

    // Inicialización del adaptador
    init {
        // Verifica que el contexto proporcionado sea una instancia de Activity
        if (mContext !is Activity) {
            throw IllegalArgumentException("El contexto debe ser una instancia de Activity")
        }
    }

    // Devuelve el número total de elementos en la lista
    override fun getCount(): Int {
        return lista.size
    }

    // Devuelve el elemento en la posición especificada
    override fun getItem(position: Int): Any {
        return lista[position]
    }

    // Devuelve el ID del elemento en la posición especificada (usando countID como identificador único)
    override fun getItemId(position: Int): Long {
        return lista[position].countID.toLong()
    }

    // Variable para mantener la posición actualmente seleccionada
    @SuppressLint("MissingInflatedId")
    private var selectedPosition: Int = -1

    // Crea y configura la vista para cada elemento en la lista
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val v: View

        // Si la vista de conversión es nula, infla una nueva vista y crea un ViewHolder
        if (convertView == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_list_alojamiento, parent, false)
            viewHolder = ViewHolder(
                v.findViewById(R.id.id_tp),
                v.findViewById(R.id.txtdetID),
                v.findViewById(R.id.tv_precio),
                v.findViewById(R.id.tv_habitacion_),
                v.findViewById(R.id.tv_tip_hab),
                v.findViewById(R.id.tv_proceso_),
                v.findViewById(R.id.tv_Fecha_Inicio),
                v.findViewById(R.id.tv_Fecha_Fin),
                v.findViewById(R.id.tv_Hora_Inicio),
                v.findViewById(R.id.tv_Hora_Fin)
            )
            v.tag = viewHolder
        } else {
            // Si la vista de conversión no es nula, reutiliza la vista y el ViewHolder
            v = convertView
            viewHolder = v.tag as ViewHolder
        }

        // Obtiene el objeto de alojamiento en la posición actual
        val alojamiento = lista[position]
        // Configura los valores de los TextViews con los datos del objeto de alojamiento
        viewHolder.tvID.text = alojamiento.idAC
        viewHolder.tvPrecio.text = alojamiento.idHP_descripcion.toString()
        viewHolder.tvHabitacion.text = alojamiento.idH_descripcion
        viewHolder.tvTipHabitacion.text = alojamiento.idTH_descripcion
        viewHolder.tvProceso.text = alojamiento.idP_descripcion
        viewHolder.tvFI.text = alojamiento.fechaInicio
        viewHolder.tvFF.text = alojamiento.fechaFin
        viewHolder.tvHI.text = alojamiento.horaInicio
        viewHolder.tvHF.text = alojamiento.horaFin

        // Configura el color de fondo para indicar la selección del elemento
        if (position == selectedPosition) {
            viewHolder.id_tp.setBackgroundColor(Color.parseColor("#00FF00")) // Verde para el elemento seleccionado
        } else {
            viewHolder.id_tp.setBackgroundColor(Color.parseColor("#6D92AD")) // Azul para los elementos no seleccionados
        }

        // Configura el listener de clic para la vista id_tp
        viewHolder.id_tp.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado y actualiza la vista

            try {
                // Obtiene la actividad actual y verifica que sea una instancia de Alojamiento
                (mContext as? Activity)?.let { activity ->
                    val alojamientoActivity = activity as? Alojamiento

                    // Asigna el ID de AlojamientoCab a la actividad actual
                    val idAC = alojamiento.idAC // Obtiene el ID de AlojamientoCab del objeto
                    alojamientoActivity?.setIdAC(idAC.toInt()) // Llama a un método en la actividad para establecer el idAC
                    Log.e("ID_AC", "ID de AlojamientoCab: $idAC")

                    // Configura los EditTexts de la actividad con los datos del alojamiento seleccionado
                    alojamientoActivity?.etFI?.setText(viewHolder.tvFI.text.toString())
                    alojamientoActivity?.etFF?.setText(viewHolder.tvFF.text.toString())
                    alojamientoActivity?.etHI?.setText(viewHolder.tvHI.text.toString())
                    alojamientoActivity?.etHF?.setText(viewHolder.tvHF.text.toString())

                    // Obtiene el texto del proceso desde el TextView y busca el índice en la lista de procesos
                    val procesoText = viewHolder.tvProceso.text.toString().trim()
                    val position = alojamientoActivity?.procesoList?.indexOfFirst {
                        it.descripcionP.trim().equals(procesoText, ignoreCase = true)
                    }
                    Log.e("ProcesoP", "Position: $position, ProcesoText: $procesoText")

                    // Selecciona el elemento en el Spinner si se encuentra en la lista
                    if (position != null && position >= 0) {
                        alojamientoActivity.sp_pro.setSelection(position)
                    } else {
                        Log.e("ProcesoP", "No se encontró la descripción del proceso en el Spinner")
                    }

                    // Obtiene el texto del tipo de habitación y busca el índice en la lista de tipos de habitación
                    val thText = viewHolder.tvTipHabitacion.text.toString().trim()
                    val positionTH = alojamientoActivity?.tipoHabitacionList?.indexOfFirst {
                        it.descripcionTH.trim().equals(thText, ignoreCase = true)
                    }
                    Log.e("ProcesoTH", "Position: $positionTH, THText: $thText")

                    // Selecciona el elemento en el Spinner si se encuentra en la lista
                    if (positionTH != null && positionTH >= 0) {
                        alojamientoActivity.sp_th.setSelection(positionTH)

                        // Obtiene el texto de la habitación y busca el índice en la lista de habitaciones
                        val habitacionText = viewHolder.tvHabitacion.text.toString().trim()
                        val positionH = alojamientoActivity?.habitacionList?.indexOfFirst {
                            it.descripcionH.trim().equals(habitacionText, ignoreCase = true)
                        }
                        Log.e("ProcesoH", "Position: $positionH, HabitacionText: $habitacionText")

                        // Selecciona el elemento en el Spinner si se encuentra en la lista
                        if (positionH != null && positionH >= 0) {
                            alojamientoActivity.sp_h.setSelection(positionH)
                        } else {
                            Log.e("ProcesoH", "No se encontró la descripción de la habitación en el Spinner")
                        }

                    } else {
                        Log.e("ProcesoTH", "No se encontró la descripción del TH en el Spinner")
                    }

                }
            } catch (e: Exception) {
                // Maneja cualquier excepción que pueda ocurrir durante el proceso
                Log.e("errorTraer", e.message.toString())
            }
        }

        // Devuelve la vista configurada para el elemento en la posición actual
        return v
    }

    // ViewHolder Pattern para mejorar el rendimiento al evitar llamadas repetidas a findViewById
    private class ViewHolder(
        val id_tp: RelativeLayout,
        val tvID: TextView,
        val tvPrecio: TextView,
        val tvHabitacion: TextView,
        val tvTipHabitacion: TextView,
        val tvProceso: TextView,
        val tvFI: TextView,
        val tvFF: TextView,
        val tvHI: TextView,
        val tvHF: TextView,
    )
}
