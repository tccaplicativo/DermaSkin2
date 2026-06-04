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
        areaSelecionada = intent.getStringExtra("areaSelecionada") ?: "Não informada"
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
        txtAreaRelatorio.text = "Área da lesão: $areaSelecionada"

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
            val fileName = "relatorio_dermaprev_${System.currentTimeMillis()}.pdf"
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
        val pageHeight = 842f
        val margin = 34f
        val right = pageWidth - margin

        canvas.drawColor(Color.parseColor("#F6FBFF"))

        val cardPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val softBluePaint = Paint().apply {
            color = Color.parseColor("#F2FAFF")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val borderPaint = Paint().apply {
            color = Color.parseColor("#DDECF7")
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            isAntiAlias = true
        }

        val dermaPaint = Paint().apply {
            color = Color.parseColor("#063B78")
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val prevPaint = Paint().apply {
            color = Color.parseColor("#1DB9D2")
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val sectionPaint = Paint().apply {
            color = Color.parseColor("#063B78")
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val titlePaint = Paint().apply {
            color = Color.parseColor("#063B78")
            textSize = 27f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val normalPaint = Paint().apply {
            color = Color.parseColor("#173B70")
            textSize = 10.5f
            isAntiAlias = true
        }

        val smallPaint = Paint().apply {
            color = Color.parseColor("#68798F")
            textSize = 8.5f
            isAntiAlias = true
        }

        val mutedPaint = Paint().apply {
            color = Color.parseColor("#50627A")
            textSize = 9.5f
            isAntiAlias = true
        }

        val riskPaint = Paint().apply {
            color = when {
                riscoTitulo.contains("alto", true) -> Color.parseColor("#D9534F")
                riscoTitulo.contains("médio", true) || riscoTitulo.contains("medio", true) -> Color.parseColor("#E6A23C")
                riscoTitulo.contains("baixo", true) -> Color.parseColor("#28A745")
                else -> Color.parseColor("#063B78")
            }
            textSize = 36f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val iconPaint = Paint().apply {
            color = Color.parseColor("#0A4BCF")
            textSize = 24f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        // HEADER
        canvas.drawRoundRect(margin, 28f, right, 112f, 24f, 24f, cardPaint)
        canvas.drawRoundRect(margin, 28f, right, 112f, 24f, 24f, borderPaint)

        val logo = BitmapFactory.decodeResource(resources, R.drawable.logo_dermaprev)
        val logoSize = 36
        val logoScaled = Bitmap.createScaledBitmap(logo, logoSize, logoSize, true)

        val dermaText = "Derma"
        val prevText = "Prev"
        val dermaWidth = dermaPaint.measureText(dermaText)
        val prevWidth = prevPaint.measureText(prevText)
        val brandWidth = logoSize + 12f + dermaWidth + prevWidth

        val brandStartX = (pageWidth - brandWidth) / 2f
        val logoY = 47f
        val textBaseY = 75f

        canvas.drawBitmap(logoScaled, brandStartX, logoY, null)
        canvas.drawText(dermaText, brandStartX + logoSize + 12f, textBaseY, dermaPaint)
        canvas.drawText(prevText, brandStartX + logoSize + 12f + dermaWidth, textBaseY, prevPaint)

        val subtitle = "Relatório para profissional de saúde"
        val subtitleWidth = mutedPaint.measureText(subtitle)
        canvas.drawText(subtitle, (pageWidth - subtitleWidth) / 2f, 94f, mutedPaint)

        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(Date())
        val dateText = "Data: $date"
        val dateWidth = smallPaint.measureText(dateText)
        canvas.drawText(dateText, right - dateWidth - 12f, 55f, smallPaint)

        // CARD RESULTADO
        canvas.drawRoundRect(margin, 132f, right, 252f, 24f, 24f, cardPaint)
        canvas.drawRoundRect(margin, 132f, right, 252f, 24f, 24f, borderPaint)

        canvas.drawText("Resultado da triagem", 54f, 160f, sectionPaint)
        canvas.drawText(riscoTitulo, 54f, 197f, titlePaint)

        val riskWidth = riskPaint.measureText(riscoPercentual)
        canvas.drawText(riscoPercentual, right - riskWidth - 36f, 197f, riskPaint)

        drawMultilineText(
            canvas,
            riscoDescricao,
            54f,
            222f,
            normalPaint,
            82
        )

        // CARD DADOS + IMAGEM
        canvas.drawRoundRect(margin, 272f, right, 448f, 24f, 24f, cardPaint)
        canvas.drawRoundRect(margin, 272f, right, 448f, 24f, 24f, borderPaint)

        canvas.drawText("Dados informados pelo usuário", 54f, 302f, sectionPaint)

        val dadosClinicos =
            "Área da lesão: $areaSelecionada\n" +
                    "Tempo da lesão: $tempoLesao\n" +
                    "Mudança de tamanho: $mudancaTamanho\n" +
                    "Mudança de cor: $mudancaCor\n" +
                    "Coceira: $coceira\n" +
                    "Sangramento: $sangramento\n" +
                    "Dor: $dor\n" +
                    "Formato irregular: $formatoIrregular"

        drawMultilineText(canvas, dadosClinicos, 54f, 330f, normalPaint, 56)

        val file = File(imagePath)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            val image = Bitmap.createScaledBitmap(bitmap, 112, 112, true)

            canvas.drawRoundRect(420f, 310f, 540f, 430f, 18f, 18f, softBluePaint)
            canvas.drawRoundRect(420f, 310f, 540f, 430f, 18f, 18f, borderPaint)
            canvas.drawBitmap(image, 424f, 314f, null)
            canvas.drawText("Imagem analisada", 426f, 442f, smallPaint)
        }

        // CARD OBSERVAÇÕES
        canvas.drawRoundRect(margin, 468f, right, 616f, 24f, 24f, cardPaint)
        canvas.drawRoundRect(margin, 468f, right, 616f, 24f, 24f, borderPaint)

        canvas.drawText("Observações para avaliação profissional", 54f, 498f, sectionPaint)

        val observacoes =
            "Este relatório organiza a imagem, a localização da lesão, as respostas clínicas e a classificação inicial gerada pelo aplicativo. " +
                    "A triagem não possui valor diagnóstico definitivo. A decisão clínica deve ser feita por profissional habilitado, considerando exame físico, dermatoscopia e, se necessário, outros procedimentos."

        drawMultilineText(canvas, observacoes, 54f, 526f, normalPaint, 86)

        // CARD AVISO
        canvas.drawRoundRect(margin, 636f, right, 742f, 24f, 24f, cardPaint)
        canvas.drawRoundRect(margin, 636f, right, 742f, 24f, 24f, borderPaint)

        canvas.drawCircle(64f, 674f, 18f, softBluePaint)
        canvas.drawText("!", 64f, 683f, iconPaint)

        canvas.drawText("Aviso importante", 92f, 668f, sectionPaint)

        val aviso =
            "O DermaPrev realiza apenas uma triagem visual com apoio de tecnologia. " +
                    "Este documento não substitui consulta médica, não confirma diagnóstico e não descarta doenças de pele."

        drawMultilineText(canvas, aviso, 92f, 694f, normalPaint, 74)

        // RODAPÉ
        canvas.drawText("Gerado pelo aplicativo DermaPrev", 40f, pageHeight - 28f, smallPaint)
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