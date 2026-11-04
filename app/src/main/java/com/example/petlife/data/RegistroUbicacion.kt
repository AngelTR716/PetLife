package com.example.petlife.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros_ubicacion")
data class RegistroUbicacion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mascotaId: Int,
    val fechaHora: String,
    val descripcionCambio: String,
    val latitud: Double?,
    val longitud: Double?
)