package com.example.proyectofinal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.VideoView
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Oferta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_oferta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val videoView: VideoView = findViewById(R.id.videoBackground)
        val videoUri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.fndofv)
        videoView.setVideoURI(videoUri)
        videoView.start()

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = true// Para que el video se repita en bucle
        }

        val toolbar: Toolbar = findViewById(R.id.toolbarO)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24_white) // Usa el drawable con el color cambiado
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu_second, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.alojamiento -> alojamiento()
            R.id.reservacion -> reservacion()
        }
        return super.onOptionsItemSelected(item)
    }

    fun alojamiento() {
        val intento1 = Intent(this, Alojamiento::class.java)
        startActivity(intento1)
    }

    fun reservacion() {
        val intento1 = Intent(this, Reservacion::class.java)
        startActivity(intento1)
    }

}