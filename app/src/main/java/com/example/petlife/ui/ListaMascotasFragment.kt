package com.example.petlife.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petlife.R
import com.example.petlife.data.PetLifeDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ListaMascotasFragment : Fragment() {

    private lateinit var adapter: MascotaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lista_mascotas, container, false)

        val rvMascotas = view.findViewById<RecyclerView>(R.id.rvMascotas)
        val fabAgregarMascota = view.findViewById<FloatingActionButton>(R.id.fabAgregarMascota)

        adapter = MascotaAdapter(emptyList())
        rvMascotas.layoutManager = LinearLayoutManager(requireContext())
        rvMascotas.adapter = adapter

        fabAgregarMascota.setOnClickListener {
            findNavController().navigate(R.id.action_listaMascotasFragment_to_registroMascotaFragment)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        cargarMascotas()
    }

    private fun cargarMascotas() {
        lifecycleScope.launch {
            val db = PetLifeDatabase.getDatabase(requireContext())
            val lista = db.mascotaDao().obtenerTodasLasMascotas()
            adapter.actualizarDatos(lista)
        }
    }
}
