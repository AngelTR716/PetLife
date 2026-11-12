package com.example.petlife.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. entidad Mascota
@Entity(tableName = "mascota_table")
data class Mascota(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val nombre: String,
    val especie: String,
    val raza: String,
    val edad: String,
    val peso: String,
    val latitudRegistro: Double = 0.0,  // GPS
    val longitudRegistro: Double = 0.0
)

// 2.Vacunas Obligatorias (Checklist + GPS)
@Entity(tableName = "vacunas_obligatorias_table")
data class VacunaObligatoria(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val mascotaId: Long,
    val nombreVacuna: String,
    val aplicada: Boolean = false,       // Check-list
    val latitudAplicacion: Double? = null, // GPS donde se aplicó
    val longitudAplicacion: Double? = null
)

// 3. Historial de Consultas GPS
@Entity(tableName = "historial_consultas_table")
data class Consulta(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val mascotaId: Long,
    val titulo: String,          // Ej: "Consulta general" o "Actualización de carnet"
    val descripcion: String,
    val fecha: String,
    val latitudConsulta: Double, // GPS donde ocurrió la consulta o modificación
    val longitudConsulta: Double
)

