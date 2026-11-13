package com.example.petlife.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petlife.R
import com.example.petlife.data.Consulta

class ConsultaAdapter(
    private var consultas: List<Consulta>,
    // Funciones que avisan al Fragment cuando se tocan los elementos
    private val onConsultaClickeada: (Consulta) -> Unit,
    private val onEditarClick: (Consulta) -> Unit,
    private val onEliminarClick: (Consulta) -> Unit
) : RecyclerView.Adapter<ConsultaAdapter.ConsultaViewHolder>() {

    class ConsultaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTituloConsulta)
        val tvFecha: TextView = view.findViewById(R.id.tvFechaConsulta)
        val btnEditar: ImageButton = view.findViewById(R.id.btnEditarConsulta)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminarConsulta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_consulta, parent, false)
        return ConsultaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultaViewHolder, position: Int) {
        val consulta = consultas[position]

        // 1. Pintar los datos en la pantalla
        holder.tvTitulo.text = consulta.titulo
        holder.tvFecha.text = consulta.fecha

        // 2. Clic en el elemento completo para ver su ubicación GPS
        holder.itemView.setOnClickListener { onConsultaClickeada(consulta) }

        // 3. Conectar los clics de los iconos (lápiz y basurero)
        holder.btnEditar.setOnClickListener { onEditarClick(consulta) }
        holder.btnEliminar.setOnClickListener { onEliminarClick(consulta) }
    }

    override fun getItemCount(): Int = consultas.size

    // Refrescar la vista cuando Room detecta cambios
    fun actualizarLista(nuevaLista: List<Consulta>) {
        consultas = nuevaLista
        notifyDataSetChanged()
    }

    fun obtenerLista(): List<Consulta> = consultas
}