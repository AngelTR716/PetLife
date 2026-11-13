package com.example.petlife.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.petlife.R
import com.example.petlife.data.VacunaObligatoria

class VacunaAdapter(
    private var vacunas: List<VacunaObligatoria>,
    // Funciones que le avisarán a tu Fragment cuando el usuario interactúe
    private val onCheckCambiado: (VacunaObligatoria, Boolean) -> Unit,
    private val onVacunaClickeada: (VacunaObligatoria) -> Unit
) : RecyclerView.Adapter<VacunaAdapter.VacunaViewHolder>() {

    class VacunaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbVacuna: CheckBox = view.findViewById(R.id.cbVacunaAplicada)
        val ivGps: ImageView = view.findViewById(R.id.ivGpsGuardado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacunaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vacuna, parent, false)
        return VacunaViewHolder(view)
    }

    override fun onBindViewHolder(holder: VacunaViewHolder, position: Int) {
        val vacuna = vacunas[position]

        // 1. Quitamos el listener un momento para evitar bugs de reciclaje
        holder.cbVacuna.setOnCheckedChangeListener(null)

        // 2. Colocamos el nombre y si está marcada o no
        holder.cbVacuna.text = vacuna.nombreVacuna
        holder.cbVacuna.isChecked = vacuna.aplicada

        // 3. Pintamos el iconito de GPS: Verde si ya se aplicó, Gris si no
        if (vacuna.aplicada && vacuna.latitudAplicacion != null) {
            holder.ivGps.setColorFilter(Color.parseColor("#10B981")) // Verde
        } else {
            holder.ivGps.setColorFilter(Color.parseColor("#D1D5DB")) // Gris
        }

        // 4. Si el usuario marca/desmarca la casilla, avisamos al Fragment
        holder.cbVacuna.setOnCheckedChangeListener { _, isChecked ->
            onCheckCambiado(vacuna, isChecked)
        }

        // 5. Si tocan toda la fila y ya tiene GPS, avisamos para que el mapa se mueva
        holder.itemView.setOnClickListener {
            if (vacuna.aplicada) {
                onVacunaClickeada(vacuna)
            }
        }
    }

    override fun getItemCount(): Int = vacunas.size

    // Función para refrescar la lista cuando haya cambios en la base de datos
    fun actualizarLista(nuevaLista: List<VacunaObligatoria>) {
        vacunas = nuevaLista
        notifyDataSetChanged()
    }

    fun obtenerLista(): List<VacunaObligatoria> = vacunas
}