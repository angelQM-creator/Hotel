package com.example.proyectofinal

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            //R.id.about -> Toast.makeText(this,"Bienveidos al APP",Toast.LENGTH_SHORT).show()

            R.id.alojamiento -> alojamineto()
            R.id.reservacion -> reservacion()
        }
        return super.onOptionsItemSelected(item)
    }


    fun alojamineto() {
        val intento1 = Intent(this, Presentacion::class.java)
        startActivity(intento1)
    }

    fun reservacion() {
        val intento1 = Intent(this, Presentacion::class.java)
        startActivity(intento1)
    }
}