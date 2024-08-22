package com.example.proyectofinal

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class Registrar : AppCompatActivity() {

    lateinit var txtUsuario: EditText // Declara una variable para el campo de texto del usuario
    lateinit var pass: EditText // Declara una variable para el campo de texto de la contraseña
    lateinit var btn_insert: Button // Declara una variable para el botón de registro

    override fun onCreate(savedInstanceState: Bundle?) { // Método llamado cuando se crea la actividad
        super.onCreate(savedInstanceState) // Llama al método onCreate de la superclase
        enableEdgeToEdge() // Habilita el diseño a pantalla completa
        setContentView(R.layout.activity_registrar) // Establece el archivo de diseño para esta actividad
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets -> // Configura el manejo de los márgenes del sistema
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) // Obtiene los márgenes de las barras del sistema
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom) // Ajusta el relleno de la vista
            insets // Devuelve los insets para seguir manejándolos
        }

        txtUsuario = findViewById(R.id.etUserRegister) // Inicializa txtUsuario con el campo de texto del usuario
        pass = findViewById(R.id.etcontraseña) // Inicializa pass con el campo de texto de la contraseña
        btn_insert = findViewById(R.id.btn_register) // Inicializa btn_insert con el botón de registro

        btn_insert.setOnClickListener { // Configura el evento de clic para el botón de registro
            insertData() // Llama al método insertData cuando se hace clic en el botón
        }
    }

    fun insertData() { // Método para insertar datos cuando se hace clic en el botón de registro
        val usuario = txtUsuario.text.toString().trim() // Obtiene el texto del campo de usuario y elimina espacios en blanco
        val password = pass.text.toString().trim() // Obtiene el texto del campo de contraseña y elimina espacios en blanco

        val progressDialog = ProgressDialog(this) // Crea un nuevo ProgressDialog
        progressDialog.setMessage("cargando...") // Establece el mensaje que se mostrará en el ProgressDialog

        if (usuario.isEmpty()) { // Verifica si el campo de usuario está vacío
            txtUsuario.error = "complete los campos" // Muestra un mensaje de error en el campo de usuario
            return // Sale del método si el campo está vacío
        } else { // Si el campo de usuario no está vacío
            progressDialog.show() // Muestra el ProgressDialog
            val request = object : StringRequest(Method.POST, "https://transportetresdiamantes.com/config_hotel/insertar.php", // Crea una solicitud POST para enviar datos al servidor
                Response.Listener<String> { response -> // Maneja la respuesta de la solicitud
                    if (response.equals("Usuario Agregado", ignoreCase = true)) { // Verifica si la respuesta del servidor es "Usuario Agregado"
                        Toast.makeText(this@Registrar, "Usuario Agregado", Toast.LENGTH_SHORT).show() // Muestra un mensaje de éxito
                        progressDialog.dismiss() // Oculta el ProgressDialog
                        val intent = Intent(this@Registrar, Logeo::class.java) // Crea un nuevo Intent para iniciar la actividad Logeo
                        startActivity(intent) // Inicia la actividad Logeo
                    } else { // Si la respuesta no es "Usuario Agregado"
                        Toast.makeText(this@Registrar, response, Toast.LENGTH_SHORT).show() // Muestra la respuesta del servidor como mensaje
                        progressDialog.dismiss() // Oculta el ProgressDialog
                        Toast.makeText(this@Registrar, "El usuario ya existe", Toast.LENGTH_SHORT).show() // Muestra un mensaje de error si el usuario ya existe
                    }
                },
                Response.ErrorListener { error -> // Maneja los errores de la solicitud
                    Toast.makeText(this@Registrar, error.message, Toast.LENGTH_SHORT).show() // Muestra el mensaje de error
                    progressDialog.dismiss() // Oculta el ProgressDialog
                }) {
                override fun getParams(): Map<String, String> { // Define los parámetros que se enviarán en la solicitud POST
                    val params = HashMap<String, String>() // Crea un HashMap para los parámetros
                    params["usuario"] = usuario // Añade el usuario a los parámetros
                    params["password"] = password // Añade la contraseña a los parámetros
                    return params // Devuelve el mapa de parámetros
                }
            }

            val requestQueue = Volley.newRequestQueue(this@Registrar) // Crea una nueva cola de solicitudes con Volley
            requestQueue.add(request) // Añade la solicitud a la cola de solicitudes
        }
    }

    override fun onBackPressed() { // Método llamado cuando se presiona el botón de retroceso
        super.onBackPressed() // Llama al método onBackPressed de la superclase
        finish() // Cierra la actividad actual
    }

    fun login(v: View) { // Método llamado cuando se hace clic en el TextView de login
        startActivity(Intent(applicationContext, Logeo::class.java)) // Inicia la actividad Logeo
        finish() // Cierra la actividad actual
    }



}