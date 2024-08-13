package com.example.proyectofinal

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log  // Importa el Log estándar de Android
import android.view.View
import android.widget.EditText
import android.widget.Toast
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

class Logeo : AppCompatActivity() {

    lateinit var usuario: EditText
    lateinit var password: EditText
    var str_user: String = ""
    var str_password: String = ""
    var url = "https://transportetresdiamantes.com/config_hotel/logear.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_logeo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuario = findViewById(R.id.etusuario)
        password = findViewById(R.id.etcontraseña)
    }

    fun Login(view: View) {
        when {
            usuario.text.toString().isEmpty() -> {
                Toast.makeText(this, "Enter User", Toast.LENGTH_SHORT).show()
            }
            password.text.toString().isEmpty() -> {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Por favor espera...")
                progressDialog.show()

                str_user = usuario.text.toString().trim()
                str_password = password.text.toString().trim()

                val request = object : StringRequest(
                    Request.Method.POST, url,
                    Response.Listener<String> { response ->
                        progressDialog.dismiss()

                        // Usa android.util.Log
                        Log.d("ServerResponse", response)

                        if (response.trim().equals("ingreso", ignoreCase = true)) {
                            usuario.setText("")
                            password.setText("")
                            startActivity(Intent(applicationContext, Oferta::class.java))
                            Toast.makeText(this@Logeo, response, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@Logeo, response, Toast.LENGTH_SHORT).show()
                        }
                    },
                    Response.ErrorListener { error ->
                        progressDialog.dismiss()
                        Log.e("VolleyError", error.toString())
                        Toast.makeText(this@Logeo, error.message.toString(), Toast.LENGTH_SHORT).show()
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        return hashMapOf(
                            "usuario" to str_user,
                            "password" to str_password
                        )
                    }
                }

                val requestQueue: RequestQueue = Volley.newRequestQueue(this)
                requestQueue.add(request)
            }
        }
    }

    fun moveToRegistration(view: View) {
        startActivity(Intent(applicationContext, Registrar::class.java))
        finish()
    }
}
