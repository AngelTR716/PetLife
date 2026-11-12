package com.example.petlife.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.petlife.R
import com.example.petlife.data.PetLifeDatabase
import kotlinx.coroutines.launch

class PerfilMascotaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil_mascota, container, false)

        val mascotaId = arguments?.getInt("mascotaId") ?: return view

        // Conectar las nuevas variables del Carnet
        val tvId = view.findViewById<TextView>(R.id.tvCarnetId)
        val tvNombre = view.findViewById<TextView>(R.id.tvCarnetNombre)
        val tvEspecie = view.findViewById<TextView>(R.id.tvCarnetEspecie)
        val tvRaza = view.findViewById<TextView>(R.id.tvCarnetRaza)
        val tvEdad = view.findViewById<TextView>(R.id.tvCarnetEdad)
        val tvPeso = view.findViewById<TextView>(R.id.tvCarnetPeso)

        lifecycleScope.launch {
            val db = PetLifeDatabase.getDatabase(requireContext())
            val mascota = db.mascotaDao().obtenerMascotaPorId(mascotaId)

            if (mascota != null) {
                tvId.text = "ID. ${mascota.id.toString().padStart(4, '0')}"
                tvNombre.text = mascota.nombre
                tvEspecie.text = mascota.especie.uppercase()
                tvRaza.text = mascota.raza.uppercase()
                tvEdad.text = "${mascota.edad} años"
                tvPeso.text = "${mascota.pesoActual} kg"
            }
        }

        return view
    }
}