package com.example.proyectofinal

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
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
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proyectofinal.Model.Model_Alojamiento
import com.google.gson.Gson
import construredes.net.appgescr.Controlles.Adapter_Alojamiento
import org.json.JSONArray
import org.json.JSONException
import java.net.URLEncoder
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
    lateinit var tP: TextView
    lateinit var btnRegistro: Button
    private lateinit var requestQueue: RequestQueue

    private lateinit var adapter: Adapter_Alojamiento
    private val listaAlojamientos = mutableListOf<Model_Alojamiento>()
    var gvregistro: GridView? = null

    val listaTridimensional = ArrayList<ArrayList<ArrayList<DetInfo>>>()


    /*********************************************/

    lateinit var sp_pro: Spinner
    lateinit var procesoList: List<Proceso>
    var id_pro: String = ""
    var id_pro_descripcion: String = ""

    lateinit var sp_th: Spinner
    lateinit var tipoHabitacionList: List<TipoHabitacion>
    var id_th: String = ""
    var id_th_descripcion: String = ""

    lateinit var sp_h: Spinner
    lateinit var habitacionList: List<Habitacion>
    var id_h: String = ""
    var id_h_descripcion: String = ""

    lateinit var habitacion_PrecioList: List<Habitacion_precio>
    var idHP: String = ""
    var idHP_descripcion: String = ""
    /*********************************************/

    lateinit var imgConsulta: ImageButton
    var str_dni: String = ""
    var url = "https://transportetresdiamantes.com/config_hotel/api.php"

    /*********************************************/
    lateinit var tv_nombre_app: TextView
    lateinit var tv_apellido_app: TextView
    lateinit var edt_dni_app: EditText
    lateinit var tv_dni_antig: TextView
    /*********************************************/

    @SuppressLint("MissingInflatedId")
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
        tP = findViewById(R.id.tP)
        imgConsulta = findViewById(R.id.imgConsulta)
        sp_pro = findViewById(R.id.sPro)
        sp_th = findViewById(R.id.sTH)
        sp_h = findViewById(R.id.sH)
        btnRegistro = findViewById(R.id.btnRegistro)
        gvregistro = findViewById(R.id.gvregistro)
        // Configura el GridView
        adapter = Adapter_Alojamiento(this, listaAlojamientos)
        gvregistro?.adapter = adapter

        requestQueue = Volley.newRequestQueue(this)

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
        btnRegistro.setOnClickListener(){
            try {
                insertarAlojamientoCab()
            }catch (e: Exception) {
                Log.e("error_reg",e.message.toString())
            }
        }

        obtenerTodosLosAlojamientos()

        // INICIOS DE PROCESOS DE USO DE PICKERS (DATE Y TIME)
        getCalendarioLibre(etFI);
        getCalendarioLibre(etFF);
        gettime(etHI);
        gettime(etHF);
        // FIN
        // INICIO DE LLENADO DE SPINNER
        cargar_list_pro();
        cargar_list_th();
    }
    // AGREGAR N CANTIDAD DE ITEMS DE DNI PARA CONSULTAR
    // BEGIN
    private fun agregarInquilinos(n: Int) {
        val inflater = LayoutInflater.from(this)

        containerPhotos?.removeAllViews()

        for (i in 0 until n) {
            val view = inflater.inflate(R.layout.item_dni, containerPhotos, false)
            val btnSelect: ImageButton = view.findViewById(R.id.imgConsultaDni)
            val edt_dni: EditText = view.findViewById(R.id.Edt_dni)
            val tv_nombre: TextView = view.findViewById(R.id.tv_nombre_)
            val tv_apellido: TextView = view.findViewById(R.id.tv_apellidos_)
            val tv_dni: TextView = view.findViewById(R.id.tv_dni_)

            btnSelect.setOnClickListener {
                // AQUI USAMOS EL EVENTO ONCLICK PARA CONSULTAR EL DNI MEDIANTE SUS EDITTET ENVIAR LA DATA (DNI) Y PROCESARLA
                tv_nombre_app = tv_nombre
                tv_apellido_app  = tv_apellido
                edt_dni_app = edt_dni
                tv_dni_antig = tv_dni
                // FUNCIÓN DE CONSULTA DE DNI
                Consultar_Dni()
            }
            containerPhotos?.addView(view)
        }
    }
    // END
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

    fun cargar_list_pro() {
        val url = "https://transportetresdiamantes.com/config_hotel/listar_proceso.php"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val dataList = ArrayList<Proceso>()
                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    val idP = jsonObject.getString("idP")
                    val descripcionP = jsonObject.getString("descripcionP")
                    dataList.add(Proceso(idP, descripcionP))
                }
                procesoList = dataList

                // Configurar el adaptador del Spinner
                val adapter = ProcesoAdapter(this, R.layout.spinner_item, dataList)
                sp_pro.adapter = adapter
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }
        )

        requestQueue.add(jsonArrayRequest)

        // Configurar el listener para obtener el ID del ítem seleccionado
        sp_pro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = procesoList[position]
                id_pro = selectedItem.idP
                id_pro_descripcion = selectedItem.descripcionP

                // Aquí agregas el Log.d para verificar el valor de id_pro
                Log.d("IDPseleccionado", "Valor de id_pro: $id_pro")

                try {
                    // Aquí puedes agregar más lógica si es necesario
                } catch (e: Exception) {
                    Toast.makeText(this@Alojamiento, e.message, Toast.LENGTH_SHORT).show()
                    Log.e("erorrenjson", e.message.toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada si no se selecciona ningún ítem
            }
        }
    }

    fun cargar_list_th() {
        val url = "https://transportetresdiamantes.com/config_hotel/listar_combo_tipo_habitacion.php"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val dataList = ArrayList<TipoHabitacion>()
                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    val idTH = jsonObject.getString("idTH")
                    val descripcionTH = jsonObject.getString("descripcionTH")
                    dataList.add(TipoHabitacion(idTH, descripcionTH))
                }
                tipoHabitacionList = dataList

                // Configurar el adaptador del Spinner
                val adapter = TipoHabitacionAdapter(this, R.layout.spinner_item, dataList)
                sp_th.adapter = adapter
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }
        )

        requestQueue.add(jsonArrayRequest)

        // Configurar el listener para obtener el ID del ítem seleccionado
        sp_th.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = tipoHabitacionList[position]
                id_th = selectedItem.idTH
                id_th_descripcion = selectedItem.descripcionTH
                try {
                    cargar_list_h(id_th)
                } catch (e: Exception) {
                    Toast.makeText(this@Alojamiento, e.message, Toast.LENGTH_SHORT).show()
                    Log.e("erorrenjson", e.message.toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada si no se selecciona ningún ítem
            }
        }
    }
    fun cargar_list_h(idTH: String) {
        val url = "https://transportetresdiamantes.com/config_hotel/listar_combo_habitacion.php"

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.POST, url, null,
            Response.Listener { response ->
                val dataList = ArrayList<Habitacion>()
                try {
                    // Manejar la respuesta como un JSONArray
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val idH = jsonObject.getString("idH")
                        val descripcionH = jsonObject.getString("descripcionH")
                        dataList.add(Habitacion(idH, descripcionH))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this@Alojamiento, "Error en la respuesta del servidor: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                habitacionList = dataList

                // Configurar el adaptador del Spinner
                val adapter = HabitacionAdapter(this@Alojamiento, R.layout.spinner_item, dataList)
                sp_h.adapter = adapter
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                Toast.makeText(this@Alojamiento, "Error en la solicitud: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }

            override fun getBody(): ByteArray {
                val params = HashMap<String, String>()
                params["idTH"] = idTH

                // Convertir el mapa a un formato de URL codificado
                val encodedParams = params.map { (key, value) ->
                    "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
                }.joinToString("&")

                return encodedParams.toByteArray(Charsets.UTF_8)
            }
        }

        requestQueue.add(jsonArrayRequest)

        // Configurar el listener para obtener el ID del ítem seleccionado
        sp_h.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = habitacionList[position]
                id_h = selectedItem.idH
                id_h_descripcion = selectedItem.descripcionH
                try {
                    cargar_precio_h(id_h)
                }catch (e: Exception){
                    Log.e("errorprecio", e.message.toString())
                }

                // Toast.makeText(this@Alojamiento, id_h, Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada si no se selecciona ningún ítem
            }
        }
    }
    fun cargar_precio_h(idH: String) {
        val url = "https://transportetresdiamantes.com/config_hotel/cargar_habitacion_Precio.php"

        val jsonArrayRequest = object : JsonArrayRequest(
            Method.POST, url, null,
            Response.Listener { response ->
                val dataList = ArrayList<Habitacion_precio>()

                try {
                    // Imprimir la respuesta del servidor para depuración
                    Log.d("CargarPrecioH", "Respuesta del servidor: $response")

                    // Manejar la respuesta como un JSONArray
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        idHP = jsonObject.getString("idHP")
                        val precio = jsonObject.getString("precio")
                        dataList.add(Habitacion_precio(idHP, precio))
                    }
                    habitacion_PrecioList = dataList

                    // Mostrar el primer elemento si existe
                    if (habitacion_PrecioList.isNotEmpty()) {
                        val selectedItem = habitacion_PrecioList[0]
                        idHP = selectedItem.idHP
                        idHP_descripcion = selectedItem.precio
                        tP.setText(selectedItem.precio)

                    } else {
                        Toast.makeText(this@Alojamiento, "No hay datos disponibles", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this@Alojamiento, "Error en el procesamiento de datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                Toast.makeText(this@Alojamiento, "Error en la solicitud: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                return mapOf("Content-Type" to "application/x-www-form-urlencoded")
            }

            override fun getBody(): ByteArray {
                val params = mapOf("idH" to idH)
                val encodedParams = params.map { (key, value) ->
                    "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
                }.joinToString("&")

                Log.d("CargarPrecioH", "Parametros codificados: $encodedParams")
                return encodedParams.toByteArray(Charsets.UTF_8)
            }
        }

        requestQueue.add(jsonArrayRequest)
    }

    // MODELO
    data class Proceso(val idP: String, val descripcionP: String)
    data class TipoHabitacion(val idTH: String, val descripcionTH: String)
    data class Habitacion(val idH: String, val descripcionH: String)
    data class Habitacion_precio(val idHP: String, val precio: String)

    // SEG MODELO
    class ProcesoAdapter(context: Context, private val resource: Int, private val items: List<Proceso>)
        : ArrayAdapter<Proceso>(context, resource, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
            val item = getItem(position)
            val textView = view.findViewById<TextView>(R.id.spinner_item_text)
            textView.text = item?.descripcionP
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return getView(position, convertView, parent)
        }
        override fun getItem(position: Int): Proceso? {
            return super.getItem(position)
        }
    }
    class TipoHabitacionAdapter(context: Context, private val resource: Int, private val items: List<TipoHabitacion>)
        : ArrayAdapter<TipoHabitacion>(context, resource, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
            val item = getItem(position)
            val textView = view.findViewById<TextView>(R.id.spinner_item_text)
            textView.text = item?.descripcionTH
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return getView(position, convertView, parent)
        }
        override fun getItem(position: Int): TipoHabitacion? {
            return super.getItem(position)
        }
    }
    class HabitacionAdapter(context: Context, private val resource: Int, private val items: List<Habitacion>)
        : ArrayAdapter<Habitacion>(context, resource, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
            val item = getItem(position)
            val textView = view.findViewById<TextView>(R.id.spinner_item_text)
            textView.text = item?.descripcionH
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return getView(position, convertView, parent)
        }
        override fun getItem(position: Int): Habitacion? {
            return super.getItem(position)
        }
    }


    fun insertarAlojamientoCab() {
        val url = "https://transportetresdiamantes.com/config_hotel/insertar_alojamiento.php"

        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    Log.d("agregarAlojamiento", "Respuesta del servidor: $response")
                    Toast.makeText(this, "Alojamiento registrado con éxito", Toast.LENGTH_SHORT).show()
                    limpiarElementos()
                } catch (e: Exception) {
                    Log.e("agregarAlojamiento", "Error en la respuesta: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()

                // Construir el mensaje de error
                val errorMsg = error.networkResponse?.let {
                    val statusCode = it.statusCode
                    val responseBody = String(it.data)
                    "Código de error: $statusCode, Respuesta: $responseBody"
                } ?: "Error en la solicitud: ${error.message}"

                // Imprimir el mensaje de error en Logcat
                Log.e("esolicitud", errorMsg)

                // Mostrar el mensaje de error en un Toast
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }

        ) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["idTH"] = id_th
                params["idH"] = id_h
                params["idHP"] = idHP
                params["idP"] = id_pro
                params["fechaInicio"] = etFI.text.toString()
                params["horaInicio"] = etHI.text.toString()
                params["fechaFin"] = etFF.text.toString()
                params["horaFin"] = etHF.text.toString()
                val gson = Gson()
                val json = gson.toJson(listaTridimensional)
                params["det_list"] = json

                Log.d("insertarAlojamientoCab", "Params: $params")
                return params
            }
        }

        requestQueue.add(stringRequest)
    }


    fun obtenerTodosLosAlojamientos() {
        val url = "https://transportetresdiamantes.com/config_hotel/listar_alojamiento_cab.php"

        val stringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                try {
                    // Procesa la respuesta JSON
                    val jsonArray = JSONArray(response)
                    listaAlojamientos.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val idAC = jsonObject.getString("idAC")
                        val descripcionTH = jsonObject.getString("descripcionTH")
                        val descripcionH = jsonObject.getString("descripcionH")
                        val descripcionP = jsonObject.getString("descripcionP")
                        val precio = jsonObject.getDouble("precio")
                        val fechaInicio = jsonObject.getString("fechaInicio")
                        val fechaFin = jsonObject.getString("fechaFin")
                        val horaInicio = jsonObject.getString("horaInicio")
                        val horaFin = jsonObject.getString("horaFin")

                        // Aquí debes asegurarte de proporcionar todos los parámetros al crear Model_Alojamiento
                        val nuevoAlojamiento = Model_Alojamiento(
                            countID = i, // Puedes usar el índice del bucle como countID si no tienes otro valor
                            idAC = idAC,
                            idTH_descripcion = descripcionTH,
                            idH_descripcion = descripcionH,
                            idP_descripcion = descripcionP,
                            fechaInicio = fechaInicio,
                            idHP_descripcion = precio,
                            fechaFin = fechaFin,
                            horaInicio = horaInicio,
                            horaFin = horaFin
                        )
                        listaAlojamientos.add(nuevoAlojamiento)
                    }
                    adapter.notifyDataSetChanged() // Actualiza la vista
                } catch (e: Exception) {
                    Log.e("obtenerAlojamientos", "Error en la respuesta: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error en la solicitud: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun limpiarElementos(){
        etFI.setText("")
        etHI.setText("")
        etFF.setText("")
        etHF.setText("")
    }


    // PROCESO DE CONSULTA DE DNI
    //  BEGIN
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
                        // EL RESPONSE OBTENIDO EN FORMATO JSON LO SERIALIZAMOS Y TOMAMOS LOS CAMPOS NOMBRES, AP PATERNO, AP MATERNO
                        val apiResponse = parseJsonResponse(response)
                        tv_nombre_app.setText(apiResponse.data.nombres)
                        tv_apellido_app.setText(apiResponse.data.apellido_paterno + " " + apiResponse.data.apellido_materno)
                        if (tv_dni_antig.text.toString() == ""){
                            tv_dni_antig.setText(edt_dni_app.text.toString())
                        }
                        // array list
                        try{
                            agregarAlArray(tv_nombre_app, tv_apellido_app, edt_dni_app, tv_dni_antig)
                        }catch (e: Exception){
                            Log.e("error_Array", e.message.toString())
                        }


                    },
                    Response.ErrorListener { error ->
                        progressDialog.dismiss()
                        Log.e("VolleyError", error.toString())
                        // Toast.makeText(this@Logeo, error.message.toString(), Toast.LENGTH_SHORT).show()
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        return hashMapOf(
                            // ENVIO DE PARÁMETRO DE DNI TOMAMOS COMO REFERENCIA EL CAMPO usuario, CAMPO NOMBRADO EN EL WEB SERVICE DE PHP
                            "usuario" to str_dni
                        )
                    }
                }

                val requestQueue: RequestQueue = Volley.newRequestQueue(this)
                requestQueue.add(request)
            }
        }
    }
    // FUNCIÓN DE PARSEO DE DATA DE DNI
    fun parseJsonResponse(jsonResponse: String): ApiResponse {
        val gson = Gson()
        return gson.fromJson(jsonResponse, ApiResponse::class.java)
    }

    // CREAMOS LA CLASE DE API RESPONSE PARA VALIDAR EL JSON
    data class ApiResponse(
        val success: Boolean,
        val data: UserData,
        val time: Double,
        val source: String
    )
    /*  CREAMOS NUESTRA CLASE UserData (MODELO) PARA QUE LOS DATOS DEL JSON SEAN TOMANDOS EN UN LIST TIPO CLASE Y COMO SE PUEDE VER
      SE TIENE LOS CAMPOS, NOMBRES, AP PATERNO, AP MATERNO */
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

    // END

    data class DetInfo(
        var dni: String,
        var nombre: String,
        var apellido: String
    )



    fun agregarAlArray(
        textViewName: TextView,
        textViewApellido: TextView,
        edtDni: EditText,
        tvDniAnterior: TextView?
    ) {
        val dniAnterior = tvDniAnterior?.text.toString()
        val dniNuevo = edtDni.text.toString()
        Log.e("datoingreso", dniAnterior + " " + dniNuevo )
        var encontrado = false

        if (dniAnterior.isNotEmpty() && dniAnterior != dniNuevo) {
            val nuevaInfo = DetInfo(dniNuevo, textViewName.text.toString(), textViewApellido.text.toString())

            // Buscar y reemplazar en la lista tridimensional
            for (lista2D in listaTridimensional) {
                for (lista1D in lista2D) {
                    for (i in lista1D.indices) {
                        if (lista1D[i].dni == dniAnterior) {
                            // Reemplazar el dato
                            lista1D[i] = nuevaInfo
                            encontrado = true
                        }
                    }
                }
            }

            if (!encontrado) {
                Log.e("error_reemplazo", "No se encontró ningún dato con DNI $dniAnterior para reemplazar.")
            }
        } else {
            try {
                val nuevaInfo = DetInfo(dniNuevo, textViewName.text.toString(), textViewApellido.text.toString())

                // Crear la nueva estructura
                val lista1D = ArrayList<DetInfo>()
                lista1D.add(nuevaInfo)

                val lista2D = ArrayList<ArrayList<DetInfo>>()
                lista2D.add(lista1D)

                listaTridimensional.add(lista2D)

                Log.e("lista_tridimensional", listaTridimensional.size.toString())
            } catch (e: Exception) {
                Log.e("error_array_2", e.message.toString())
            }
        }

        // Mostrar el contenido de la lista tridimensional
        try {
            for (lista2D in listaTridimensional) {
                for (lista1D in lista2D) {
                    for (info in lista1D) {
                        // Acceder a los datos
                        Log.e("dato_array", "Nombre: ${info.nombre}, Apellido: ${info.apellido}, DNI: ${info.dni}")
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("error_array", ex.message.toString())
        }
    }




}


