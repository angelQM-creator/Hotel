package com.example.proyectofinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Presentacion : AppCompatActivity() {

    lateinit var btnCerrar: Button
    lateinit var btnContinuar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_presentacion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        btnCerrar = findViewById(R.id.btnCerrar)
        btnContinuar = findViewById(R.id.btnContinuar)

        btnContinuar.setOnClickListener{
            val intento1 = Intent(this, QuienesSomos::class.java)
            startActivity(intento1)
        }

        btnCerrar.setOnClickListener {
            cerrar()
        }

    }

    fun cerrar(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setMessage("Fin de la APP!!")
            .setTitle("CERRAR APP")
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                Toast.makeText(applicationContext, "Cerrando la aplicación", Toast.LENGTH_SHORT).show()
                System.exit(0) // Asegura que la aplicación se cierra completamente
            }
            .setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(applicationContext, "La aplicación continuará", Toast.LENGTH_SHORT).show()
                dialog.dismiss() // Cierra el diálogo sin cerrar la app
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }




}