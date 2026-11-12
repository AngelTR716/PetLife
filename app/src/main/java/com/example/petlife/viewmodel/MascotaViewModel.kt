package com.example.petlife.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.petlife.data.Consulta
import com.example.petlife.data.Mascota
import com.example.petlife.data.PetLifeDatabase
import com.example.petlife.data.VacunaObligatoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ==========================================
// A. CLASE AUXILIAR PARA EL GPS
// Guarda el título (ej. "Vacuna Rabia") y las coordenadas que el mapa debe mostrar
// ==========================================
data class UbicacionGps(val titulo: String, val lat: Double, val lng: Double)

class MascotaViewModel(application: Application) : AndroidViewModel(application) {

    // Conexión directa a tu base de datos usando DAO
    private val dao = PetLifeDatabase.getDatabase(application).mascotaDao()

    // ==========================================
    // B. CONTROL DEL MÓDULO "REVISIÓN GPS"
    // Esta variable reactiva avisa a la interfaz cuándo y a dónde mover el mapa
    // ==========================================
    private val _ubicacionActual = MutableStateFlow<UbicacionGps?>(null)
    val ubicacionActual: StateFlow<UbicacionGps?> = _ubicacionActual.asStateFlow()

    // Función que se ejecuta al oprimir una Consulta, Vacuna o al cargar por defecto
    fun actualizarUbicacionGps(titulo: String, lat: Double, lng: Double) {
        _ubicacionActual.value = UbicacionGps(titulo, lat, lng)
    }

    // ==========================================
    // C. LÓGICA DEL CARNET
    // Al modificarlo, se actualiza y automáticamente crea el registro en Consultas
    // ==========================================
    fun modificarCarnet(mascota: Mascota, latActual: Double, lngActual: Double) {
        viewModelScope.launch {
            // 1. Guarda los nuevos datos de la mascota
            dao.actualizarMascota(mascota)

            // 2. Crea la "Actualización de carnet" en el historial de consultas
            val registroModificacion = Consulta(
                mascotaId = mascota.id,
                titulo = "Actualización de carnet",
                descripcion = "Se modificaron los datos del carnet",
                fecha = "Fecha actual", // Aquí puedes usar SimpleDateFormat en la versión final
                latitudConsulta = latActual,
                longitudConsulta = lngActual
            )
            dao.insertarConsulta(registroModificacion)

            // 3. Mueve el mapa a esta nueva ubicación
            actualizarUbicacionGps("Actualización de carnet", latActual, lngActual)
        }
    }

    // ==========================================
    // D. LÓGICA DE VACUNAS OBLIGATORIAS (Checklist)
    // ==========================================
    fun inicializarVacunasObligatorias(mascotaId: Long) {
        viewModelScope.launch {
            val listaGeneral = listOf(
                "Vacuna Obligatoria (Ej. Rabia)",
                "Vacuna Múltiple / Específica",
                "Refuerzo Anual",
                "Desparasitación Interna",
                "Desparasitación Externa"
            )
            listaGeneral.forEach { nombre ->
                dao.insertarVacuna(
                    VacunaObligatoria(
                        mascotaId = mascotaId,
                        nombreVacuna = nombre,
                        aplicada = false
                    )
                )
            }
        }
    }

    // ==========================================
    // E. LÓGICA DEL HISTORIAL DE CONSULTAS
    // ==========================================
    fun guardarConsulta(consulta: Consulta) = viewModelScope.launch {
        dao.insertarConsulta(consulta)
        actualizarUbicacionGps(consulta.titulo, consulta.latitudConsulta, consulta.longitudConsulta)
    }

    fun editarConsulta(consulta: Consulta) = viewModelScope.launch {
        dao.actualizarConsulta(consulta)
        actualizarUbicacionGps(consulta.titulo, consulta.latitudConsulta, consulta.longitudConsulta)
    }

    fun borrarConsulta(consulta: Consulta) = viewModelScope.launch {
        dao.eliminarConsulta(consulta)
    }
}