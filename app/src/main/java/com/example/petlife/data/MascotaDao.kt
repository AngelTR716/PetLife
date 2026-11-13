package com.example.petlife.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MascotaDao {

    // --- CARNET / MASCOTA ---
    @Query("SELECT * FROM mascota_table LIMIT 1")
    fun obtenerMascota(): Flow<Mascota?>

    @Query("SELECT * FROM mascota_table")
    suspend fun obtenerTodasLasMascotas(): List<Mascota>

    @Query("SELECT * FROM mascota_table WHERE id = :id")
    fun obtenerMascotaPorId(id: Long): Flow<Mascota?>

    @Insert
    suspend fun insertarMascota(mascota: Mascota): Long

    @Update
    suspend fun actualizarMascota(mascota: Mascota)

    @Delete
    suspend fun eliminarMascota(mascota: Mascota)


    // --- VACUNAS OBLIGATORIAS (Checklist + GPS) ---
    @Query("SELECT * FROM vacunas_obligatorias_table WHERE mascotaId = :mascotaId")
    fun obtenerVacunasPorMascota(mascotaId: Long): Flow<List<VacunaObligatoria>>

    @Insert
    suspend fun insertarVacuna(vacuna: VacunaObligatoria)

    @Update
    suspend fun actualizarVacuna(vacuna: VacunaObligatoria)


    // --- HISTORIAL DE CONSULTAS (Modificar, eliminar y GPS) ---
    @Query("SELECT * FROM historial_consultas_table WHERE mascotaId = :mascotaId ORDER BY id DESC")
    fun obtenerConsultasPorMascota(mascotaId: Long): Flow<List<Consulta>>

    @Insert
    suspend fun insertarConsulta(consulta: Consulta)

    @Update
    suspend fun actualizarConsulta(consulta: Consulta)

    @Delete
    suspend fun eliminarConsulta(consulta: Consulta)
}