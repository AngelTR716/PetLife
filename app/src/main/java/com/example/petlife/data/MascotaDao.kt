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
}