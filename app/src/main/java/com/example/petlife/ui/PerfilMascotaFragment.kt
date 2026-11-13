package com.example.petlife.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petlife.R
import com.example.petlife.data.Consulta
import com.example.petlife.utils.PdfGenerator
import com.example.petlife.viewmodel.MascotaViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PerfilMascotaFragment : Fragment(R.layout.fragment_perfil_mascota) {

    private lateinit var viewModel: MascotaViewModel
    private lateinit var vacunaAdapter: VacunaAdapter
    private lateinit var consultaAdapter: ConsultaAdapter

    // Cliente para conectarnos al GPS de Google
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Lanzador para pedir permisos de ubicación en pantalla
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(requireContext(), "¡GPS Activado!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Se requiere GPS para guardar la ubicación", Toast.LENGTH_LONG).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[MascotaViewModel::class.java]

        // Inicializar el cliente de GPS
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        configurarListas(view)
        configurarClicsDePantalla(view)
        observarDatosDeLaBase(view)
    }

    // ==========================================
    // FUNCIÓN PARA OBTENER GPS REAL DEL TELÉFONO
    // ==========================================
    private fun obtenerUbicacionReal(onLocationResult: (lat: Double, lng: Double) -> Unit) {
        // Verificar si tenemos permiso
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Si hay permiso, pedimos la última ubicación conocida
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    onLocationResult(location.latitude, location.longitude)
                } else {
                    Toast.makeText(requireContext(), "Buscando señal GPS... intenta de nuevo", Toast.LENGTH_SHORT).show()
                    // Coordenadas de respaldo temporal por si el GPS del celular está apagado
                    onLocationResult(14.6349, -90.5069)
                }
            }
        } else {
            // Si no hay permiso, lanzamos la ventanita para que el usuario acepte
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun configurarListas(view: View) {
        val rvVacunas = view.findViewById<RecyclerView>(R.id.rvVacunasObligatorias)
        val rvConsultas = view.findViewById<RecyclerView>(R.id.rvHistorialConsultas)

        vacunaAdapter = VacunaAdapter(emptyList(),
            onCheckCambiado = { vacuna, isChecked ->
                if (isChecked) {
                    // ¡GPS REAL AL MARCAR LA VACUNA!
                    obtenerUbicacionReal { latitud, longitud ->
                        viewModel.marcarVacuna(vacuna, true, latitud, longitud)
                        Toast.makeText(requireContext(), "Vacuna '${vacuna.nombreVacuna}' aplicada con GPS", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    viewModel.marcarVacuna(vacuna, false, 0.0, 0.0)
                    Toast.makeText(requireContext(), "Vacuna desmarcada", Toast.LENGTH_SHORT).show()
                }
            },
            onVacunaClickeada = { vacuna ->
                if (vacuna.aplicada && vacuna.latitudAplicacion != null && vacuna.longitudAplicacion != null) {
                    viewModel.actualizarUbicacionGps("Vacuna: ${vacuna.nombreVacuna}", vacuna.latitudAplicacion, vacuna.longitudAplicacion)
                    Toast.makeText(requireContext(), "Mostrando ubicación de: ${vacuna.nombreVacuna}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Esta vacuna aún no ha sido aplicada", Toast.LENGTH_SHORT).show()
                }
            }
        )
        rvVacunas.layoutManager = LinearLayoutManager(requireContext())
        rvVacunas.adapter = vacunaAdapter

        consultaAdapter = ConsultaAdapter(emptyList(),
            onConsultaClickeada = { consulta ->
                viewModel.actualizarUbicacionGps("Consulta: ${consulta.titulo}", consulta.latitudConsulta, consulta.longitudConsulta)
                Toast.makeText(requireContext(), "Mostrando ubicación de: ${consulta.titulo}", Toast.LENGTH_SHORT).show()
            },
            onEditarClick = { consulta ->
                mostrarDialogoEditarConsulta(consulta)
            },
            onEliminarClick = { consulta ->
                viewModel.borrarConsulta(consulta)
            }
        )
        rvConsultas.layoutManager = LinearLayoutManager(requireContext())
        rvConsultas.adapter = consultaAdapter
    }

    private fun configurarClicsDePantalla(view: View) {
        val cardCarnet = view.findViewById<CardView>(R.id.cardCarnetMascota)
        val btnAgregarConsulta = view.findViewById<Button>(R.id.btnAgregarConsulta)

        cardCarnet.setOnClickListener {
            val mascota = viewModel.mascotaActual.value
            if (mascota != null && (mascota.latitudRegistro != 0.0 || mascota.longitudRegistro != 0.0)) {
                viewModel.actualizarUbicacionGps("Carnet: ${mascota.nombre}", mascota.latitudRegistro, mascota.longitudRegistro)
            }

            val opciones = arrayOf(
                "Ver Ubicación en Mapa",
                "Modificar Carnet",
                "📄 Generar y Compartir PDF",
                "Eliminar Mascota"
            )
            AlertDialog.Builder(requireContext())
                .setTitle("Opciones del Carnet")
                .setItems(opciones) { _, which ->
                    when (which) {
                        0 -> {
                            val m = viewModel.mascotaActual.value
                            if (m != null) {
                                viewModel.actualizarUbicacionGps("Carnet: ${m.nombre}", m.latitudRegistro, m.longitudRegistro)
                                Toast.makeText(requireContext(), "Mostrando ubicación del Carnet", Toast.LENGTH_SHORT).show()
                            }
                        }
                        1 -> mostrarDialogoModificarCarnet()
                        2 -> generarPdfActual()
                        3 -> mostrarDialogoConfirmarEliminarMascota()
                    }
                }
                .show()
        }

        btnAgregarConsulta.setOnClickListener {
            mostrarDialogoNuevaConsulta()
        }
    }

    private fun generarPdfActual() {
        val mascota = viewModel.mascotaActual.value
        if (mascota == null) {
            Toast.makeText(requireContext(), "No hay una mascota cargada", Toast.LENGTH_SHORT).show()
            return
        }

        val vacunasList = vacunaAdapter.obtenerLista()
        val consultasList = consultaAdapter.obtenerLista()

        PdfGenerator.generarYCompartirPdf(
            requireContext(),
            mascota,
            vacunasList,
            consultasList
        )
    }

    private fun mostrarDialogoConfirmarEliminarMascota() {
        val mascota = viewModel.mascotaActual.value
        if (mascota == null) {
            Toast.makeText(requireContext(), "No hay una mascota cargada", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Carnet")
            .setMessage("¿Estás seguro de que deseas eliminar el carnet de ${mascota.nombre}? Esta acción borrará la mascota y su historial.")
            .setPositiveButton("Eliminar") { dialog, _ ->
                viewModel.eliminarMascota(mascota)
                Toast.makeText(requireContext(), "Mascota eliminada correctamente", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                findNavController().popBackStack()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun mostrarDialogoNuevaConsulta() {
        val mascota = viewModel.mascotaActual.value
        if (mascota == null) {
            Toast.makeText(requireContext(), "No se ha cargado una mascota", Toast.LENGTH_SHORT).show()
            return
        }

        val context = requireContext()
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Nueva Consulta Médica")

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val paddingPx = (16 * resources.displayMetrics.density).toInt()
            setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
        }

        val etTitulo = EditText(context).apply {
            hint = "Título (Ej: Dolor de estómago)"
        }
        layout.addView(etTitulo)

        val etDescripcion = EditText(context).apply {
            hint = "Descripción (Ej: Le recetaron pastillas)"
        }
        layout.addView(etDescripcion)

        builder.setView(layout)

        builder.setPositiveButton("Guardar") { dialog, _ ->
            val titulo = etTitulo.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()

            if (titulo.isNotEmpty()) {
                obtenerUbicacionReal { latitud, longitud ->
                    val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    val nuevaConsulta = Consulta(
                        mascotaId = mascota.id,
                        titulo = titulo,
                        descripcion = descripcion,
                        fecha = fechaActual,
                        latitudConsulta = latitud,
                        longitudConsulta = longitud
                    )
                    viewModel.guardarConsulta(nuevaConsulta)
                    Toast.makeText(context, "Consulta guardada con ubicación GPS", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun mostrarDialogoEditarConsulta(consulta: Consulta) {
        val context = requireContext()
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Editar Consulta Médica")

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val paddingPx = (16 * resources.displayMetrics.density).toInt()
            setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
        }

        val etTitulo = EditText(context).apply {
            hint = "Título de la consulta"
            setText(consulta.titulo)
        }
        layout.addView(etTitulo)

        val etDescripcion = EditText(context).apply {
            hint = "Descripción"
            setText(consulta.descripcion)
        }
        layout.addView(etDescripcion)

        builder.setView(layout)

        builder.setPositiveButton("Guardar") { dialog, _ ->
            val titulo = etTitulo.text.toString().trim()
            val descripcion = etDescripcion.text.toString().trim()

            if (titulo.isNotEmpty()) {
                obtenerUbicacionReal { latitud, longitud ->
                    val consultaEditada = consulta.copy(
                        titulo = titulo,
                        descripcion = descripcion,
                        latitudConsulta = latitud,
                        longitudConsulta = longitud
                    )
                    viewModel.editarConsulta(consultaEditada)
                    Toast.makeText(context, "Consulta actualizada con GPS", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun mostrarDialogoModificarCarnet() {
        val mascota = viewModel.mascotaActual.value
        if (mascota == null) {
            Toast.makeText(requireContext(), "No se ha cargado una mascota", Toast.LENGTH_SHORT).show()
            return
        }

        val context = requireContext()
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Modificar Carnet de Mascota")

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val paddingPx = (16 * resources.displayMetrics.density).toInt()
            setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
        }

        val etNombre = EditText(context).apply {
            hint = "Nombre"
            setText(mascota.nombre)
        }
        layout.addView(etNombre)

        val etEspecie = EditText(context).apply {
            hint = "Especie"
            setText(mascota.especie)
        }
        layout.addView(etEspecie)

        val etRaza = EditText(context).apply {
            hint = "Raza"
            setText(mascota.raza)
        }
        layout.addView(etRaza)

        val etEdad = EditText(context).apply {
            hint = "Edad"
            setText(mascota.edad)
        }
        layout.addView(etEdad)

        val etPeso = EditText(context).apply {
            hint = "Peso"
            setText(mascota.peso)
        }
        layout.addView(etPeso)

        builder.setView(layout)

        builder.setPositiveButton("Guardar") { dialog, _ ->
            val nombre = etNombre.text.toString().trim()
            if (nombre.isNotEmpty()) {
                obtenerUbicacionReal { latitud, longitud ->
                    val mascotaActualizada = mascota.copy(
                        nombre = nombre,
                        especie = etEspecie.text.toString().trim(),
                        raza = etRaza.text.toString().trim(),
                        edad = etEdad.text.toString().trim(),
                        peso = etPeso.text.toString().trim()
                    )
                    viewModel.modificarCarnet(mascotaActualizada, latitud, longitud)
                    Toast.makeText(context, "Carnet modificado con GPS", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun observarDatosDeLaBase(view: View) {
        val tvNombre = view.findViewById<TextView>(R.id.tvCarnetNombre)
        val tvEspecie = view.findViewById<TextView>(R.id.tvCarnetEspecie)
        val tvRaza = view.findViewById<TextView>(R.id.tvCarnetRaza)
        val tvEdad = view.findViewById<TextView>(R.id.tvCarnetEdad)
        val tvPeso = view.findViewById<TextView>(R.id.tvCarnetPeso)
        val tvId = view.findViewById<TextView>(R.id.tvCarnetId)
        val tvEtiquetaGps = view.findViewById<TextView>(R.id.tvEtiquetaUbicacionGps)
        val btnVerMapa = view.findViewById<Button>(R.id.btnVerEnMapa)

        var mascotaIdActual = -1L
        var latitudActualMapa = 0.0
        var longitudActualMapa = 0.0

        // 1. Observar datos
        lifecycleScope.launch {
            viewModel.mascotaActual.collectLatest { mascota ->
                mascota?.let {
                    tvNombre.text = it.nombre
                    tvEspecie.text = it.especie
                    tvRaza.text = it.raza
                    tvEdad.text = it.edad
                    tvPeso.text = it.peso
                    tvId.text = "ID. ${it.id}"

                    // Obtenemos tu GPS actual al abrir el perfil
                    obtenerUbicacionReal { latitud, longitud ->
                        viewModel.actualizarUbicacionGps("Ubicación Actual de ${it.nombre}", latitud, longitud)
                    }

                    if (it.id != mascotaIdActual) {
                        mascotaIdActual = it.id
                        lifecycleScope.launch {
                            viewModel.obtenerVacunas(it.id).collectLatest { lista ->
                                if (lista.isEmpty()) viewModel.inicializarVacunasObligatorias(it.id)
                                else vacunaAdapter.actualizarLista(lista)
                            }
                        }
                        lifecycleScope.launch {
                            viewModel.obtenerConsultas(it.id).collectLatest { lista ->
                                consultaAdapter.actualizarLista(lista)
                            }
                        }
                    }
                }
            }
        }

        // 2. Observar GPS seleccionado
        lifecycleScope.launch {
            viewModel.ubicacionActual.collectLatest { ubicacion ->
                if (ubicacion != null) {
                    tvEtiquetaGps.text = "Mostrando ubicación de:\n${ubicacion.titulo}"
                    latitudActualMapa = ubicacion.lat
                    longitudActualMapa = ubicacion.lng
                }
            }
        }

        // 3. Abrir Google Maps
        btnVerMapa.setOnClickListener {
            if (latitudActualMapa != 0.0 && longitudActualMapa != 0.0) {
                val uri = Uri.parse("geo:$latitudActualMapa,$longitudActualMapa?q=$latitudActualMapa,$longitudActualMapa(Ubicación)")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=$latitudActualMapa,$longitudActualMapa")))
                }
            }
        }
    }
}