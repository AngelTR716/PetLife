package com.example.petlife.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.petlife.data.Consulta
import com.example.petlife.data.Mascota
import com.example.petlife.data.VacunaObligatoria
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    fun generarYCompartirPdf(
        context: Context,
        mascota: Mascota,
        vacunas: List<VacunaObligatoria>,
        consultas: List<Consulta>
    ) {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        var pageNumber = 1

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint()
        val textPaint = Paint()

        var y = 40f

        // Función para verificar si se requiere crear una nueva página
        fun checkNewPage(neededSpace: Float) {
            if (y + neededSpace > pageHeight - 50) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 40f
            }
        }

        // ==========================================
        // 1. ENCABEZADO PRINCIPAL (Tema Oscuro #1F2937)
        // ==========================================
        paint.color = Color.parseColor("#1F2937")
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(RectF(30f, y, (pageWidth - 30).toFloat(), y + 70f), 12f, 12f, paint)

        textPaint.color = Color.WHITE
        textPaint.textSize = 18f
        textPaint.isFakeBoldText = true
        canvas.drawText("🐾 CARNET DE IDENTIFICACIÓN - PetLife", 45f, y + 30f, textPaint)

        textPaint.color = Color.parseColor("#9CA3AF")
        textPaint.textSize = 11f
        textPaint.isFakeBoldText = false
        val fechaGeneracion = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Generado el: $fechaGeneracion", 45f, y + 52f, textPaint)

        y += 85f

        // ==========================================
        // 2. TARJETA DEL CARNET DE LA MASCOTA
        // ==========================================
        checkNewPage(180f)

        // Borde exterior
        paint.color = Color.parseColor("#E5E7EB")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRoundRect(RectF(30f, y, (pageWidth - 30).toFloat(), y + 160f), 10f, 10f, paint)

        // Fondo de tarjeta
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#F9FAFB")
        canvas.drawRoundRect(RectF(30f, y, (pageWidth - 30).toFloat(), y + 160f), 10f, 10f, paint)

        // Titular del Carnet
        textPaint.color = Color.parseColor("#1F2937")
        textPaint.textSize = 22f
        textPaint.isFakeBoldText = true
        canvas.drawText("🐾 ${mascota.nombre}", 50f, y + 35f, textPaint)

        textPaint.color = Color.parseColor("#4B5563")
        textPaint.textSize = 12f
        textPaint.isFakeBoldText = true
        canvas.drawText("ID REGISTRO: ${mascota.id}", (pageWidth - 180).toFloat(), y + 35f, textPaint)

        // Línea divisora verde
        paint.color = Color.parseColor("#10B981")
        paint.strokeWidth = 3f
        canvas.drawLine(50f, y + 45f, (pageWidth - 50).toFloat(), y + 45f, paint)

        // Datos en rejilla
        textPaint.textSize = 12f
        textPaint.isFakeBoldText = true
        textPaint.color = Color.parseColor("#374151")

        canvas.drawText("Especie:", 50f, y + 75f, textPaint)
        textPaint.isFakeBoldText = false
        canvas.drawText(mascota.especie, 110f, y + 75f, textPaint)

        textPaint.isFakeBoldText = true
        canvas.drawText("Raza:", 260f, y + 75f, textPaint)
        textPaint.isFakeBoldText = false
        canvas.drawText(mascota.raza, 310f, y + 75f, textPaint)

        textPaint.isFakeBoldText = true
        canvas.drawText("Edad:", 50f, y + 105f, textPaint)
        textPaint.isFakeBoldText = false
        canvas.drawText(mascota.edad, 110f, y + 105f, textPaint)

        textPaint.isFakeBoldText = true
        canvas.drawText("Peso:", 260f, y + 105f, textPaint)
        textPaint.isFakeBoldText = false
        canvas.drawText(mascota.peso, 310f, y + 105f, textPaint)

        textPaint.isFakeBoldText = true
        canvas.drawText("GPS Registro:", 50f, y + 135f, textPaint)
        textPaint.isFakeBoldText = false
        val gpsRegistro = if (mascota.latitudRegistro != 0.0) "${mascota.latitudRegistro}, ${mascota.longitudRegistro}" else "No registrado"
        canvas.drawText(gpsRegistro, 140f, y + 135f, textPaint)

        y += 180f

        // ==========================================
        // 3. VACUNAS OBLIGATORIAS
        // ==========================================
        checkNewPage(40f)

        paint.color = Color.parseColor("#10B981")
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(RectF(30f, y, (pageWidth - 30).toFloat(), y + 26f), 6f, 6f, paint)

        textPaint.color = Color.WHITE
        textPaint.textSize = 13f
        textPaint.isFakeBoldText = true
        canvas.drawText("VACUNAS OBLIGATORIAS Y DESPARASITACIÓN", 45f, y + 18f, textPaint)

        y += 35f

        if (vacunas.isEmpty()) {
            checkNewPage(25f)
            textPaint.color = Color.GRAY
            textPaint.isFakeBoldText = false
            canvas.drawText("No hay vacunas registradas.", 50f, y + 15f, textPaint)
            y += 25f
        } else {
            for (v in vacunas) {
                checkNewPage(28f)

                textPaint.isFakeBoldText = true
                if (v.aplicada) {
                    textPaint.color = Color.parseColor("#059669")
                    canvas.drawText("[✓] APLICADA", 50f, y + 15f, textPaint)
                } else {
                    textPaint.color = Color.parseColor("#DC2626")
                    canvas.drawText("[  ] PENDIENTE", 50f, y + 15f, textPaint)
                }

                textPaint.color = Color.parseColor("#1F2937")
                textPaint.isFakeBoldText = false
                canvas.drawText(v.nombreVacuna, 150f, y + 15f, textPaint)

                if (v.aplicada && v.latitudAplicacion != null && v.longitudAplicacion != null) {
                    textPaint.color = Color.parseColor("#6B7280")
                    textPaint.textSize = 10f
                    canvas.drawText("GPS: ${v.latitudAplicacion}, ${v.longitudAplicacion}", 380f, y + 15f, textPaint)
                    textPaint.textSize = 12f
                }

                y += 24f
            }
        }

        y += 15f

        // ==========================================
        // 4. HISTORIAL DE CONSULTAS Y MODIFICACIONES
        // ==========================================
        checkNewPage(40f)

        paint.color = Color.parseColor("#3B82F6")
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(RectF(30f, y, (pageWidth - 30).toFloat(), y + 26f), 6f, 6f, paint)

        textPaint.color = Color.WHITE
        textPaint.textSize = 13f
        textPaint.isFakeBoldText = true
        canvas.drawText("HISTORIAL DE CONSULTAS Y MODIFICACIONES", 45f, y + 18f, textPaint)

        y += 35f

        if (consultas.isEmpty()) {
            checkNewPage(25f)
            textPaint.color = Color.GRAY
            textPaint.isFakeBoldText = false
            canvas.drawText("No hay consultas o modificaciones registradas.", 50f, y + 15f, textPaint)
            y += 25f
        } else {
            for (c in consultas) {
                checkNewPage(45f)

                textPaint.color = Color.parseColor("#1F2937")
                textPaint.isFakeBoldText = true
                canvas.drawText("• ${c.titulo}", 50f, y + 15f, textPaint)

                textPaint.color = Color.parseColor("#6B7280")
                textPaint.textSize = 10f
                canvas.drawText("Fecha: ${c.fecha}", (pageWidth - 160).toFloat(), y + 15f, textPaint)

                if (c.descripcion.isNotEmpty()) {
                    textPaint.color = Color.parseColor("#4B5563")
                    textPaint.textSize = 11f
                    textPaint.isFakeBoldText = false
                    canvas.drawText("  Detalle: ${c.descripcion}", 60f, y + 30f, textPaint)
                }

                if (c.latitudConsulta != 0.0) {
                    textPaint.color = Color.parseColor("#9CA3AF")
                    textPaint.textSize = 9f
                    canvas.drawText("GPS: ${c.latitudConsulta}, ${c.longitudConsulta}", (pageWidth - 160).toFloat(), y + 30f, textPaint)
                }

                y += 40f
            }
        }

        // Finalizar última página
        pdfDocument.finishPage(page)

        // ==========================================
        // 5. GUARDAR Y COMPARTIR ARCHIVO PDF (WhatsApp, etc.)
        // ==========================================
        try {
            val fileDir = File(context.cacheDir, "pdf")
            if (!fileDir.exists()) fileDir.mkdirs()

            val nombreLimpio = mascota.nombre.replace("[^a-zA-Z0-9]".toRegex(), "_")
            val pdfFile = File(fileDir, "Carnet_${nombreLimpio}.pdf")
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                pdfFile
            )

            // Intent para compartir por WhatsApp, Correo, etc.
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "Carnet de Identificación - ${mascota.nombre}")
                putExtra(Intent.EXTRA_TEXT, "Te comparto el Carnet Veterinario de ${mascota.nombre} (PetLife).")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Compartir Carnet PDF por WhatsApp, Correo...")
            context.startActivity(chooser)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
