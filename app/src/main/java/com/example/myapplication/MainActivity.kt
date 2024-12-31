package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.font.FontFamily
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import java.io.ByteArrayOutputStream
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etDate = findViewById<EditText>(R.id.etDate)
        val etPreventivo = findViewById<EditText>(R.id.etPreventivo)
        val etClienteNome = findViewById<EditText>(R.id.etClienteNome)
        val etIndirizzo = findViewById<EditText>(R.id.etIndirizzo)
        val etCitta = findViewById<EditText>(R.id.etCitta)
        val etPiva = findViewById<EditText>(R.id.etPiva)
        val etTelefono = findViewById<EditText>(R.id.etTelefono)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etDescrizione = findViewById<EditText>(R.id.etDescrizione)
        val etTotale = findViewById<EditText>(R.id.etTotale)
        val btnGeneratePDF = findViewById<Button>(R.id.btnGeneratePDF)

        // Richiesta permessi
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Permesso necessario per salvare il PDF", Toast.LENGTH_SHORT).show()
            }
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        btnGeneratePDF.setOnClickListener {
            val date = etDate.text.toString()
            val preventivo = etPreventivo.text.toString()
            val clienteNome = etClienteNome.text.toString()
            val indirizzo = etIndirizzo.text.toString()
            val citta = etCitta.text.toString()
            val piva = etPiva.text.toString()
            val telefono = etTelefono.text.toString()
            val email = etEmail.text.toString()
            val descrizione = etDescrizione.text.toString()
            val totale = etTotale.text.toString()

            generatePDF(date, preventivo, clienteNome, indirizzo, citta, piva, telefono, email, descrizione, totale)
        }
    }

    private fun generatePDF(
        date: String,
        preventivo: String,
        clienteNome: String,
        indirizzo: String,
        citta: String,
        piva: String,
        telefono: String,
        email: String,
        descrizione: String,
        totale: String
    ) {
        try {
            val downloadsPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath ?: throw Exception("Percorso non disponibile")
            val file = File(downloadsPath, "Preventivo_$preventivo.pdf")


            val writer = PdfWriter(file)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument, PageSize.A4)

            // Sfondo
            val bgBitmap = BitmapFactory.decodeResource(resources, R.drawable.background_image)
            val bgStream = ByteArrayOutputStream()
            bgBitmap.compress(Bitmap.CompressFormat.PNG, 100, bgStream)
            val bgImage = Image(ImageDataFactory.create(bgStream.toByteArray()))
            bgImage.scaleToFit(PageSize.A4.width - 80, PageSize.A4.height - 150)
            bgImage.setFixedPosition(
                (PageSize.A4.width - bgImage.imageScaledWidth) / 2,
                pdfDocument.defaultPageSize.height - bgImage.imageScaledHeight - 30
            )
            document.add(bgImage)

            // Intestazione con separatore
            document.add(
                Paragraph("PREVENTIVO")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(25f)
                    .setBold()
                    .setMarginBottom(5f)
            )

// Linea separatrice
            document.add(
                Paragraph("")
                    .setBorderBottom(SolidBorder(ColorConstants.BLACK, 1f)) // Linea nera con altezza 1px
                    .setMarginBottom(10f)
            )

// Informazioni di data e numero preventivo
            document.add(
                Paragraph("Data: $date\nN. Preventivo: $preventivo")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(12f)
                    .setMarginBottom(20f)
            )

            val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
                .setWidth(UnitValue.createPercentValue(100f))
                .setMarginTop(10f)
                .setMarginBottom(20f)

// Cella Azienda
            val aziendaCell = Cell()
                .add(
                    Paragraph("Azienda")
                        .setTextAlignment(TextAlignment.CENTER) // Centro orizzontalmente
                        .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE) // Centro verticalmente
                        .setBold()
                        .setFontSize(13f)
                )
                .add(
                    Paragraph("\nMs Pitture Di Micivoda Sinan\nVia Fra' Gioacchino Stevan 13\nNove, Vicenza\n04255400246\n+393452589610\nmicisi79@gmail.com")
                        .setFontSize(12f)
                )
                .setPadding(10f)
                .setBorder(SolidBorder(1f))

            table.addCell(aziendaCell)

// Cella Cliente
            val clienteCell = Cell()
                .add(
                    Paragraph("Cliente")
                        .setTextAlignment(TextAlignment.CENTER) // Centro orizzontalmente
                        .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE) // Centro verticalmente
                        .setBold()
                        .setFontSize(13f)

                )
                .add(
                    Paragraph("\nNome: $clienteNome\nIndirizzo: $indirizzo\nCittà: $citta\nP.IVA: $piva\nTelefono: $telefono\nEmail: $email")
                        .setFontSize(12f)

                )
                .setPadding(10f)
                .setBorder(SolidBorder(1f))

            table.addCell(clienteCell)

// Aggiungi la tabella al documento
            document.add(table)




            // Descrizione dei lavori
            document.add(
                Paragraph("Descrizione dei lavori:")
                    .setBold()
                    .setMarginTop(10f)
                    .setMarginBottom(10f)
                    .setTextAlignment(TextAlignment.CENTER)
            )
            document.add(
                Paragraph(descrizione)
            )

            // Totale
            document.add(
                Paragraph("Totale: € $totale")
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20f)
            )

            // Footer
            val footerBitmap = BitmapFactory.decodeResource(resources, R.drawable.footer)
            val footerStream = ByteArrayOutputStream()
            footerBitmap.compress(Bitmap.CompressFormat.PNG, 100, footerStream)
            val footerImage = Image(ImageDataFactory.create(footerStream.toByteArray()))
            footerImage.scaleToFit(PageSize.A4.width - 40, 150f)
            footerImage.setFixedPosition(
                (PageSize.A4.width - footerImage.imageScaledWidth) / 2,
                20f
            )
            document.add(footerImage)

            document.close()

            Toast.makeText(this, "PDF salvato in: $downloadsPath", Toast.LENGTH_LONG).show()
            openPDF(file)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Errore: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun openPDF(file: File) {
        val uri: Uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(Intent.createChooser(intent, "Apri PDF con"))
    }
}