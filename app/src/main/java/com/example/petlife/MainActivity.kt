package com.example.petlife

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Esta línea fuerza la barra de notificaciones a negro sólido
        window.statusBarColor = Color.BLACK

        setContentView(R.layout.activity_main)
    }
}