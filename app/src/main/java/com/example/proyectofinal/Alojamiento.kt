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
    lateinit var btnEditar: Button
    lateinit var btnEliminar: Button
    private var currentIdAC: Int? = null
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

    var id_ac: String = ""
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

    @SuppressLint("MissingInflatedId") // Suprime advertencias sobre IDs de vistas infladas que podrían no estar presentes
    override fun onCreate(savedInstanceState: Bundle?) { // Método llamado cuando se crea la actividad
        super.onCreate(savedInstanceState) // Llama al método onCreate de la superclase para asegurarse de que la actividad se inicialice correctamente

        setContentView(R.layout.activity_alojamiento) // Establece el diseño de la actividad usando el archivo XML correspondiente
        enableEdgeToEdge() // Habilita el diseño "Edge-to-Edge" para que el contenido se extienda hasta los bordes de la pantalla

        // Configura el manejo de los márgenes de la ventana para el contenedor principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) // Obtiene los márgenes de las barras del sistema (como la barra de estado y la barra de navegación)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom) // Ajusta el relleno del contenedor para que no se superponga con las barras del sistema
            insets // Devuelve los insets para que puedan aplicarse otras operaciones si es necesario
        }

        // Configura la barra de herramientas (Toolbar)
        val toolbar: Toolbar = findViewById(R.id.toolbarA) // Obtiene una referencia al Toolbar desde el diseño
        setSupportActionBar(toolbar) // Establece el Toolbar como la barra de acción de la actividad
        supportActionBar?.apply { // Aplica configuraciones a la ActionBar si está disponible
            setDisplayHomeAsUpEnabled(true) // Muestra el botón de "Up" en la ActionBar
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24_white) // Establece el ícono del botón de "Up"
        }

        // Inicializa las vistas y componentes de la interfaz de usuario
        edt_cant_inquilinos_ = findViewById(R.id.edt_cant_inquilinos) // Campo de texto para ingresar la cantidad de inquilinos
        etFI = findViewById(R.id.etFI) // Campo de texto para la fecha de inicio
        etFF = findViewById(R.id.etFF) // Campo de texto para la fecha de fin
        etHI = findViewById(R.id.etHI) // Campo de texto para la hora de inicio
        etHF = findViewById(R.id.etHF) // Campo de texto para la hora de fin
        tP = findViewById(R.id.tP) // Campo de texto para algún otro propósito (según el diseño)
        imgConsulta = findViewById(R.id.imgConsulta) // Imagen o botón para realizar consultas
        sp_pro = findViewById(R.id.sPro) // Spinner para seleccionar un proceso
        sp_th = findViewById(R.id.sTH) // Spinner para seleccionar un tipo de habitación
        sp_h = findViewById(R.id.sH) // Spinner para seleccionar una habitación
        btnRegistro = findViewById(R.id.btnRegistro) // Botón para registrar datos
        btnEditar = findViewById(R.id.btnEditar) // Botón para editar datos
        btnEliminar = findViewById(R.id.btnEliminar) // Botón para eliminar datos
        gvregistro = findViewById(R.id.gvregistro) // GridView para mostrar registros

        // Configura el adaptador para el GridView
        adapter = Adapter_Alojamiento(this, listaAlojamientos) // Crea una instancia del adaptador con la lista de alojamientos
        gvregistro?.adapter = adapter // Asigna el adaptador al GridView

        requestQueue = Volley.newRequestQueue(this) // Crea una cola de solicitudes para Volley para manejar solicitudes de red

        // Configura el listener para el botón de consulta
        imgConsulta.setOnClickListener() {
            try {
                if (edt_cant_inquilinos_.text.toString() == "") { // Verifica si el campo de cantidad de inquilinos está vacío
                    Toast.makeText(this, "INGRESAR UN VALOR PARA PROCEDER", Toast.LENGTH_SHORT).show() // Muestra un mensaje si el campo está vacío
                } else {
                    containerPhotos = findViewById(R.id.containerPhotos); // Inicializa un contenedor para fotos (según el diseño)
                    agregarInquilinos(edt_cant_inquilinos_.text.toString().toInt()) // Llama a la función para agregar inquilinos con el valor ingresado
                }
            } catch (e: Exception) {
                Toast.makeText(this, "INGRESAR UN VALOR PARA PROCEDER", Toast.LENGTH_SHORT).show() // Muestra un mensaje de error en caso de excepción
            }
        }

        // Configura el listener para el botón de registro
        btnRegistro.setOnClickListener(){
            try {
                insertarAlojamientoCab() // Llama a la función para insertar un nuevo alojamiento
            }catch (e: Exception) {
                Log.e("error_reg",e.message.toString()) // Registra el error en el log en caso de excepción
            }
        }

        // Configura el listener para el botón de edición
        btnEditar.setOnClickListener {
            val idAC = currentIdAC ?: run {
                Toast.makeText(this, "ID de Alojamiento no encontrado", Toast.LENGTH_SHORT).show() // Muestra un mensaje si no se encuentra el ID de alojamiento
                return@setOnClickListener // Sale del listener si no se encuentra el ID
            }

            val fechaInicio = etFI.text.toString() // Obtiene la fecha de inicio
            val fechaFin = etFF.text.toString() // Obtiene la fecha de fin
            val horaInicio = etHI.text.toString() // Obtiene la hora de inicio
            val horaFin = etHF.text.toString() // Obtiene la hora de fin

            // Obtiene las posiciones seleccionadas de los spinners
            val idTH = tipoHabitacionList[sp_th.selectedItemPosition].idTH // ID del tipo de habitación seleccionado
            val idH = habitacionList[sp_h.selectedItemPosition].idH // ID de la habitación seleccionada
            val idP = procesoList[sp_pro.selectedItemPosition].idP // ID del proceso seleccionado

            // Llama a la función para editar el alojamiento
            editarAlojamientoCab(idAC, fechaInicio, fechaFin, horaInicio, horaFin, idTH.toInt(), idH.toInt(), idP.toInt(), idHP.toInt())
        }

        obtenerTodosLosAlojamientos() // Llama a la función para obtener todos los alojamientos

        // Configuración de pickers para fechas y horas
        getCalendarioLibre(etFI); // Configura el picker de fecha para el campo de fecha de inicio
        getCalendarioLibre(etFF); // Configura el picker de fecha para el campo de fecha de fin
        gettime(etHI); // Configura el picker de hora para el campo de hora de inicio
        gettime(etHF); // Configura el picker de hora para el campo de hora de fin

        // Llenado de los spinners
        cargar_list_pro(); // Carga la lista de procesos en el spinner correspondiente
        cargar_list_th(); // Carga la lista de tipos de habitación en el spinner correspondiente
    }

    // AGREGAR N CANTIDAD DE ITEMS DE DNI PARA CONSULTAR
    // BEGIN
    private fun agregarInquilinos(n: Int) { // Función privada para agregar un número específico de inquilinos al contenedor
        val inflater = LayoutInflater.from(this) // Obtiene un LayoutInflater para inflar vistas desde los recursos de diseño

        containerPhotos?.removeAllViews() // Elimina todas las vistas actuales del contenedor para prepararlo para nuevas vistas

        for (i in 0 until n) { // Itera desde 0 hasta n-1 para agregar n vistas
            val view = inflater.inflate(R.layout.item_dni, containerPhotos, false) // Infla una vista del diseño item_dni sin agregarla inmediatamente al contenedor
            val btnSelect: ImageButton = view.findViewById(R.id.imgConsultaDni) // Obtiene una referencia al ImageButton para seleccionar el DNI
            val edt_dni: EditText = view.findViewById(R.id.Edt_dni) // Obtiene una referencia al EditText donde se ingresa el DNI
            val tv_nombre: TextView = view.findViewById(R.id.tv_nombre_) // Obtiene una referencia al TextView para mostrar el nombre
            val tv_apellido: TextView = view.findViewById(R.id.tv_apellidos_) // Obtiene una referencia al TextView para mostrar el apellido
            val tv_dni: TextView = view.findViewById(R.id.tv_dni_) // Obtiene una referencia al TextView para mostrar el DNI

            // Configura el listener para el botón de selección
            btnSelect.setOnClickListener {
                // AQUI USAMOS EL EVENTO ONCLICK PARA CONSULTAR EL DNI MEDIANTE SUS EDITTET ENVIAR LA DATA (DNI) Y PROCESARLA
                tv_nombre_app = tv_nombre // Asigna la referencia del TextView de nombre a una variable global o de clase
                tv_apellido_app  = tv_apellido // Asigna la referencia del TextView de apellido a una variable global o de clase
                edt_dni_app = edt_dni // Asigna la referencia del EditText del DNI a una variable global o de clase
                tv_dni_antig = tv_dni // Asigna la referencia del TextView del DNI a una variable global o de clase
                // FUNCIÓN DE CONSULTA DE DNI
                Consultar_Dni() // Llama a la función para consultar el DNI usando las referencias asignadas
            }
            containerPhotos?.addView(view) // Agrega la vista inflada al contenedor
        }
    }

    // END
    private fun gettime(editText: EditText) {
        editText.setOnClickListener { TimeSetter(editText) }
    }
    class TimeSetter(private val editText: EditText) : View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

        private var calendar: Calendar? = null // Variable para almacenar una instancia de Calendar
        private var format: SimpleDateFormat? = null // Variable para almacenar una instancia de SimpleDateFormat

        init {
            editText.onFocusChangeListener = this // Establece el listener para cambios de foco en el EditText
            editText.setOnClickListener(this) // Establece el listener para clics en el EditText
        }

        override fun onFocusChange(view: View, hasFocus: Boolean) {
            if (hasFocus) { // Si el EditText obtiene el foco
                showPicker(view) // Muestra el selector de tiempo
            }
        }

        override fun onClick(view: View) {
            showPicker(view) // Muestra el selector de tiempo al hacer clic en el EditText
        }

        private fun showPicker(view: View) {
            if (calendar == null) { // Si la instancia de Calendar es nula
                calendar = Calendar.getInstance() // Inicializa Calendar con la fecha y hora actuales
            }

            val hour = calendar!!.get(Calendar.HOUR_OF_DAY) // Obtiene la hora actual del Calendar
            val minute = calendar!!.get(Calendar.MINUTE) // Obtiene los minutos actuales del Calendar

            TimePickerDialog(view.context, this, hour, minute, true).show() // Muestra el TimePickerDialog con la hora y minutos actuales
        }

        override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
            calendar!!.set(Calendar.HOUR_OF_DAY, hourOfDay) // Establece la hora seleccionada en el Calendar
            calendar!!.set(Calendar.MINUTE, minute) // Establece los minutos seleccionados en el Calendar
            if (format == null) { // Si la instancia de SimpleDateFormat es nula
                format = SimpleDateFormat("HH:mm:ss", Locale.getDefault()) // Inicializa SimpleDateFormat para el formato de hora
            }
            editText.setText(format!!.format(calendar!!.time)) // Establece el texto del EditText con la hora seleccionada en formato "HH:mm:ss"
            TimeAdd() // Llama a la función TimeAdd (probablemente para realizar alguna acción adicional después de seleccionar la hora)
        }

        private fun TimeAdd() {
            // Implementation of TimeAdd method
            // Aquí puedes implementar lo que desees hacer después de seleccionar el tiempo
        }
    }

    private fun getCalendarioLibre(editText: EditText) {
        editText.setOnClickListener { showDatePickerDialogLibre(editText) }
    }
    private fun showDatePickerDialogLibre(editText: EditText) { // Función privada para mostrar un diálogo de selección de fecha
        // Crea una nueva instancia de DatePickerFragmentLibre y configura el listener para la selección de fecha
        val newFragmentLibre = DatePickerFragmnetLibre.newInstance { datePicker, year, month, day ->
            // Construye la fecha seleccionada en formato "dd/MM/yyyy"
            val selectedDate = "${DosDigitos(day)}/${DosDigitos(month + 1)}/$year"
            editText.setText(selectedDate) // Establece el texto del EditText con la fecha seleccionada
        }
        // Muestra el DatePickerFragmentLibre usando el FragmentManager
        newFragmentLibre.show(supportFragmentManager, "datePicker")
    }

    private fun DosDigitos(n: Int): String { // Función privada que toma un entero n y devuelve una cadena con dos dígitos
        return if (n <= 9) "0$n" // Si n es menor o igual a 9, antepone un "0" a n y lo convierte en una cadena
        else n.toString() // Si n es mayor de 9, convierte n a una cadena sin modificaciones
    }
    class DatePickerFragmnetLibre : DialogFragment() { // Clase que extiende DialogFragment para mostrar un diálogo de selección de fecha
        private var listener: OnDateSetListener? = null // Variable para almacenar el listener que manejará la selección de la fecha

        // Método para establecer el listener que manejará la fecha seleccionada
        fun setListener(listener: OnDateSetListener?) {
            this.listener = listener // Asigna el listener pasado como argumento a la variable miembro
        }

        // Método que crea y devuelve el diálogo de selección de fecha
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val c: Calendar = Calendar.getInstance() // Obtiene una instancia de Calendar con la fecha y hora actuales
            val anio: Int = c.get(Calendar.YEAR) // Obtiene el año actual
            val mes: Int = c.get(Calendar.MONTH) // Obtiene el mes actual (0-indexado)
            val dia: Int = c.get(Calendar.DAY_OF_MONTH) // Obtiene el día del mes actual

            // Crea un DatePickerDialog con el contexto de la actividad, el listener para la selección de fecha, y la fecha actual
            val cde = getActivity()?.let { DatePickerDialog(it, listener, dia, mes, anio) }
            cde?.datePicker?.init(anio, mes, dia, null) // Inicializa el DatePicker con la fecha actual
            return cde!! // Devuelve el DatePickerDialog
        }

        companion object {
            // Método estático para crear una nueva instancia de DatePickerFragmnetLibre
            fun newInstance(listener: OnDateSetListener?): DatePickerFragmnetLibre {
                val fragment = DatePickerFragmnetLibre() // Crea una nueva instancia de DatePickerFragmnetLibre
                fragment.setListener(listener) // Establece el listener en la instancia del fragmento
                return fragment // Devuelve la instancia del fragmento
            }
        }
    }


    fun cargar_list_pro() {
        val url = "https://transportetresdiamantes.com/config_hotel/listar_proceso.php" // URL del servicio web que proporciona la lista de procesos

        // Crear una solicitud JsonArrayRequest para obtener datos en formato JSON desde la URL
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null, // Método GET para la solicitud, sin parámetros adicionales
            Response.Listener { response -> // Listener para manejar la respuesta de la solicitud
                val dataList = ArrayList<Proceso>() // Crear una lista vacía para almacenar los objetos Proceso
                for (i in 0 until response.length()) { // Iterar sobre cada elemento del JSON Array
                    val jsonObject = response.getJSONObject(i) // Obtener el objeto JSON en la posición actual
                    val idP = jsonObject.getString("idP") // Extraer el valor del campo "idP" como String
                    val descripcionP = jsonObject.getString("descripcionP") // Extraer el valor del campo "descripcionP" como String
                    dataList.add(Proceso(idP, descripcionP)) // Crear un objeto Proceso y añadirlo a la lista
                }
                procesoList = dataList // Asignar la lista de procesos a la variable global procesoList

                // Configurar el adaptador del Spinner con los datos obtenidos
                val adapter = ProcesoAdapter(this, R.layout.spinner_item, dataList) // Crear un adaptador personalizado para el Spinner
                sp_pro.adapter = adapter // Asignar el adaptador al Spinner
            },
            Response.ErrorListener { error -> // Listener para manejar errores en la solicitud
                error.printStackTrace() // Imprimir el error en la consola para depuración
            }
        )

        requestQueue.add(jsonArrayRequest) // Añadir la solicitud a la cola de solicitudes de Volley

        // Configurar el listener para manejar la selección de ítems en el Spinner
        sp_pro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = procesoList[position] // Obtener el elemento seleccionado en la lista de procesos
                id_pro = selectedItem.idP // Asignar el ID del proceso seleccionado a la variable id_pro
                id_pro_descripcion = selectedItem.descripcionP // Asignar la descripción del proceso seleccionado a la variable id_pro_descripcion

                // Aquí agregas el Log.d para verificar el valor de id_pro
                Log.d("IDPseleccionado", "Valor de id_pro: $id_pro") // Imprimir el valor del ID del proceso seleccionado para depuración

                try {
                    // Aquí puedes agregar más lógica si es necesario
                } catch (e: Exception) {
                    Toast.makeText(this@Alojamiento, e.message, Toast.LENGTH_SHORT).show() // Mostrar un mensaje de error si ocurre una excepción
                    Log.e("erorrenjson", e.message.toString()) // Imprimir el error en la consola para depuración
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada si no se selecciona ningún ítem
            }
        }
    }


    fun cargar_list_th() {
        val url = "https://transportetresdiamantes.com/config_hotel/listar_combo_tipo_habitacion.php" // URL del servicio web para obtener la lista de tipos de habitación

        // Crear una solicitud JsonArrayRequest para obtener datos en formato JSON desde la URL
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null, // Método GET para la solicitud, sin parámetros adicionales
            Response.Listener { response -> // Listener para manejar la respuesta de la solicitud
                val dataList = ArrayList<TipoHabitacion>() // Crear una lista vacía para almacenar los objetos TipoHabitacion
                for (i in 0 until response.length()) { // Iterar sobre cada elemento del JSON Array
                    val jsonObject = response.getJSONObject(i) // Obtener el objeto JSON en la posición actual
                    val idTH = jsonObject.getString("idTH") // Extraer el valor del campo "idTH" como String
                    val descripcionTH = jsonObject.getString("descripcionTH") // Extraer el valor del campo "descripcionTH" como String
                    dataList.add(TipoHabitacion(idTH, descripcionTH)) // Crear un objeto TipoHabitacion y añadirlo a la lista
                }
                tipoHabitacionList = dataList // Asignar la lista de tipos de habitación a la variable global tipoHabitacionList

                // Configurar el adaptador del Spinner con los datos obtenidos
                val adapter = TipoHabitacionAdapter(this, R.layout.spinner_item, dataList) // Crear un adaptador personalizado para el Spinner
                sp_th.adapter = adapter // Asignar el adaptador al Spinner sp_th
            },
            Response.ErrorListener { error -> // Listener para manejar errores en la solicitud
                error.printStackTrace() // Imprimir el error en la consola para depuración
            }
        )

        requestQueue.add(jsonArrayRequest) // Añadir la solicitud a la cola de solicitudes de Volley

        // Configurar el listener para manejar la selección de ítems en el Spinner
        sp_th.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = tipoHabitacionList[position] // Obtener el elemento seleccionado en la lista de tipos de habitación
                id_th = selectedItem.idTH // Asignar el ID del tipo de habitación seleccionado a la variable id_th
                id_th_descripcion = selectedItem.descripcionTH // Asignar la descripción del tipo de habitación seleccionado a la variable id_th_descripcion
                try {
                    cargar_list_h(id_th) // Llamar a la función para cargar la lista de habitaciones según el tipo de habitación seleccionado
                } catch (e: Exception) {
                    Toast.makeText(this@Alojamiento, e.message, Toast.LENGTH_SHORT).show() // Mostrar un mensaje de error si ocurre una excepción
                    Log.e("erorrenjson", e.message.toString()) // Imprimir el error en la consola para depuración
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada si no se selecciona ningún ítem
            }
        }
    }

    fun cargar_list_h(idTH: String) {
        val url = "https://transportetresdiamantes.com/config_hotel/listar_combo_habitacion.php" // URL del servicio web para obtener la lista de habitaciones

        // Crear una solicitud JsonArrayRequest para obtener datos en formato JSON desde la URL
        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.POST, url, null, // Método POST para la solicitud, sin parámetros en el cuerpo
            Response.Listener { response -> // Listener para manejar la respuesta de la solicitud
                val dataList = ArrayList<Habitacion>() // Crear una lista vacía para almacenar los objetos Habitacion
                try {
                    // Manejar la respuesta como un JSONArray
                    for (i in 0 until response.length()) { // Iterar sobre cada elemento del JSON Array
                        val jsonObject = response.getJSONObject(i) // Obtener el objeto JSON en la posición actual
                        val idH = jsonObject.getString("idH") // Extraer el valor del campo "idH" como String
                        val descripcionH = jsonObject.getString("descripcionH") // Extraer el valor del campo "descripcionH" como String
                        dataList.add(Habitacion(idH, descripcionH)) // Crear un objeto Habitacion y añadirlo a la lista
                    }
                } catch (e: JSONException) { // Capturar errores al procesar el JSON
                    e.printStackTrace() // Imprimir el error en la consola para depuración
                    Toast.makeText(this@Alojamiento, "Error en la respuesta del servidor: ${e.message}", Toast.LENGTH_SHORT).show() // Mostrar un mensaje de error en la interfaz
                }
                habitacionList = dataList // Asignar la lista de habitaciones a la variable global habitacionList

                // Configurar el adaptador del Spinner con los datos obtenidos
                val adapter = HabitacionAdapter(this@Alojamiento, R.layout.spinner_item, dataList) // Crear un adaptador personalizado para el Spinner
                sp_h.adapter = adapter // Asignar el adaptador al Spinner sp_h
            },
            Response.ErrorListener { error -> // Listener para manejar errores en la solicitud
                error.printStackTrace() // Imprimir el error en la consola para depuración
                Toast.makeText(this@Alojamiento, "Error en la solicitud: ${error.message}", Toast.LENGTH_SHORT).show() // Mostrar un mensaje de error en la interfaz
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/x-www-form-urlencoded" // Establecer el tipo de contenido del cuerpo de la solicitud
                return headers
            }

            override fun getBody(): ByteArray {
                val params = HashMap<String, String>()
                params["idTH"] = idTH // Añadir el parámetro "idTH" al cuerpo de la solicitud

                // Convertir el mapa a un formato de URL codificado
                val encodedParams = params.map { (key, value) ->
                    "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
                }.joinToString("&")

                return encodedParams.toByteArray(Charsets.UTF_8) // Convertir los parámetros codificados en un ByteArray
            }
        }

        requestQueue.add(jsonArrayRequest) // Añadir la solicitud a la cola de solicitudes de Volley

        // Configurar el listener para manejar la selección de ítems en el Spinner
        sp_h.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = habitacionList[position] // Obtener el elemento seleccionado en la lista de habitaciones
                id_h = selectedItem.idH // Asignar el ID de la habitación seleccionada a la variable id_h
                id_h_descripcion = selectedItem.descripcionH // Asignar la descripción de la habitación seleccionada a la variable id_h_descripcion
                try {
                    cargar_precio_h(id_h) // Llamar a la función para cargar el precio de la habitación según el ID seleccionado
                } catch (e: Exception) {
                    Log.e("errorprecio", e.message.toString()) // Imprimir el error en la consola para depuración
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada si no se selecciona ningún ítem
            }
        }
    }

    fun cargar_precio_h(idH: String) {
        val url = "https://transportetresdiamantes.com/config_hotel/cargar_habitacion_Precio.php" // URL del servicio web para obtener el precio de la habitación

        // Crear una solicitud JsonArrayRequest para obtener datos en formato JSON desde la URL
        val jsonArrayRequest = object : JsonArrayRequest(
            Method.POST, url, null, // Método POST para la solicitud, sin parámetros en el cuerpo
            Response.Listener { response -> // Listener para manejar la respuesta de la solicitud
                val dataList = ArrayList<Habitacion_precio>() // Crear una lista vacía para almacenar los objetos Habitacion_precio

                try {
                    // Imprimir la respuesta del servidor para depuración
                    Log.d("CargarPrecioH", "Respuesta del servidor: $response")

                    // Manejar la respuesta como un JSONArray
                    for (i in 0 until response.length()) { // Iterar sobre cada elemento del JSON Array
                        val jsonObject = response.getJSONObject(i) // Obtener el objeto JSON en la posición actual
                        idHP = jsonObject.getString("idHP") // Extraer el valor del campo "idHP" como String
                        val precio = jsonObject.getString("precio") // Extraer el valor del campo "precio" como String
                        dataList.add(Habitacion_precio(idHP, precio)) // Crear un objeto Habitacion_precio y añadirlo a la lista
                    }
                    habitacion_PrecioList = dataList // Asignar la lista de precios de habitaciones a la variable global habitacion_PrecioList

                    // Mostrar el primer elemento si existe
                    if (habitacion_PrecioList.isNotEmpty()) {
                        val selectedItem = habitacion_PrecioList[0] // Obtener el primer elemento de la lista de precios
                        idHP = selectedItem.idHP // Asignar el ID de la habitación a la variable idHP
                        idHP_descripcion = selectedItem.precio // Asignar el precio a la variable idHP_descripcion
                        tP.setText(selectedItem.precio) // Mostrar el precio en un TextView tP

                    } else {
                        Toast.makeText(this@Alojamiento, "No hay datos disponibles", Toast.LENGTH_SHORT).show() // Mostrar un mensaje si no hay datos
                    }
                } catch (e: JSONException) { // Capturar errores al procesar el JSON
                    e.printStackTrace() // Imprimir el error en la consola para depuración
                    Toast.makeText(this@Alojamiento, "Error en el procesamiento de datos: ${e.message}", Toast.LENGTH_SHORT).show() // Mostrar un mensaje de error en la interfaz
                }
            },
            Response.ErrorListener { error -> // Listener para manejar errores en la solicitud
                error.printStackTrace() // Imprimir el error en la consola para depuración
                Toast.makeText(this@Alojamiento, "Error en la solicitud: ${error.message}", Toast.LENGTH_SHORT).show() // Mostrar un mensaje de error en la interfaz
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                return mapOf("Content-Type" to "application/x-www-form-urlencoded") // Establecer el tipo de contenido del cuerpo de la solicitud
            }

            override fun getBody(): ByteArray {
                val params = mapOf("idH" to idH) // Crear un mapa con el parámetro "idH"
                val encodedParams = params.map { (key, value) ->
                    "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
                }.joinToString("&") // Convertir el mapa a un formato de URL codificado

                Log.d("CargarPrecioH", "Parametros codificados: $encodedParams") // Imprimir los parámetros codificados para depuración
                return encodedParams.toByteArray(Charsets.UTF_8) // Convertir los parámetros codificados en un ByteArray
            }
        }

        requestQueue.add(jsonArrayRequest) // Añadir la solicitud a la cola de solicitudes de Volley
    }


    // MODELO
    data class Proceso(val idP: String, val descripcionP: String)
    data class TipoHabitacion(val idTH: String, val descripcionTH: String)
    data class Habitacion(val idH: String, val descripcionH: String)
    data class Habitacion_precio(val idHP: String, val precio: String)

    // SEG MODELO
