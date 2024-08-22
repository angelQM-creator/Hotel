package com.example.proyectofinal

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log  // Importa el Log estándar de Android para registro de mensajes
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

// Define la clase Logeo que extiende AppCompatActivity
class Logeo : AppCompatActivity() {

    // Declara variables para los campos de texto y otras variables
    lateinit var usuario: EditText
    lateinit var password: EditText
    var str_user: String = ""
    var str_password: String = ""
    var url = "https://transportetresdiamantes.com/config_hotel/logear.php"  // URL del script de inicio de sesión

    // Se llama al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Habilita el diseño de pantalla completa sin recortes en los bordes
        setContentView(R.layout.activity_logeo)  // Establece el diseño de la actividad

        // Ajusta el relleno de la vista principal para tener en cuenta las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configura el VideoView para reproducir un video en el fondo
        val videoView: VideoView = findViewById(R.id.videoBackground)
        val videoUri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.loginv)
        videoView.setVideoURI(videoUri)  // Establece la URI del video
        videoView.start()  // Comienza la reproducción del video

        // Configura el VideoView para que el video se repita en bucle
        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true
        }

        // Inicializa los campos de texto
        usuario = findViewById(R.id.etusuario)
        password = findViewById(R.id.etcontraseña)
    }

    // Función que maneja el inicio de sesión cuando se hace clic en el botón
    fun Login(view: View) {
        when {
            // Verifica si el campo de usuario está vacío y muestra un mensaje si es necesario
            usuario.text.toString().isEmpty() -> {
                Toast.makeText(this, "Enter User", Toast.LENGTH_SHORT).show()
            }
            // Verifica si el campo de contraseña está vacío y muestra un mensaje si es necesario
            password.text.toString().isEmpty() -> {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // Muestra un diálogo de progreso mientras se realiza la solicitud
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Por favor espera...")
                progressDialog.show()

                // Obtiene los valores de usuario y contraseña de los campos de texto
                str_user = usuario.text.toString()
                str_password = password.text.toString()

                // Crea una solicitud POST con Volley
                val request = object : StringRequest(
                    Request.Method.POST, url,
                    // Maneja la respuesta exitosa del servidor
                    Response.Listener<String> { response ->
                        progressDialog.dismiss()  // Oculta el diálogo de progreso

                        // Registra la respuesta para depuración
                        Log.d("ServerResponse", response)

                        // Verifica si la respuesta comienza con "Bienvenido"
                        if (response.startsWith("Bienvenido", ignoreCase = true)) {
                            // Limpia los campos de texto
                            usuario.setText("")
                            password.setText("")
                            // Inicia la actividad Oferta
                            startActivity(Intent(applicationContext, Oferta::class.java))
                            // Muestra un mensaje de bienvenida
                            Toast.makeText(this@Logeo, response, Toast.LENGTH_SHORT).show()
                        } else {
                            // Muestra la respuesta del servidor como un mensaje de error
                            Toast.makeText(this@Logeo, response, Toast.LENGTH_SHORT).show()
                        }
                    },
                    // Maneja los errores de la solicitud
                    Response.ErrorListener { error ->
                        progressDialog.dismiss()  // Oculta el diálogo de progreso
                        // Registra el error para depuración
                        Log.e("VolleyError", error.toString())
                        // Muestra un mensaje de error de red
                        Toast.makeText(this@Logeo, error.message.toString(), Toast.LENGTH_SHORT).show()
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        // Devuelve los parámetros de la solicitud POST
                        return hashMapOf(
                            "usuario" to str_user,
                            "password" to str_password
                        )
                    }
                }

                // Crea una cola de solicitudes Volley y añade la solicitud StringRequest
                val requestQueue: RequestQueue = Volley.newRequestQueue(this)
                requestQueue.add(request)
            }
        }
    }

    // Función que maneja el cambio a la actividad de registro
    fun moveToRegistration(view: View) {
        // Inicia la actividad Registrar y finaliza la actividad actual
        startActivity(Intent(applicationContext, Registrar::class.java))
        finish()
    }
}
