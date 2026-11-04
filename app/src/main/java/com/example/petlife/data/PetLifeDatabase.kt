package com.example.petlife.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Mascota::class, RegistroUbicacion::class], version = 1, exportSchema = false)
abstract class PetLifeDatabase : RoomDatabase() {
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
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}