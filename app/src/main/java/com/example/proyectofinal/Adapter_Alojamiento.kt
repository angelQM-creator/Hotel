package construredes.net.appgescr.Controlles;

import android.annotation.SuppressLint
import android.app.Activity;
import android.content.Context;
import android.graphics.Color
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.proyectofinal.Alojamiento
import com.example.proyectofinal.Model.Model_Alojamiento
import com.example.proyectofinal.R


class Adapter_Alojamiento(private val mContext: Context, private val lista: MutableList<Model_Alojamiento>) : BaseAdapter() {

    init {
        if (mContext !is Activity) {
            throw IllegalArgumentException("El contexto debe ser una instancia de Activity")
        }
    }

    override fun getCount(): Int {
        return lista.size
    }

    override fun getItem(position: Int): Any {
        return lista[position]
    }

    override fun getItemId(position: Int): Long {
        return lista[position].countID.toLong()
    }

    @SuppressLint("MissingInflatedId")
    private var selectedPosition: Int = -1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val v: View

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
            v = convertView
            viewHolder = v.tag as ViewHolder
        }

        // Configuración de los valores
        val alojamiento = lista[position]
        viewHolder.tvID.text = alojamiento.idAC
        viewHolder.tvPrecio.text = alojamiento.idHP_descripcion.toString()
        viewHolder.tvHabitacion.text = alojamiento.idH_descripcion
        viewHolder.tvTipHabitacion.text = alojamiento.idTH_descripcion
        viewHolder.tvProceso.text = alojamiento.idP_descripcion
        viewHolder.tvFI.text = alojamiento.fechaInicio
        viewHolder.tvFF.text = alojamiento.fechaFin
        viewHolder.tvHI.text = alojamiento.horaInicio
        viewHolder.tvHF.text = alojamiento.horaFin

        // Restaura el color de fondo predeterminado para la vista actual
        if (position == selectedPosition) {
            viewHolder.id_tp.setBackgroundColor(Color.parseColor("#00FF00"))
        } else {
            viewHolder.id_tp.setBackgroundColor(Color.parseColor("#6D92AD"))
        }

        viewHolder.id_tp.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()

            try {
                (mContext as? Activity)?.let { activity ->
                    val alojamientoActivity = activity as? Alojamiento
                    alojamientoActivity?.etFI?.setText(viewHolder.tvFI.text.toString())
                    alojamientoActivity?.etFF?.setText(viewHolder.tvFF.text.toString())
                    alojamientoActivity?.etHI?.setText(viewHolder.tvHI.text.toString())
                    alojamientoActivity?.etHF?.setText(viewHolder.tvHF.text.toString())

                    // Obtener el texto del proceso desde el TextView
                    val procesoText = viewHolder.tvProceso.text.toString().trim()

                    // Buscar el índice en la lista de procesos donde la descripción coincida
                    val position = alojamientoActivity?.procesoList?.indexOfFirst {
                        it.descripcionP.trim().equals(procesoText, ignoreCase = true)
                    }

                    // Registrar en Log si se encuentra la posición
                    Log.e("ProcesoP", "Position: $position, ProcesoText: $procesoText")

                    // Si la posición es válida (no es -1), selecciona el elemento en el Spinner
                    if (position != null && position >= 0) {
                        alojamientoActivity.sp_pro.setSelection(position)
                    } else {
                        Log.e("ProcesoP", "No se encontró la descripción del proceso en el Spinner")
                    }

                    // Obtener el texto del proceso desde el TextView
                    val thText = viewHolder.tvTipHabitacion.text.toString().trim()

                    // Buscar el índice en la lista de procesos donde la descripción coincida
                    val positionTH = alojamientoActivity?.tipoHabitacionList?.indexOfFirst {
                        it.descripcionTH.trim().equals(thText, ignoreCase = true)
                    }

                    // Registrar en Log si se encuentra la posición
                    Log.e("ProcesoTH", "Position: $positionTH, THText: $thText")

                    // Si la posición es válida (no es -1), selecciona el elemento en el Spinner
                    if (positionTH != null && positionTH >= 0) {
                        alojamientoActivity.sp_th.setSelection(positionTH)

                        val habitacionText = viewHolder.tvHabitacion.text.toString().trim()

                        val positionH = alojamientoActivity?.habitacionList?.indexOfFirst {
                            it.descripcionH.trim().equals(habitacionText, ignoreCase = true)
                        }

                        // Registrar en Log si se encuentra la posición
                        Log.e("ProcesoH", "Position: $positionH, HabitacionText: $habitacionText")

                        // Si la posición es válida (no es -1), selecciona el elemento en el Spinner
                        if (positionH != null && positionH >= 0) {
                            alojamientoActivity.sp_h.setSelection(positionH)

                            // Asignar el ID de AlojamientoCab y registrar en Log
                            val idAC = alojamiento.idAC // Asumiendo que `alojamiento` es el objeto actual
                            viewHolder.tvID.text = idAC.toString()
                            Log.e("ID_AC", "ID de AlojamientoCab: $idAC")
                        } else {
                            Log.e("ProcesoH", "No se encontró la descripción de la habitacion en el Spinner")
                        }

                    } else {
                        Log.e("ProcesoTH", "No se encontró la descripción del TH en el Spinner")
                    }

                }
            } catch (e: Exception) {
                Log.e("errorTraer", e.message.toString())
            }
        }


        return v
    }



    // ViewHolder Pattern para mejorar el rendimiento
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
