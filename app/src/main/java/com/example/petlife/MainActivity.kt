package com.example.petlife

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Solo necesitamos que infle el diseño que creamos
        setContentView(R.layout.activity_main)
    }
}