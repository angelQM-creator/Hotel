package com.example.proyectofinal

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class Registrar : AppCompatActivity() {

    lateinit var txtUsuario: EditText
    lateinit var pass: EditText
    lateinit var btn_insert: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtUsuario = findViewById(R.id.etUserRegister)
        pass = findViewById(R.id.etcontraseña);
        btn_insert = findViewById(R.id.btn_register);

        btn_insert.setOnClickListener {
            insertData()
        }

    }

    fun insertData() {
        val usuario = txtUsuario.text.toString().trim()
        val password = pass.text.toString().trim()

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("cargando...")

        if (usuario.isEmpty()) {
            txtUsuario.error = "complete los campos"
            return
        }else {
            progressDialog.show()
            val request = object : StringRequest(Method.POST, "https://transportetresdiamantes.com/config_hotel/insertar.php",
                Response.Listener<String> { response ->
                    if (response.equals("Usuario Agregado", ignoreCase = true)) {
                        Toast.makeText(this@Registrar, "Usuario Agregado", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                        val intent = Intent(this@Registrar, Logeo::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@Registrar, response, Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                        Toast.makeText(this@Registrar, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this@Registrar, error.message, Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["usuario"] = usuario
                    params["password"] = password
                    return params
                }
            }

            val requestQueue = Volley.newRequestQueue(this@Registrar)
            requestQueue.add(request)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun login(v: View) {
        startActivity(Intent(applicationContext, Logeo::class.java))
        finish()
    }



}