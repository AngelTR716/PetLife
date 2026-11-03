package com.example.petlife.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petlife.R
import com.example.petlife.data.PetLifeDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController


class ListaMascotasFragment : Fragment() {

    private lateinit var adaptador: MascotaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Conecta este código con el diseño fragment_lista_mascotas.xml
        val view = inflater.inflate(R.layout.fragment_lista_mascotas, container, false)

        // 1. Configura el RecyclerView (lista de arriba hacia abajo)
        val rvMascotas = view.findViewById<RecyclerView>(R.id.rvMascotas)
        rvMascotas.layoutManager = LinearLayoutManager(requireContext())

        // 2. Inicializa el adaptador con una lista vacía por el momento
        adaptador = MascotaAdapter(emptyList())
        rvMascotas.adapter = adaptador

        // 3. Configura el botón flotante
        val fabAgregar = view.findViewById<FloatingActionButton>(R.id.fabAgregarMascota)
        fabAgregar.setOnClickListener {
            // Activa la "flecha" del nav_graph para viajar a la pantalla de registro
            findNavController().navigate(R.id.action_listaMascotasFragment_to_registroMascotaFragment)
        }

        // 4. Extrae los datos de Room
        cargarMascotas()

        return view
    }

    // Método para consultar la base de datos usando Corrutinas en segundo plano
    private fun cargarMascotas() {
        lifecycleScope.launch {
            val db = PetLifeDatabase.getDatabase(requireContext())
            // Obtiene la lista desde el DAO
            val lista = db.mascotaDao().obtenerTodasLasMascotas()
            // Envía la lista al adaptador para que actualice la pantalla
            adaptador.actualizarDatos(lista)
        }
    }
}