// Define la clase ProcesoAdapter que extiende de ArrayAdapter<Proceso>
    class ProcesoAdapter(context: Context, private val resource: Int, private val items: List<Proceso>)
        : ArrayAdapter<Proceso>(context, resource, items) {

        // Override de la función getView que se utiliza para crear la vista del item en el spinner
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            // Usa la vista convertView si no es nula, de lo contrario infla una nueva vista desde el recurso especificado
            val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
            // Obtiene el item en la posición actual del spinner
            val item = getItem(position)
            // Encuentra el TextView en la vista inflada por su ID
            val textView = view.findViewById<TextView>(R.id.spinner_item_text)
            // Establece el texto del TextView con la descripción del item
            textView.text = item?.descripcionP
            // Retorna la vista modificada para el item del spinner
            return view
        }

        // Override de la función getDropDownView que se utiliza para crear la vista del dropdown del spinner
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            // Llama a getView para reutilizar el código de getView para el dropdown
            return getView(position, convertView, parent)
        }

        // Override de la función getItem que retorna el item en la posición especificada
        override fun getItem(position: Int): Proceso? {
            // Llama a la implementación de la clase base para obtener el item
            return super.getItem(position)
        }
    }

    // Define la clase TipoHabitacionAdapter que extiende de ArrayAdapter<TipoHabitacion>
    class TipoHabitacionAdapter(context: Context, private val resource: Int, private val items: List<TipoHabitacion>)
        : ArrayAdapter<TipoHabitacion>(context, resource, items) {

        // Override de la función getView que se utiliza para crear la vista del item en el spinner
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            // Usa la vista convertView si no es nula, de lo contrario infla una nueva vista desde el recurso especificado
            val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
            // Obtiene el item en la posición actual del spinner
            val item = getItem(position)
            // Encuentra el TextView en la vista inflada por su ID
            val textView = view.findViewById<TextView>(R.id.spinner_item_text)
            // Establece el texto del TextView con la descripción del item
            textView.text = item?.descripcionTH
            // Retorna la vista modificada para el item del spinner
            return view
        }

        // Override de la función getDropDownView que se utiliza para crear la vista del dropdown del spinner
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            // Llama a getView para reutilizar el código de getView para el dropdown
            return getView(position, convertView, parent)
        }

        // Override de la función getItem que retorna el item en la posición especificada
        override fun getItem(position: Int): TipoHabitacion? {
            // Llama a la implementación de la clase base para obtener el item
            return super.getItem(position)
        }
    }

    // Define la clase HabitacionAdapter que extiende de ArrayAdapter<Habitacion>
    class HabitacionAdapter(context: Context, private val resource: Int, private val items: List<Habitacion>)
        : ArrayAdapter<Habitacion>(context, resource, items) {

        // Override de la función getView que se utiliza para crear la vista del item en el spinner
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            // Usa la vista convertView si no es nula, de lo contrario infla una nueva vista desde el recurso especificado
            val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
            // Obtiene el item en la posición actual del spinner
            val item = getItem(position)
            // Encuentra el TextView en la vista inflada por su ID
            val textView = view.findViewById<TextView>(R.id.spinner_item_text)
            // Establece el texto del TextView con la descripción del item
            textView.text = item?.descripcionH
            // Retorna la vista modificada para el item del spinner
            return view
        }

        // Override de la función getDropDownView que se utiliza para crear la vista del dropdown del spinner
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            // Llama a getView para reutilizar el código de getView para el dropdown
            return getView(position, convertView, parent)
        }

        // Override de la función getItem que retorna el item en la posición especificada
        override fun getItem(position: Int): Habitacion? {
            // Llama a la implementación de la clase base para obtener el item
            return super.getItem(position)
        }
    }



    // Define la función insertarAlojamientoCab
    fun insertarAlojamientoCab() {
        // URL del script PHP que maneja la inserción de datos en la base de datos
        val url = "https://transportetresdiamantes.com/config_hotel/insertar_alojamiento.php"

        // Crea un objeto StringRequest para enviar una solicitud POST al servidor
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                // Maneja la respuesta del servidor si la solicitud es exitosa
                try {
                    // Imprime la respuesta en Logcat para depuración
                    Log.d("agregarAlojamiento", "Respuesta del servidor: $response")
                    // Muestra un mensaje Toast informando que el alojamiento se registró con éxito
                    Toast.makeText(this, "Alojamiento registrado con éxito", Toast.LENGTH_SHORT).show()
                    // Limpia los elementos de la interfaz de usuario (por ejemplo, campos de texto)
                    limpiarElementos()
                } catch (e: Exception) {
                    // Captura y maneja cualquier excepción que ocurra al procesar la respuesta
                    Log.e("agregarAlojamiento", "Error en la respuesta: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                // Maneja cualquier error que ocurra durante la solicitud
                error.printStackTrace()

                // Construye un mensaje de error detallado
                val errorMsg = error.networkResponse?.let {
                    // Extrae el código de estado HTTP y el cuerpo de la respuesta
                    val statusCode = it.statusCode
                    val responseBody = String(it.data)
                    // Construye un mensaje de error con la información obtenida
                    "Código de error: $statusCode, Respuesta: $responseBody"
                } ?: "Error en la solicitud: ${error.message}"

                // Imprime el mensaje de error en Logcat
                Log.e("esolicitud", errorMsg)

                // Muestra el mensaje de error en un Toast
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        ) {
            // Override del método getHeaders para especificar los encabezados HTTP de la solicitud
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                // Establece el tipo de contenido como "application/x-www-form-urlencoded"
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }

            // Override del método getParams para definir los parámetros que se enviarán en la solicitud POST
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                // Añade los parámetros necesarios para la solicitud POST
                params["idTH"] = id_th
                params["idH"] = id_h
                params["idHP"] = idHP
                params["idP"] = id_pro
                params["fechaInicio"] = etFI.text.toString()
                params["horaInicio"] = etHI.text.toString()
                params["fechaFin"] = etFF.text.toString()
                params["horaFin"] = etHF.text.toString()
                // Convierte la lista tridimensional en JSON usando Gson
                val gson = Gson()
                val json = gson.toJson(listaTridimensional)
                params["det_list"] = json

                // Imprime los parámetros en Logcat para depuración
                Log.d("insertarAlojamientoCab", "Params: $params")
                return params
            }
        }

        // Añade la solicitud a la cola de solicitudes para ser ejecutada
        requestQueue.add(stringRequest)
    }



    // Define la función obtenerTodosLosAlojamientos para obtener la lista de alojamientos desde el servidor
    fun obtenerTodosLosAlojamientos() {
        // URL del script PHP que maneja la solicitud de listar los alojamientos
        val url = "https://transportetresdiamantes.com/config_hotel/listar_alojamiento_cab.php"

        // Crea un objeto StringRequest para realizar una solicitud GET al servidor
        val stringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                // Maneja la respuesta del servidor si la solicitud es exitosa
                try {
                    // Convierte la respuesta JSON en un JSONArray
                    val jsonArray = JSONArray(response)
                    // Limpia la lista de alojamientos actual
                    listaAlojamientos.clear()
                    // Itera sobre el JSONArray para procesar cada objeto JSON
                    for (i in 0 until jsonArray.length()) {
                        // Obtiene el objeto JSON en la posición actual
                        val jsonObject = jsonArray.getJSONObject(i)
                        // Extrae los datos del objeto JSON
                        val idAC = jsonObject.getString("idAC")
                        val descripcionTH = jsonObject.getString("descripcionTH")
                        val descripcionH = jsonObject.getString("descripcionH")
                        val descripcionP = jsonObject.getString("descripcionP")
                        val precio = jsonObject.getDouble("precio")
                        val fechaInicio = jsonObject.getString("fechaInicio")
                        val fechaFin = jsonObject.getString("fechaFin")
                        val horaInicio = jsonObject.getString("horaInicio")
                        val horaFin = jsonObject.getString("horaFin")

                        // Crea un nuevo objeto Model_Alojamiento con los datos obtenidos
                        val nuevoAlojamiento = Model_Alojamiento(
                            countID = i, // Usa el índice del bucle como countID si no tienes otro valor
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
                        // Añade el nuevo objeto a la lista de alojamientos
                        listaAlojamientos.add(nuevoAlojamiento)
                    }
                    // Notifica al adaptador que los datos han cambiado y que debe actualizarse
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    // Captura y maneja cualquier excepción que ocurra al procesar la respuesta
                    Log.e("obtenerAlojamientos", "Error en la respuesta: ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                // Maneja cualquier error que ocurra durante la solicitud
                error.printStackTrace()
                // Muestra un mensaje Toast con el error
                Toast.makeText(this, "Error en la solicitud: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            // Override del método getHeaders para especificar los encabezados HTTP de la solicitud
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                // Establece el tipo de contenido como "application/x-www-form-urlencoded"
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }
        }

        // Añade la solicitud a la cola de solicitudes para ser ejecutada
        requestQueue.add(stringRequest)
    }


    fun setIdAC(idAC: Int) {
        // Asigna el valor del parámetro idAC a la variable currentIdAC
        currentIdAC = idAC
    }

    // Define una función para editar la información de un alojamiento
    fun editarAlojamientoCab(
        idAC: Int,
        fechaInicio: String,
        fechaFin: String,
        horaInicio: String,
        horaFin: String,
        idTH: Int,
        idH: Int,
        idP: Int,
        idHP: Int
    ) {
        // URL del script PHP que maneja la edición del alojamiento
        val url = "https://transportetresdiamantes.com/config_hotel/editar_alojamiento.php"

        // Crea un objeto StringRequest para realizar una solicitud POST al servidor
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                // Manejo de la respuesta del servidor
                Log.d("editarAlojamientoCab", "Response: $response")
                // Verifica si la respuesta contiene el mensaje de éxito esperado
                if (response.contains("Editado correctamente")) {
                    // Muestra un mensaje Toast indicando que la edición fue exitosa
                    Toast.makeText(this, "Alojamiento editado correctamente", Toast.LENGTH_SHORT).show()
                    // Imprime en Logcat el ID del proceso
                    Log.e("IDP", "ID del Proceso: $idP")
                    // Limpia los elementos de la interfaz (posiblemente campos de entrada)
                    limpiarElementos()
                } else {
                    // Muestra un mensaje Toast indicando que hubo un error al editar el alojamiento
                    Toast.makeText(this, "Error al editar alojamiento", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                // Manejo del error en caso de fallo de la solicitud
                Log.e("editarAlojamientoCab", "Error: ${error.message}")
                // Muestra un mensaje Toast con el error de red
                Toast.makeText(this, "Error de red: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            // Override del método getParams para especificar los parámetros de la solicitud
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                // Añade los parámetros a la solicitud POST
                params["idAC"] = idAC.toString()
                params["fechaInicio"] = fechaInicio
                params["fechaFin"] = fechaFin
                params["horaInicio"] = horaInicio
                params["horaFin"] = horaFin
                params["idTH"] = idTH.toString()
                params["idH"] = idH.toString()
                params["idP"] = idP.toString()
                params["idHP"] = idHP.toString()
                return params
            }
        }

        // Crea una nueva cola de solicitudes de Volley y añade la solicitud a la cola
        Volley.newRequestQueue(this).add(stringRequest)
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
        // Verifica si el campo de texto para DNI está vacío
        when {
            edt_dni_app.text.toString().isEmpty() -> {
                // Muestra un Toast si el campo de DNI está vacío
                Toast.makeText(this, "Enter Dni", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // Muestra un diálogo de progreso mientras se realiza la solicitud
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Por favor espera...")
                progressDialog.show()

                // Obtiene el DNI ingresado y lo limpia de espacios
                str_dni = edt_dni_app.text.toString().trim()

                // Crea una solicitud POST con Volley
                val request = object : StringRequest(
                    Request.Method.POST, url,
                    Response.Listener<String> { response ->
                        // Oculta el diálogo de progreso cuando se recibe la respuesta
                        progressDialog.dismiss()

                        // Registra la respuesta del servidor en Logcat
                        Log.d("ServerResponse", response)
                        // Parsea la respuesta JSON para obtener los campos deseados
                        val apiResponse = parseJsonResponse(response)
                        // Actualiza las vistas con los datos obtenidos
                        tv_nombre_app.setText(apiResponse.data.nombres)
                        tv_apellido_app.setText(apiResponse.data.apellido_paterno + " " + apiResponse.data.apellido_materno)
                        // Si el campo de DNI antiguo está vacío, actualízalo con el DNI ingresado
                        if (tv_dni_antig.text.toString() == ""){
                            tv_dni_antig.setText(edt_dni_app.text.toString())
                        }
                        // Intenta agregar los datos a un ArrayList
                        try{
                            agregarAlArray(tv_nombre_app, tv_apellido_app, edt_dni_app, tv_dni_antig)
                        }catch (e: Exception){
                            // Registra cualquier error que ocurra al agregar datos al ArrayList
                            Log.e("error_Array", e.message.toString())
                        }

                    },
                    Response.ErrorListener { error ->
                        // Oculta el diálogo de progreso en caso de error
                        progressDialog.dismiss()
                        // Registra el error en Logcat
                        Log.e("VolleyError", error.toString())
                        // Puedes descomentar esta línea para mostrar un mensaje de error
                        // Toast.makeText(this@Logeo, error.message.toString(), Toast.LENGTH_SHORT).show()
                    }) {
                    @Throws(AuthFailureError::class)
                    // Especifica los parámetros que se enviarán en la solicitud POST
                    override fun getParams(): Map<String, String> {
                        return hashMapOf(
                            // Envía el parámetro DNI con la clave "usuario", que es el nombre del campo en el web service PHP
                            "usuario" to str_dni
                        )
                    }
                }

                // Crea una cola de solicitudes de Volley y añade la solicitud a la cola
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
        val success: Boolean,       // Indica si la solicitud fue exitosa (true) o fallida (false).
        val data: UserData,         // Contiene los datos del usuario, representados por la clase UserData.
        val time: Double,           // Tiempo en que se realizó la solicitud, en segundos o milisegundos.
        val source: String          // Fuente u origen de la información, como el nombre del servicio API.
    )
    /*  CREAMOS NUESTRA CLASE UserData (MODELO) PARA QUE LOS DATOS DEL JSON SEAN TOMANDOS EN UN LIST TIPO CLASE Y COMO SE PUEDE VER
        SE TIENE LOS CAMPOS, NOMBRES, AP PATERNO, AP MATERNO */
    data class UserData(
        val numero: String,                  // Número de identificación del usuario, como un DNI o ID.
        val nombre_completo: String,         // Nombre completo del usuario.
        val nombres: String,                 // Nombres del usuario, puede ser solo el primer nombre o todos los nombres.
        val apellido_paterno: String,        // Apellido paterno del usuario.
        val apellido_materno: String,        // Apellido materno del usuario.
        val codigo_verificacion: Int,         // Código de verificación, probablemente para validaciones adicionales.
        val ubigeo_sunat: String,             // Código de ubicación relacionado con SUNAT, administración tributaria en Perú.
        val ubigeo: List<Any?>,               // Lista de datos de ubicación. Se usa Any? para manejar posibles tipos de datos diversos o nulos.
        val direccion: String                // Dirección del usuario.
    )


    // END

    data class DetInfo(
        var dni: String,
        var nombre: String,
        var apellido: String
    )



    // Función para agregar o reemplazar datos en una lista tridimensional basada en el DNI
    fun agregarAlArray(
        textViewName: TextView,           // TextView que contiene el nombre
        textViewApellido: TextView,       // TextView que contiene el apellido
        edtDni: EditText,                 // EditText que contiene el DNI nuevo
        tvDniAnterior: TextView?          // TextView que contiene el DNI anterior (puede ser nulo)
    ) {
        // Obtiene el DNI anterior y el nuevo DNI desde las vistas
        val dniAnterior = tvDniAnterior?.text.toString()
        val dniNuevo = edtDni.text.toString()

        // Imprime en log el DNI anterior y el nuevo DNI para depuración
        Log.e("datoingreso", "$dniAnterior $dniNuevo")

        var encontrado = false  // Bandera para verificar si el DNI anterior se encuentra en la lista

        // Verifica si el DNI anterior no está vacío y es diferente del DNI nuevo
        if (dniAnterior.isNotEmpty() && dniAnterior != dniNuevo) {
            // Crea una nueva instancia de DetInfo con los datos actualizados
            val nuevaInfo = DetInfo(dniNuevo, textViewName.text.toString(), textViewApellido.text.toString())

            // Recorre la lista tridimensional para encontrar y reemplazar el dato con el DNI anterior
            for (lista2D in listaTridimensional) {
                for (lista1D in lista2D) {
                    for (i in lista1D.indices) {
                        if (lista1D[i].dni == dniAnterior) {
                            // Reemplaza el dato en la lista1D
                            lista1D[i] = nuevaInfo
                            encontrado = true
                        }
                    }
                }
            }

            // Si no se encontró el DNI anterior para reemplazar, se imprime un mensaje de error
            if (!encontrado) {
                Log.e("error_reemplazo", "No se encontró ningún dato con DNI $dniAnterior para reemplazar.")
            }
        } else {
            // Si el DNI anterior está vacío o es igual al nuevo DNI, se añade la nueva información
            try {
                // Crea una nueva instancia de DetInfo con los datos
                val nuevaInfo = DetInfo(dniNuevo, textViewName.text.toString(), textViewApellido.text.toString())

                // Crea una nueva lista 1D y añade la nueva información
                val lista1D = ArrayList<DetInfo>()
                lista1D.add(nuevaInfo)

                // Crea una nueva lista 2D y añade la lista 1D
                val lista2D = ArrayList<ArrayList<DetInfo>>()
                lista2D.add(lista1D)

                // Añade la lista 2D a la lista tridimensional
                listaTridimensional.add(lista2D)

                // Imprime en log el tamaño actual de la lista tridimensional para depuración
                Log.e("lista_tridimensional", listaTridimensional.size.toString())
            } catch (e: Exception) {
                // Captura y muestra cualquier excepción que ocurra durante el proceso de adición
                Log.e("error_array_2", e.message.toString())
            }
        }

        // Muestra el contenido de la lista tridimensional en el log para depuración
        try {
            for (lista2D in listaTridimensional) {
                for (lista1D in lista2D) {
                    for (info in lista1D) {
                        // Accede y muestra los datos de cada instancia de DetInfo
                        Log.e("dato_array", "Nombre: ${info.nombre}, Apellido: ${info.apellido}, DNI: ${info.dni}")
                    }
                }
            }
        } catch (ex: Exception) {
            // Captura y muestra cualquier excepción que ocurra durante la visualización de datos
            Log.e("error_array", ex.message.toString())
        }
    }




}


