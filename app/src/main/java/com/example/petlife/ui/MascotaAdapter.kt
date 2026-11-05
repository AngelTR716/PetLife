package com.example.petlife.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petlife.R
import com.example.petlife.data.Mascota

// El Adapter es el puente entre la base de datos y la lista visual (RecyclerView)
class MascotaAdapter(private var listaMascotas: List<Mascota>) : RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder>() {

    // 1. Clase interna que conecta las variables con los textos de la tarjeta (item_mascota.xml)
    class MascotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombreMascota)
        val tvEspecieRaza: TextView = itemView.findViewById(R.id.tvEspecieRaza)
    }

    // 2. Infla (dibuja) el diseño de la tarjeta por cada mascota que exista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mascota, parent, false)
        return MascotaViewHolder(view)
    }

    // 3. Llena la tarjeta con los datos reales de la mascota
    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = listaMascotas[position]
        holder.tvNombre.text = mascota.nombre
        // Concatenamos especie y raza separadas por un guion
        holder.tvEspecieRaza.text = "${mascota.especie} - ${mascota.raza}"

        // --- ESTE ES EL NUEVO CÓDIGO DEL CLIC ---
        // holder.itemView representa la tarjeta completa
        holder.itemView.setOnClickListener { view ->
            val bundle = android.os.Bundle().apply {
                putInt("mascotaId", mascota.id) // Guarda el ID de la mascota tocada
            }
            // Navega a la pantalla de perfil llevando el ID
            androidx.navigation.Navigation.findNavController(view)
                .navigate(R.id.perfilMascotaFragment, bundle)
        }
    }

    // 4. Le dice al sistema cuántos elementos hay en total en la lista
    override fun getItemCount(): Int {
        return listaMascotas.size
    }

    // 5. Actualiza la lista cuando se extraen datos nuevos de Room
    fun actualizarDatos(nuevaLista: List<Mascota>) {
        listaMascotas = nuevaLista
        notifyDataSetChanged() // Refresca la pantalla visualmente
    }
}