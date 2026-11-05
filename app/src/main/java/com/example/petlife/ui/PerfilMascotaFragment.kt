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

        // 1. Atrapa el ID que envía la tarjeta desde la pantalla principal
        val mascotaId = arguments?.getInt("mascotaId") ?: return view

        // 2. Conecta las variables con los textos del diseño XML
        val tvNombre = view.findViewById<TextView>(R.id.tvPerfilNombre)
        val tvDetalles = view.findViewById<TextView>(R.id.tvPerfilDetalles)

        // 3. Ejecuta una consulta a Room en segundo plano (Corrutina)
        lifecycleScope.launch {
            val db = PetLifeDatabase.getDatabase(requireContext())
            val mascota = db.mascotaDao().obtenerMascotaPorId(mascotaId)

            // 4. Si encuentra la mascota, llena el encabezado con sus datos reales
            if (mascota != null) {
                tvNombre.text = mascota.nombre
                tvDetalles.text = "${mascota.especie} - ${mascota.raza} | ${mascota.edad} años | ${mascota.pesoActual} kg"
            }
        }

        return view
    }
}