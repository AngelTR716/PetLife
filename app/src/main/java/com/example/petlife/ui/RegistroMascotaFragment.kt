package com.example.petlife.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.petlife.R
import com.example.petlife.data.Mascota
import com.example.petlife.data.PetLifeDatabase
import kotlinx.coroutines.launch

class RegistroMascotaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registro_mascota, container, false)

        val etNombre = view.findViewById<EditText>(R.id.etNombre)
        val etEspecie = view.findViewById<EditText>(R.id.etEspecie)
        val etRaza = view.findViewById<EditText>(R.id.etRaza)
        val etEdad = view.findViewById<EditText>(R.id.etEdad)
        val etPeso = view.findViewById<EditText>(R.id.etPeso)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val especie = etEspecie.text.toString()
            val raza = etRaza.text.toString()
            val edad = etEdad.text.toString().toIntOrNull() ?: 0
            val peso = etPeso.text.toString().toDoubleOrNull() ?: 0.0

            if (nombre.isNotEmpty()) {
                val nuevaMascota = Mascota(nombre = nombre, especie = especie, raza = raza, edad = edad, pesoActual = peso)
                guardarMascota(nuevaMascota)
            } else {
                Toast.makeText(requireContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun guardarMascota(mascota: Mascota) {
        lifecycleScope.launch {
            val db = PetLifeDatabase.getDatabase(requireContext())
            db.mascotaDao().insertarMascota(mascota)

            Toast.makeText(requireContext(), "Mascota guardada", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }
}
