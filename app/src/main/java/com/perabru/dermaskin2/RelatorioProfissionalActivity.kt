package com.perabru.dermaskin2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RelatorioProfissionalActivity : AppCompatActivity() {

    private lateinit var txtAreaRelatorio: TextView
    private lateinit var imgRelatorioFoto: ImageView

    private lateinit var btnExportarRelatorioPdf: Button
    private lateinit var btnHistoricoRelatorio: Button
    private lateinit var btnVoltarRelatorio: Button

    private var areaSelecionada: String = ""
    private var imagePath: String = ""

    // Questionário da lesão
    private var tempoLesao: String = ""
    private var mudancaTamanho: String = ""
    private var mudancaCor: String = ""
    private var coceira: String = ""
    private var sangramento: String = ""
    private var dor: String = ""
    private var formatoIrregular: String = ""

    // Resultado da triagem
    private var riscoTitulo: String = ""
    private var riscoPercentual: String = ""
    private var riscoDescricao: String = ""
    private var nivelAtencao: String = ""

    // Dados da IA
    private var classificacaoIA: String = ""
    private var confiancaIA: Float = 0f
    private var scoreSuspeitaIA: Float = 0f
    private var confiancaClassificacaoPercentual: Int = 0

    private var pendingPdfBytes: ByteArray? = null

    private val createPdfLauncher =
        registerForActivityResult(
            ActivityResultContracts.CreateDocument("application/pdf")
        ) { uri ->

            if (uri != null) {

                try {

                    val bytes = pendingPdfBytes

                    if (bytes == null) {

                        Toast.makeText(
                            this,
                            "Nenhum PDF foi gerado.",
                            Toast.LENGTH_LONG
                        ).show()

                        return@registerForActivityResult
                    }

                    contentResolver
                        .openOutputStream(uri)
                        ?.use { outputStream ->

                            outputStream.write(bytes)
                        }

                    Toast.makeText(
                        this,
                        "Relatório salvo com sucesso!",
                        Toast.LENGTH_LONG
                    ).show()

                } catch (e: Exception) {

                    Toast.makeText(
                        this,
                        "Erro ao salvar PDF: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_relatorio_profissional
        )

        recuperarDados()
        iniciarComponentes()
        configurarTela()
        configurarCliques()
    }

    private fun recuperarDados() {

        areaSelecionada =
            intent.getStringExtra("areaSelecionada")
                ?: "Não informada"

        imagePath =
            intent.getStringExtra("imagePath")
                ?: ""

        tempoLesao =
            intent.getStringExtra("tempoLesao")
                ?: "Não informado"

        mudancaTamanho =
            intent.getStringExtra("mudancaTamanho")
                ?: "Não informado"

        mudancaCor =
            intent.getStringExtra("mudancaCor")
                ?: "Não informado"

        coceira =
            intent.getStringExtra("coceira")
                ?: "Não informado"

        sangramento =
            intent.getStringExtra("sangramento")
                ?: "Não informado"

        dor =
            intent.getStringExtra("dor")
                ?: "Não informado"

        formatoIrregular =
            intent.getStringExtra("formatoIrregular")
                ?: "Não informado"

        riscoTitulo =
            intent.getStringExtra("riscoTitulo")
                ?: "Resultado não calculado"

        riscoPercentual =
            intent.getStringExtra("riscoPercentual")
                ?: "--"

        riscoDescricao =
            intent.getStringExtra("riscoDescricao")
                ?: "Sem descrição disponível."

        nivelAtencao =
            intent.getStringExtra("nivelAtencao")
                ?: riscoTitulo

        classificacaoIA =
            intent.getStringExtra("classificacaoIA")
                ?: "Não informado"

        confiancaIA =
            intent.getFloatExtra(
                "confiancaIA",
                0f
            )

        scoreSuspeitaIA =
            intent.getFloatExtra(
                "scoreSuspeitaIA",
                0f
            )

        confiancaClassificacaoPercentual =
            intent.getIntExtra(
                "confiancaClassificacaoPercentual",
                0
            )
    }

    private fun iniciarComponentes() {

        txtAreaRelatorio =
            findViewById(R.id.txtAreaRelatorio)

        imgRelatorioFoto =
            findViewById(R.id.imgRelatorioFoto)

        btnExportarRelatorioPdf =
            findViewById(R.id.btnExportarRelatorioPdf)

        btnHistoricoRelatorio =
            findViewById(R.id.btnHistoricoRelatorio)

        btnVoltarRelatorio =
            findViewById(R.id.btnVoltarRelatorio)
    }

    private fun configurarTela() {

        txtAreaRelatorio.text =
            "Área da lesão: $areaSelecionada"

        carregarImagem()
    }

    private fun carregarImagem() {

        if (imagePath.isBlank()) {
            return
        }

        val file =
            File(imagePath)

        if (file.exists()) {

            try {

                val bitmap =
                    carregarImagemComOrientacaoCorreta(
                        imagePath
                    )

                imgRelatorioFoto.setImageBitmap(
                    bitmap
                )

            } catch (_: Exception) {

                val bitmap =
                    BitmapFactory.decodeFile(
                        imagePath
                    )

                imgRelatorioFoto.setImageBitmap(
                    bitmap
                )
            }
        }
    }

    private fun configurarCliques() {

        btnExportarRelatorioPdf.setOnClickListener {

            exportarPdf()
        }

        btnHistoricoRelatorio.setOnClickListener {

            val intent =
                Intent(
                    this,
                    HistoricoAnalisesActivity::class.java
                )

            startActivity(intent)
        }

        /*
         * Volta diretamente para a tela inicial
         * da análise e remove as telas intermediárias.
         */
        btnVoltarRelatorio.setOnClickListener {

            val intent =
                Intent(
                    this,
                    AnaliseActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)

            finish()
        }
    }

    private fun exportarPdf() {

        try {

            pendingPdfBytes =
                gerarPdfBytes()

            val fileName =
                "relatorio_dermaprev_${System.currentTimeMillis()}.pdf"

            createPdfLauncher.launch(
                fileName
            )

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Erro ao gerar PDF: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun gerarPdfBytes(): ByteArray {

        val pdfDocument =
            PdfDocument()

        val pageWidth = 595
        val pageHeight = 842

        val pageInfo =
            PdfDocument.PageInfo
                .Builder(
                    pageWidth,
                    pageHeight,
                    1
                )
                .create()

        val page =
            pdfDocument.startPage(
                pageInfo
            )

        desenharPdf(
            page.canvas
        )

        pdfDocument.finishPage(
            page
        )

        val outputStream =
            ByteArrayOutputStream()

        pdfDocument.writeTo(
            outputStream
        )

        pdfDocument.close()

        return outputStream.toByteArray()
    }

    private fun desenharPdf(
        canvas: Canvas
    ) {

        val pageWidth = 595f
        val pageHeight = 842f

        val margin = 38f
        val contentWidth =
            pageWidth - (margin * 2)

        val right =
            pageWidth - margin

        canvas.drawColor(
            Color.parseColor("#F6FBFF")
        )

        val branco =
            Paint().apply {
                color = Color.WHITE
                style = Paint.Style.FILL
                isAntiAlias = true
            }

        val azulSuave =
            Paint().apply {
                color =
                    Color.parseColor("#F0F8FF")

                style =
                    Paint.Style.FILL

                isAntiAlias = true
            }

        val borda =
            Paint().apply {

                color =
                    Color.parseColor("#D9EAF5")

                style =
                    Paint.Style.STROKE

                strokeWidth =
                    1.2f

                isAntiAlias = true
            }

        val azulEscuro =
            Color.parseColor("#063B78")

        val azulClaro =
            Color.parseColor("#1DB9D2")

        val titulo =
            Paint().apply {

                color = azulEscuro

                textSize = 20f

                typeface =
                    Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                    )

                isAntiAlias = true
            }

        val secao =
            Paint().apply {

                color = azulEscuro

                textSize = 13.5f

                typeface =
                    Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                    )

                isAntiAlias = true
            }

        val normal =
            Paint().apply {

                color =
                    Color.parseColor("#173B70")

                textSize = 10f

                isAntiAlias = true
            }

        val pequeno =
            Paint().apply {

                color =
                    Color.parseColor("#60738A")

                textSize = 8.5f

                isAntiAlias = true
            }

        val destaque =
            Paint().apply {

                color =
                    corNivelAtencao()

                textSize = 22f

                typeface =
                    Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                    )

                isAntiAlias = true
            }

        val percentualPaint =
            Paint().apply {

                color = azulEscuro

                textSize = 30f

                typeface =
                    Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                    )

                isAntiAlias = true
            }

        /*
         * HEADER
         */
        canvas.drawRoundRect(
            margin,
            28f,
            right,
            105f,
            18f,
            18f,
            branco
        )

        canvas.drawRoundRect(
            margin,
            28f,
            right,
            105f,
            18f,
            18f,
            borda
        )

        val logo =
            BitmapFactory.decodeResource(
                resources,
                R.drawable.logo_dermaprev
            )

        val logoScaled =
            Bitmap.createScaledBitmap(
                logo,
                36,
                36,
                true
            )

        canvas.drawBitmap(
            logoScaled,
            55f,
            46f,
            null
        )

        val dermaPaint =
            Paint().apply {

                color = azulEscuro

                textSize = 23f

                typeface =
                    Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                    )

                isAntiAlias = true
            }

        val prevPaint =
            Paint().apply {

                color = azulClaro

                textSize = 23f

                typeface =
                    Typeface.create(
                        Typeface.DEFAULT,
                        Typeface.BOLD
                    )

                isAntiAlias = true
            }

        canvas.drawText(
            "Derma",
            101f,
            70f,
            dermaPaint
        )

        val larguraDerma =
            dermaPaint.measureText(
                "Derma"
            )

        canvas.drawText(
            "Prev",
            101f + larguraDerma,
            70f,
            prevPaint
        )

        canvas.drawText(
            "Relatório de triagem de lesão cutânea",
            101f,
            88f,
            pequeno
        )

        val data =
            SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale("pt", "BR")
            ).format(
                Date()
            )

        val dataTexto =
            "Gerado em $data"

        canvas.drawText(
            dataTexto,
            right - pequeno.measureText(dataTexto) - 15f,
            88f,
            pequeno
        )

        var y = 132f

        /*
         * TÍTULO
         */
        canvas.drawText(
            "Resultado da triagem",
            margin,
            y,
            titulo
        )

        y += 20f

        /*
         * CARD RESULTADO
         */
        val cardResultadoTop =
            y

        val cardResultadoBottom =
            y + 105f

        canvas.drawRoundRect(
            margin,
            cardResultadoTop,
            right,
            cardResultadoBottom,
            18f,
            18f,
            branco
        )

        canvas.drawRoundRect(
            margin,
            cardResultadoTop,
            right,
            cardResultadoBottom,
            18f,
            18f,
            borda
        )

        canvas.drawText(
            "Nível de atenção",
            margin + 18f,
            y + 25f,
            pequeno
        )

        canvas.drawText(
            nivelAtencao,
            margin + 18f,
            y + 52f,
            destaque
        )

        canvas.drawText(
            "Suspeita visual de lesões cutâneas",
            right - 195f,
            y + 25f,
            pequeno
        )

        val larguraPercentual =
            percentualPaint.measureText(
                riscoPercentual
            )

        canvas.drawText(
            riscoPercentual,
            right - larguraPercentual - 18f,
            y + 63f,
            percentualPaint
        )

        canvas.drawText(
            "Score visual do classificador de imagem",
            right - 195f,
            y + 82f,
            pequeno
        )

        y =
            cardResultadoBottom + 18f

        /*
         * CARD IMAGEM
         */
        val imagemCardTop =
            y

        val imagemCardBottom =
            imagemCardTop + 190f

        canvas.drawRoundRect(
            margin,
            imagemCardTop,
            right,
            imagemCardBottom,
            18f,
            18f,
            branco
        )

        canvas.drawRoundRect(
            margin,
            imagemCardTop,
            right,
            imagemCardBottom,
            18f,
            18f,
            borda
        )

        canvas.drawText(
            "Imagem analisada",
            margin + 18f,
            imagemCardTop + 28f,
            secao
        )

        canvas.drawText(
            "Área da lesão: $areaSelecionada",
            margin + 18f,
            imagemCardTop + 52f,
            normal
        )

        val file =
            File(imagePath)

        if (file.exists()) {

            try {

                val bitmap =
                    carregarImagemComOrientacaoCorreta(
                        imagePath
                    )

                desenharImagemCentralizada(
                    canvas = canvas,
                    bitmap = bitmap,
                    left = margin + 18f,
                    top = imagemCardTop + 68f,
                    width = 105f,
                    height = 105f
                )

            } catch (_: Exception) {
            }
        }

        val textoImagem =
            "A imagem apresentada corresponde à fotografia utilizada pelo aplicativo durante a análise visual."

        drawWrappedText(
            canvas = canvas,
            text = textoImagem,
            x = margin + 145f,
            startY = imagemCardTop + 92f,
            paint = normal,
            maxWidth = contentWidth - 170f,
            lineSpacing = 5f
        )

        y =
            imagemCardBottom + 18f

        /*
         * CARD INFORMAÇÕES
         */
        val infoTop =
            y

        val infoBottom =
            infoTop + 175f

        canvas.drawRoundRect(
            margin,
            infoTop,
            right,
            infoBottom,
            18f,
            18f,
            branco
        )

        canvas.drawRoundRect(
            margin,
            infoTop,
            right,
            infoBottom,
            18f,
            18f,
            borda
        )

        canvas.drawText(
            "Informações relatadas pelo usuário",
            margin + 18f,
            infoTop + 28f,
            secao
        )

        val dados =
            "Tempo da lesão: $tempoLesao\n" +
                    "Mudança de tamanho: $mudancaTamanho\n" +
                    "Mudança de cor: $mudancaCor\n" +
                    "Coceira: $coceira\n" +
                    "Sangramento: $sangramento\n" +
                    "Dor: $dor\n" +
                    "Formato irregular: $formatoIrregular"

        drawWrappedText(
            canvas = canvas,
            text = dados,
            x = margin + 18f,
            startY = infoTop + 53f,
            paint = normal,
            maxWidth = contentWidth - 36f,
            lineSpacing = 4f
        )

        y =
            infoBottom + 18f

        /*
         * CARD AVISO
         */
        val avisoTop =
            y

        val avisoBottom =
            avisoTop + 102f

        canvas.drawRoundRect(
            margin,
            avisoTop,
            right,
            avisoBottom,
            18f,
            18f,
            azulSuave
        )

        canvas.drawRoundRect(
            margin,
            avisoTop,
            right,
            avisoBottom,
            18f,
            18f,
            borda
        )

        canvas.drawText(
            "Orientação importante",
            margin + 18f,
            avisoTop + 27f,
            secao
        )

        val aviso =
            "Este documento foi gerado para apoiar a comunicação com um profissional de saúde. " +
                    "O DermaPrev realiza uma triagem auxiliar e não confirma nem descarta câncer de pele. " +
                    "A interpretação clínica deve ser realizada por profissional habilitado."

        drawWrappedText(
            canvas = canvas,
            text = aviso,
            x = margin + 18f,
            startY = avisoTop + 50f,
            paint = normal,
            maxWidth = contentWidth - 36f,
            lineSpacing = 4f
        )

        /*
         * RODAPÉ
         */
        val rodape =
            "DermaPrev • Documento de apoio à triagem"

        canvas.drawText(
            rodape,
            margin,
            pageHeight - 25f,
            pequeno
        )
    }

    /*
     * Agora as linhas são quebradas pela largura REAL
     * do texto e não pela quantidade de caracteres.
     */
    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        x: Float,
        startY: Float,
        paint: Paint,
        maxWidth: Float,
        lineSpacing: Float
    ): Float {

        var y =
            startY

        val fontMetrics =
            paint.fontMetrics

        val lineHeight =
            (fontMetrics.descent - fontMetrics.ascent) +
                    lineSpacing

        val paragrafos =
            text.split("\n")

        for (paragrafo in paragrafos) {

            if (paragrafo.isBlank()) {

                y += lineHeight
                continue
            }

            val palavras =
                paragrafo.split(" ")

            var linhaAtual =
                ""

            for (palavra in palavras) {

                val linhaTeste =
                    if (linhaAtual.isBlank()) {
                        palavra
                    } else {
                        "$linhaAtual $palavra"
                    }

                if (
                    paint.measureText(linhaTeste) <=
                    maxWidth
                ) {

                    linhaAtual =
                        linhaTeste

                } else {

                    if (linhaAtual.isNotBlank()) {

                        canvas.drawText(
                            linhaAtual,
                            x,
                            y,
                            paint
                        )

                        y += lineHeight
                    }

                    linhaAtual =
                        palavra
                }
            }

            if (linhaAtual.isNotBlank()) {

                canvas.drawText(
                    linhaAtual,
                    x,
                    y,
                    paint
                )

                y += lineHeight
            }
        }

        return y
    }

    /*
     * Mostra a imagem sem esticar.
     * Faz o equivalente ao centerCrop do ImageView.
     */
    private fun desenharImagemCentralizada(
        canvas: Canvas,
        bitmap: Bitmap,
        left: Float,
        top: Float,
        width: Float,
        height: Float
    ) {

        val proporcaoDestino =
            width / height

        val proporcaoImagem =
            bitmap.width.toFloat() /
                    bitmap.height.toFloat()

        val srcLeft: Int
        val srcTop: Int
        val srcRight: Int
        val srcBottom: Int

        if (
            proporcaoImagem >
            proporcaoDestino
        ) {

            val novaLargura =
                (
                        bitmap.height *
                                proporcaoDestino
                        ).toInt()

            srcLeft =
                (bitmap.width - novaLargura) / 2

            srcRight =
                srcLeft + novaLargura

            srcTop = 0
            srcBottom = bitmap.height

        } else {

            val novaAltura =
                (
                        bitmap.width /
                                proporcaoDestino
                        ).toInt()

            srcTop =
                (bitmap.height - novaAltura) / 2

            srcBottom =
                srcTop + novaAltura

            srcLeft = 0
            srcRight = bitmap.width
        }

        val origem =
            android.graphics.Rect(
                srcLeft,
                srcTop,
                srcRight,
                srcBottom
            )

        val destino =
            RectF(
                left,
                top,
                left + width,
                top + height
            )

        canvas.drawBitmap(
            bitmap,
            origem,
            destino,
            null
        )
    }

    /*
     * Carrega a foto respeitando a orientação EXIF.
     * Também reduz imagens muito grandes antes de
     * colocá-las na tela/PDF.
     */
    private fun carregarImagemComOrientacaoCorreta(
        caminho: String
    ): Bitmap {

        val optionsBounds =
            BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

        BitmapFactory.decodeFile(
            caminho,
            optionsBounds
        )

        var sampleSize = 1

        while (
            optionsBounds.outWidth / sampleSize > 1200 ||
            optionsBounds.outHeight / sampleSize > 1200
        ) {

            sampleSize *= 2
        }

        val options =
            BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }

        val bitmap =
            BitmapFactory.decodeFile(
                caminho,
                options
            ) ?: throw IllegalStateException(
                "Não foi possível carregar a imagem."
            )

        val exif =
            ExifInterface(
                caminho
            )

        val orientacao =
            exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

        val matrix =
            Matrix()

        when (orientacao) {

            ExifInterface.ORIENTATION_ROTATE_90 ->
                matrix.postRotate(90f)

            ExifInterface.ORIENTATION_ROTATE_180 ->
                matrix.postRotate(180f)

            ExifInterface.ORIENTATION_ROTATE_270 ->
                matrix.postRotate(270f)

            ExifInterface.ORIENTATION_FLIP_HORIZONTAL ->
                matrix.postScale(-1f, 1f)

            ExifInterface.ORIENTATION_FLIP_VERTICAL ->
                matrix.postScale(1f, -1f)
        }

        return if (
            !matrix.isIdentity
        ) {

            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )

        } else {

            bitmap
        }
    }

    private fun corNivelAtencao(): Int {

        return when {

            nivelAtencao.contains(
                "elevado",
                ignoreCase = true
            ) -> {

                Color.parseColor(
                    "#D9534F"
                )
            }

            nivelAtencao.contains(
                "moderado",
                ignoreCase = true
            ) -> {

                Color.parseColor(
                    "#E6A23C"
                )
            }

            nivelAtencao.contains(
                "baixo",
                ignoreCase = true
            ) -> {

                Color.parseColor(
                    "#28A745"
                )
            }

            else -> {

                Color.parseColor(
                    "#063B78"
                )
            }
        }
    }
}