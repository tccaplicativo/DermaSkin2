package com.perabru.dermaskin2

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RelatorioProfissionalActivity : AppCompatActivity() {

    private lateinit var txtAreaRelatorio: TextView
    private lateinit var imgRelatorioFoto: ImageView
    private lateinit var txtResumoRelatorio: TextView
    private lateinit var txtClinicoRelatorio: TextView
    private lateinit var btnExportarRelatorioPdf: Button
    private lateinit var btnHistoricoRelatorio: Button
    private lateinit var btnVoltarRelatorio: Button

    private var areaSelecionada: String = ""
    private var imagePath: String = ""

    private var tempoLesao: String = ""
    private var mudancaTamanho: String = ""
    private var mudancaCor: String = ""
    private var coceira: String = ""
    private var sangramento: String = ""
    private var dor: String = ""
    private var formatoIrregular: String = ""

    private var riscoTitulo: String = ""
    private var riscoPercentual: String = ""
    private var riscoDescricao: String = ""

    private var pendingPdfBytes: ByteArray? = null

    private val createPdfLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
            if (uri != null) {
                try {
                    val bytes = pendingPdfBytes

                    if (bytes == null) {
                        Toast.makeText(this, "Nenhum PDF foi gerado.", Toast.LENGTH_LONG).show()
                        return@registerForActivityResult
                    }

                    contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(bytes)
                    }

                    Toast.makeText(this, "Relatório salvo com sucesso!", Toast.LENGTH_LONG).show()

                } catch (e: Exception) {
                    Toast.makeText(this, "Erro ao salvar PDF: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relatorio_profissional)

        recuperarDados()
        iniciarComponentes()
        configurarTela()
        configurarCliques()
    }

    private fun recuperarDados() {
        areaSelecionada = intent.getStringExtra("areaSelecionada") ?: "Área não informada"
        imagePath = intent.getStringExtra("imagePath") ?: ""

        tempoLesao = intent.getStringExtra("tempoLesao") ?: "Não informado"
        mudancaTamanho = intent.getStringExtra("mudancaTamanho") ?: "Não informado"
        mudancaCor = intent.getStringExtra("mudancaCor") ?: "Não informado"
        coceira = intent.getStringExtra("coceira") ?: "Não informado"
        sangramento = intent.getStringExtra("sangramento") ?: "Não informado"
        dor = intent.getStringExtra("dor") ?: "Não informado"
        formatoIrregular = intent.getStringExtra("formatoIrregular") ?: "Não informado"

        riscoTitulo = intent.getStringExtra("riscoTitulo") ?: "Risco não calculado"
        riscoPercentual = intent.getStringExtra("riscoPercentual") ?: "--"
        riscoDescricao = intent.getStringExtra("riscoDescricao") ?: "Sem descrição disponível."
    }

    private fun iniciarComponentes() {
        txtAreaRelatorio = findViewById(R.id.txtAreaRelatorio)
        imgRelatorioFoto = findViewById(R.id.imgRelatorioFoto)
        txtResumoRelatorio = findViewById(R.id.txtResumoRelatorio)
        txtClinicoRelatorio = findViewById(R.id.txtClinicoRelatorio)

        btnExportarRelatorioPdf = findViewById(R.id.btnExportarRelatorioPdf)
        btnHistoricoRelatorio = findViewById(R.id.btnHistoricoRelatorio)
        btnVoltarRelatorio = findViewById(R.id.btnVoltarRelatorio)
    }

    private fun configurarTela() {
        txtAreaRelatorio.text = "Área selecionada: $areaSelecionada"

        txtResumoRelatorio.text =
            "Classificação: $riscoTitulo\n" +
                    "Percentual estimado: $riscoPercentual\n\n" +
                    riscoDescricao

        txtClinicoRelatorio.text =
            "Tempo da lesão: $tempoLesao\n\n" +
                    "Mudança de tamanho: $mudancaTamanho\n" +
                    "Mudança de cor: $mudancaCor\n" +
                    "Coceira: $coceira\n" +
                    "Sangramento: $sangramento\n" +
                    "Dor: $dor\n" +
                    "Formato irregular: $formatoIrregular"

        carregarImagem()
    }

    private fun carregarImagem() {
        if (imagePath.isBlank()) return

        val file = File(imagePath)

        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            imgRelatorioFoto.setImageBitmap(bitmap)
        }
    }

    private fun configurarCliques() {
        btnExportarRelatorioPdf.setOnClickListener {
            exportarPdf()
        }

        btnHistoricoRelatorio.setOnClickListener {
            val intent = Intent(this, HistoricoAnalisesActivity::class.java)
            startActivity(intent)
        }

        btnVoltarRelatorio.setOnClickListener {
            finish()
        }
    }

    private fun exportarPdf() {
        try {
            pendingPdfBytes = gerarPdfBytes()
            val fileName = "relatorio_dermaskin_${System.currentTimeMillis()}.pdf"
            createPdfLauncher.launch(fileName)
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao gerar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun gerarPdfBytes(): ByteArray {
        val pdfDocument = PdfDocument()

        val pageWidth = 595
        val pageHeight = 842

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)

        desenharPdf(page.canvas)

        pdfDocument.finishPage(page)

        val outputStream = ByteArrayOutputStream()
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()

        return outputStream.toByteArray()
    }

    private fun desenharPdf(canvas: Canvas) {
        val pageWidth = 595f

        val titlePaint = Paint().apply {
            color = Color.parseColor("#4A2A1A")
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.parseColor("#8B4A2F")
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val normalPaint = Paint().apply {
            color = Color.parseColor("#2E1B13")
            textSize = 11f
            isAntiAlias = true
        }

        val smallPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 9f
            isAntiAlias = true
        }

        val whitePaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val creamPaint = Paint().apply {
            color = Color.parseColor("#FFF8F2")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val borderPaint = Paint().apply {
            color = Color.parseColor("#E0BFAE")
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }

        val riskPaint = Paint().apply {
            color = when {
                riscoTitulo.contains("alto", true) -> Color.parseColor("#D9534F")
                riscoTitulo.contains("médio", true) || riscoTitulo.contains("medio", true) -> Color.parseColor("#E6A23C")
                riscoTitulo.contains("baixo", true) -> Color.parseColor("#5CB85C")
                else -> Color.parseColor("#4A2A1A")
            }
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        canvas.drawColor(Color.parseColor("#F7EFE8"))

        canvas.drawRoundRect(30f, 30f, pageWidth - 30f, 115f, 24f, 24f, whitePaint)
        canvas.drawText("DermaSkin", 50f, 66f, titlePaint)
        canvas.drawText("Relatório para profissional de saúde", 50f, 92f, subtitlePaint)

        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(Date())
        canvas.drawText("Data: $date", 410f, 66f, smallPaint)

        canvas.drawRoundRect(30f, 135f, pageWidth - 30f, 260f, 24f, 24f, creamPaint)
        canvas.drawRoundRect(30f, 135f, pageWidth - 30f, 260f, 24f, 24f, borderPaint)

        canvas.drawText("Resultado da triagem", 50f, 165f, subtitlePaint)
        canvas.drawText(riscoTitulo, 50f, 205f, titlePaint)
        canvas.drawText(riscoPercentual, 410f, 205f, riskPaint)

        drawMultilineText(canvas, riscoDescricao, 50f, 232f, normalPaint, 88)

        canvas.drawRoundRect(30f, 280f, pageWidth - 30f, 445f, 24f, 24f, whitePaint)
        canvas.drawRoundRect(30f, 280f, pageWidth - 30f, 445f, 24f, 24f, borderPaint)

        canvas.drawText("Dados informados pelo usuário", 50f, 310f, subtitlePaint)

        val dadosClinicos =
            "Área da lesão: $areaSelecionada\n" +
                    "Tempo da lesão: $tempoLesao\n" +
                    "Mudança de tamanho: $mudancaTamanho\n" +
                    "Mudança de cor: $mudancaCor\n" +
                    "Coceira: $coceira\n" +
                    "Sangramento: $sangramento\n" +
                    "Dor: $dor\n" +
                    "Formato irregular: $formatoIrregular"

        drawMultilineText(canvas, dadosClinicos, 50f, 338f, normalPaint, 80)

        val file = File(imagePath)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            val image = Bitmap.createScaledBitmap(bitmap, 120, 120, true)
            canvas.drawBitmap(image, 420f, 315f, null)
            canvas.drawText("Imagem analisada", 420f, 445f, smallPaint)
        }

        canvas.drawRoundRect(30f, 465f, pageWidth - 30f, 630f, 24f, 24f, creamPaint)
        canvas.drawRoundRect(30f, 465f, pageWidth - 30f, 630f, 24f, 24f, borderPaint)

        canvas.drawText("Observações para avaliação profissional", 50f, 495f, subtitlePaint)

        val observacoes =
            "Este relatório organiza a imagem, a localização da lesão, as respostas clínicas e a classificação inicial gerada pelo aplicativo. " +
                    "A triagem não possui valor diagnóstico definitivo. A decisão clínica deve ser feita por profissional habilitado, considerando exame físico, dermatoscopia e, se necessário, outros procedimentos."

        drawMultilineText(canvas, observacoes, 50f, 522f, normalPaint, 88)

        canvas.drawRoundRect(30f, 650f, pageWidth - 30f, 760f, 24f, 24f, whitePaint)
        canvas.drawRoundRect(30f, 650f, pageWidth - 30f, 760f, 24f, 24f, borderPaint)

        canvas.drawText("Aviso importante", 50f, 680f, subtitlePaint)

        val aviso =
            "O DermaSkin realiza apenas uma triagem visual com apoio de tecnologia. " +
                    "Este documento não substitui consulta médica, não confirma diagnóstico e não descarta doenças de pele."

        drawMultilineText(canvas, aviso, 50f, 707f, normalPaint, 88)

        canvas.drawText("Gerado pelo aplicativo DermaSkin", 40f, 825f, smallPaint)
    }

    private fun drawMultilineText(
        canvas: Canvas,
        text: String,
        x: Float,
        startY: Float,
        paint: Paint,
        maxCharsPerLine: Int
    ) {
        var y = startY
        val lines = mutableListOf<String>()

        text.split("\n").forEach { paragraph ->
            if (paragraph.length <= maxCharsPerLine) {
                lines.add(paragraph)
            } else {
                var currentLine = ""

                paragraph.split(" ").forEach { word ->
                    val testLine = (currentLine + " " + word).trim()

                    if (testLine.length <= maxCharsPerLine) {
                        currentLine = testLine
                    } else {
                        if (currentLine.isNotBlank()) lines.add(currentLine)
                        currentLine = word
                    }
                }

                if (currentLine.isNotBlank()) lines.add(currentLine)
            }
        }

        for (line in lines) {
            canvas.drawText(line, x, y, paint)
            y += 15f
        }
    }
}