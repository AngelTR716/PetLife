package com.example.petlife.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MascotaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMascota(mascota: Mascota)

    // Aquí faltaba la "s" en tu código original
    @Query("SELECT * FROM mascotas")
    suspend fun obtenerTodasLasMascotas(): List<Mascota>

    // La nueva función que acabamos de agregar
    @Query("SELECT * FROM mascotas WHERE id = :id")
    suspend fun obtenerMascotaPorId(id: Int): Mascota
}