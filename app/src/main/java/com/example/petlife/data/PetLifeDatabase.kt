package com.example.petlife.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// versión 2
@Database(
    entities = [Mascota::class, RegistroUbicacion::class, VacunaObligatoria::class, Consulta::class],
    version = 2,
    exportSchema = false
)
abstract class PetLifeDatabase : RoomDatabase() {

    // Declaramos el DAO que acabamos de actualizar
    abstract fun mascotaDao(): MascotaDao

    companion object {
        @Volatile
        private var INSTANCE: PetLifeDatabase? = null

        fun getDatabase(context: Context): PetLifeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PetLifeDatabase::class.java,
                    "petlife_database"
                )
                    .fallbackToDestructiveMigration() // Limpia y recrea si cambia la versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}