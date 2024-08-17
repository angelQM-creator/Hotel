package com.example.proyectofinal

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class Alojamiento : AppCompatActivity() {

    private var containerPhotos: LinearLayout? = null
    lateinit var edt_cant_inquilinos_: EditText
    lateinit var etFI: EditText
    lateinit var etFF: EditText
    lateinit var etHI: EditText
    lateinit var etHF: EditText

    lateinit var imgConsulta: ImageButton
    var str_dni: String = ""
    var url = "https://transportetresdiamantes.com/config_hotel/api.php"

    /*********************************************/
    lateinit var tv_nombre_app: TextView
    lateinit var tv_apellido_app: TextView
    lateinit var edt_dni_app: EditText

    /*********************************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_alojamiento)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val toolbar: Toolbar = findViewById(R.id.toolbarA)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24_white) // Usa el drawable con el color cambiado
        }
        edt_cant_inquilinos_ = findViewById(R.id.edt_cant_inquilinos)
        etFI = findViewById(R.id.etFI)
        etFF = findViewById(R.id.etFF)
        etHI = findViewById(R.id.etHI)
        etHF = findViewById(R.id.etHF)
        imgConsulta = findViewById(R.id.imgConsulta)
        imgConsulta.setOnClickListener() {
            try {
                if (edt_cant_inquilinos_.text.toString() == "") {
                    Toast.makeText(this, "INGRESAR UN VALOR PARA PROCEDER", Toast.LENGTH_SHORT)
                } else {
                    containerPhotos = findViewById(R.id.containerPhotos);
                    agregarInquilinos(edt_cant_inquilinos_.text.toString().toInt())
                }
            } catch (e: Exception) {
                Toast.makeText(this, "INGRESAR UN VALOR PARA PROCEDER", Toast.LENGTH_SHORT)
            }

        }

        getCalendarioLibre(etFI);
        getCalendarioLibre(etFF);
        gettime(etHI);
        gettime(etHF);


    }

    private fun agregarInquilinos(n: Int) {
        val inflater = LayoutInflater.from(this)

        containerPhotos?.removeAllViews()
        // containerPhotos_durante.removeAllViews()
        // containerPhotos_despues.removeAllViews()

        for (i in 0 until n) {
            val view = inflater.inflate(R.layout.item_dni, containerPhotos, false)
            val btnSelect: ImageButton = view.findViewById(R.id.imgConsultaDni)
            val edt_dni: EditText = view.findViewById(R.id.Edt_dni)
            val tv_nombre: TextView = view.findViewById(R.id.tv_nombre_)
            val tv_apellido: TextView = view.findViewById(R.id.tv_apellidos_)

            btnSelect.setOnClickListener {
                tv_nombre_app = tv_nombre
                tv_apellido_app  = tv_apellido
                edt_dni_app = edt_dni
                Consultar_Dni()
            }
            containerPhotos?.addView(view)
        }
    }

    private fun gettime(editText: EditText) {
        editText.setOnClickListener { TimeSetter(editText) }
    }

    class TimeSetter(private val editText: EditText) : View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

        private var calendar: Calendar? = null
        private var format: SimpleDateFormat? = null

        init {
            editText.onFocusChangeListener = this
            editText.setOnClickListener(this)
        }

        override fun onFocusChange(view: View, hasFocus: Boolean) {
            if (hasFocus) {
                showPicker(view)
            }
        }

        override fun onClick(view: View) {
            showPicker(view)
        }

        private fun showPicker(view: View) {
            if (calendar == null) {
                calendar = Calendar.getInstance()
            }

            val hour = calendar!!.get(Calendar.HOUR_OF_DAY)
            val minute = calendar!!.get(Calendar.MINUTE)

            TimePickerDialog(view.context, this, hour, minute, true).show()
        }

        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            calendar!!.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar!!.set(Calendar.MINUTE, minute)
            if (format == null) {
                format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            }
            editText.setText(format!!.format(calendar!!.time))
            TimeAdd()
        }

        private fun TimeAdd() {
            // Implementation of TimeAdd method
        }
    }



    private fun getCalendarioLibre(editText: EditText) {
        editText.setOnClickListener { showDatePickerDialogLibre(editText) }
    }

    private fun showDatePickerDialogLibre(editText: EditText) {
        val newFragmentLibre = DatePickerFragmnetLibre.newInstance { datePicker, year, month, day ->
            val selectedDate = "${DosDigitos(day)}/${DosDigitos(month + 1)}/$year"
            editText.setText(selectedDate)
        }
        newFragmentLibre.show(supportFragmentManager, "datePicker")
    }
    private fun DosDigitos(n: Int): String {
        return if (n <= 9) "0$n" else n.toString()
    }

    class DatePickerFragmnetLibre : DialogFragment() {
        private var listener: OnDateSetListener? = null


        fun setListener(listener: OnDateSetListener?) {
            this.listener = listener
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c: Calendar = Calendar.getInstance()
            val anio: Int = c.get(Calendar.YEAR)
            val mes: Int = c.get(Calendar.MONTH)
            val dia: Int = c.get(Calendar.DAY_OF_MONTH)

            val cde = getActivity()?.let { DatePickerDialog(it, listener, dia, mes, anio) }
            cde?.datePicker?.init(anio, mes, dia, null)
            return cde!!
            //            return  new DatePickerDialog(getActivity(),listener,dia,mes,anio);
        }

        companion object {
            fun newInstance(listener: OnDateSetListener?): DatePickerFragmnetLibre {
                val fragment = DatePickerFragmnetLibre()
                fragment.setListener(listener)
                return fragment
            }
        }
    }


    fun Consultar_Dni() {
        when {
            edt_dni_app.text.toString().isEmpty() -> {
                Toast.makeText(this, "Enter Dni", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Por favor espera...")
                progressDialog.show()

                str_dni = edt_dni_app.text.toString().trim()

                val request = object : StringRequest(
                    Request.Method.POST, url,
                    Response.Listener<String> { response ->
                        progressDialog.dismiss()

                        // Usa android.util.Log
                        Log.d("ServerResponse", response)

                        val apiResponse = parseJsonResponse(response)
                        tv_nombre_app.setText(apiResponse.data.nombres)
                        tv_apellido_app.setText(apiResponse.data.apellido_paterno + " " + apiResponse.data.apellido_materno)
                       //Toast.makeText( applicationContext,"Nombre completo: ${apiResponse.data.nombres}",Toast.LENGTH_SHORT).show()
//                        else {
////                            Toast.makeText(this@Logeo, response, Toast.LENGTH_SHORT).show()
////                        }
                    },
                    Response.ErrorListener { error ->
                        progressDialog.dismiss()
                        Log.e("VolleyError", error.toString())
                        // Toast.makeText(this@Logeo, error.message.toString(), Toast.LENGTH_SHORT).show()
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        return hashMapOf(
                            "usuario" to str_dni
                        )
                    }
                }

                val requestQueue: RequestQueue = Volley.newRequestQueue(this)
                requestQueue.add(request)
            }
        }
    }
    fun parseJsonResponse(jsonResponse: String): ApiResponse {
        val gson = Gson()
        return gson.fromJson(jsonResponse, ApiResponse::class.java)
    }

    data class ApiResponse(
        val success: Boolean,
        val data: UserData,
        val time: Double,
        val source: String
    )
    data class UserData(
        val numero: String,
        val nombre_completo: String,
        val nombres: String,
        val apellido_paterno: String,
        val apellido_materno: String,
        val codigo_verificacion: Int,
        val ubigeo_sunat: String,
        val ubigeo: List<Any?>,
        val direccion: String
    )

}