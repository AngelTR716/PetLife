package com.example.petlife.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MascotaDao {
    @Insert
    suspend fun insertarMascota(mascota: Mascota): Long

    @Insert
    suspend fun insertarRegistroUbicacion(registro: RegistroUbicacion)

    @Query("SELECT * FROM mascotas")
    suspend fun obtenerTodasLasMascotas(): List<Mascota>

    // Busca una mascota específica por su ID en la base de datos local
    @Query("SELECT * FROM tabla_mascota WHERE id = :id")
    suspend fun obtenerMascotaPorId(id: Int): Mascota
}