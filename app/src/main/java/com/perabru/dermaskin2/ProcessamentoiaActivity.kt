package com.perabru.dermaskin2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.min
import kotlin.math.roundToInt

class ProcessamentoiaActivity : AppCompatActivity() {

    private lateinit var txtAreaProcessamento: TextView
    private lateinit var txtStatusProcessamento: TextView
    private lateinit var progressProcessamento: ProgressBar

    private var areaSelecionada: String = ""
    private var imagePath: String = ""

    // Respostas do questionário da lesão
    private var tempoLesao: String = ""
    private var mudancaTamanho: String = ""
    private var mudancaCor: String = ""
    private var coceira: String = ""
    private var sangramento: String = ""
    private var dor: String = ""
    private var formatoIrregular: String = ""

    // Perfil de saúde salvo no Firebase
    private var faixaEtaria: String = "Não informado"
    private var tipoPele: String = "Não informado"
    private var exposicaoSolar: String = "Não informado"
    private var usoProtetor: String = "Não informado"
    private var historicoFamiliar: String = "Não informado"
    private var lesoesAnteriores: String = "Não informado"
    private var trabalhoAoSol: String = "Não informado"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_processamentoia)

        recuperarDados()
        iniciarComponentes()
        configurarTela()

        buscarPerfilSaude {
            iniciarProcessamentoIA()
        }
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
    }

    private fun iniciarComponentes() {

        txtAreaProcessamento =
            findViewById(R.id.txtAreaProcessamento)

        txtStatusProcessamento =
            findViewById(R.id.txtStatusProcessamento)

        progressProcessamento =
            findViewById(R.id.progressProcessamento)
    }

    private fun configurarTela() {

        txtAreaProcessamento.text =
            "Área da lesão: $areaSelecionada"

        txtStatusProcessamento.text =
            "Preparando análise da imagem..."
    }

    private fun buscarPerfilSaude(
        aoConcluir: () -> Unit
    ) {

        txtStatusProcessamento.text =
            "Carregando informações do perfil..."

        val usuarioAtual =
            FirebaseAuth
                .getInstance()
                .currentUser

        if (usuarioAtual == null) {

            aoConcluir()
            return
        }

        val referenciaPerfil =
            FirebaseDatabase
                .getInstance()
                .reference
                .child("usuarios")
                .child(usuarioAtual.uid)
                .child("perfilSaude")

        referenciaPerfil
            .get()
            .addOnSuccessListener { snapshot ->

                faixaEtaria =
                    snapshot
                        .child("faixaEtaria")
                        .getValue(String::class.java)
                        ?: "Não informado"

                tipoPele =
                    snapshot
                        .child("tipoPele")
                        .getValue(String::class.java)
                        ?: "Não informado"

                exposicaoSolar =
                    snapshot
                        .child("exposicaoSolar")
                        .getValue(String::class.java)
                        ?: "Não informado"

                usoProtetor =
                    snapshot
                        .child("usoProtetor")
                        .getValue(String::class.java)
                        ?: "Não informado"

                historicoFamiliar =
                    snapshot
                        .child("historicoFamiliar")
                        .getValue(String::class.java)
                        ?: "Não informado"

                lesoesAnteriores =
                    snapshot
                        .child("lesoesAnteriores")
                        .getValue(String::class.java)
                        ?: "Não informado"

                trabalhoAoSol =
                    snapshot
                        .child("trabalhoAoSol")
                        .getValue(String::class.java)
                        ?: "Não informado"

                aoConcluir()
            }
            .addOnFailureListener {

                aoConcluir()
            }
    }

    private fun iniciarProcessamentoIA() {

        if (imagePath.isBlank()) {

            Toast.makeText(
                this,
                "Imagem não encontrada.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        val arquivo =
            File(imagePath)

        if (!arquivo.exists()) {

            Toast.makeText(
                this,
                "Arquivo da imagem não encontrado.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        Thread {

            try {

                atualizarStatus(
                    "Carregando modelo de inteligência artificial..."
                )

                val interpreter =
                    Interpreter(
                        carregarModelo()
                    )

                val labels =
                    carregarLabels()

                val inputTensor =
                    interpreter.getInputTensor(0)

                val inputShape =
                    inputTensor.shape()

                if (inputShape.size != 4) {

                    throw IllegalStateException(
                        "Formato de entrada do modelo não reconhecido."
                    )
                }

                val alturaModelo =
                    inputShape[1]

                val larguraModelo =
                    inputShape[2]

                if (inputTensor.dataType() != DataType.FLOAT32) {

                    throw IllegalStateException(
                        "O modelo não utiliza entrada FLOAT32."
                    )
                }

                atualizarStatus(
                    "Preparando imagem capturada..."
                )

                val bitmapOriginal =
                    carregarImagemComOrientacaoCorreta(
                        imagePath
                    )

                val bitmapQuadrado =
                    recortarCentro(
                        bitmapOriginal
                    )

                val bitmapModelo =
                    Bitmap.createScaledBitmap(
                        bitmapQuadrado,
                        larguraModelo,
                        alturaModelo,
                        true
                    )

                atualizarStatus(
                    "Analisando características visuais..."
                )

                val entrada =
                    prepararImagemParaModelo(
                        bitmapModelo,
                        larguraModelo,
                        alturaModelo
                    )

                val outputShape =
                    interpreter
                        .getOutputTensor(0)
                        .shape()

                val quantidadeClasses =
                    outputShape.last()

                val resultado =
                    Array(1) {
                        FloatArray(
                            quantidadeClasses
                        )
                    }

                interpreter.run(
                    entrada,
                    resultado
                )

                interpreter.close()

                atualizarStatus(
                    "Interpretando resultado da análise..."
                )

                val probabilidades =
                    resultado[0]

                var indiceMaior = 0

                for (i in probabilidades.indices) {

                    if (
                        probabilidades[i] >
                        probabilidades[indiceMaior]
                    ) {

                        indiceMaior = i
                    }
                }

                val classificacao =
                    labels.getOrElse(
                        indiceMaior
                    ) {

                        "Classe $indiceMaior"
                    }

                val confianca =
                    probabilidades[indiceMaior]

                val indiceSuspeita =
                    labels.indexOfFirst { label ->

                        val texto =
                            label.lowercase()

                        texto.contains("suspeita") &&
                                !texto.contains("não suspeita") &&
                                !texto.contains("nao suspeita")
                    }

                val scoreSuspeita =
                    if (
                        indiceSuspeita >= 0 &&
                        indiceSuspeita < probabilidades.size
                    ) {

                        probabilidades[indiceSuspeita]
                            .coerceIn(
                                0f,
                                1f
                            )

                    } else {

                        0f
                    }

                runOnUiThread {

                    txtStatusProcessamento.text =
                        "Análise concluída."

                    continuarParaResultado(
                        classificacao,
                        confianca,
                        scoreSuspeita
                    )
                }

            } catch (e: Exception) {

                e.printStackTrace()

                runOnUiThread {

                    txtStatusProcessamento.text =
                        "Não foi possível concluir a análise."

                    Toast.makeText(
                        this,
                        "Erro na IA: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }.start()
    }

    private fun carregarModelo(): ByteBuffer {

        val bytes =
            assets
                .open("model_unquant.tflite")
                .use {
                    it.readBytes()
                }

        val buffer =
            ByteBuffer.allocateDirect(
                bytes.size
            )

        buffer.order(
            ByteOrder.nativeOrder()
        )

        buffer.put(
            bytes
        )

        buffer.rewind()

        return buffer
    }

    private fun carregarLabels(): List<String> {

        return assets
            .open("labels.txt")
            .bufferedReader()
            .useLines { linhas ->

                linhas
                    .map { linha ->

                        linha
                            .trim()
                            .replace(
                                Regex("^\\d+\\s*"),
                                ""
                            )
                    }
                    .filter {
                        it.isNotBlank()
                    }
                    .toList()
            }
    }

    private fun prepararImagemParaModelo(
        bitmap: Bitmap,
        largura: Int,
        altura: Int
    ): ByteBuffer {

        val buffer =
            ByteBuffer.allocateDirect(
                4 *
                        largura *
                        altura *
                        3
            )

        buffer.order(
            ByteOrder.nativeOrder()
        )

        val pixels =
            IntArray(
                largura *
                        altura
            )

        bitmap.getPixels(
            pixels,
            0,
            largura,
            0,
            0,
            largura,
            altura
        )

        for (pixel in pixels) {

            val vermelho =
                (pixel shr 16) and 0xFF

            val verde =
                (pixel shr 8) and 0xFF

            val azul =
                pixel and 0xFF

            buffer.putFloat(
                (vermelho / 127.5f) - 1.0f
            )

            buffer.putFloat(
                (verde / 127.5f) - 1.0f
            )

            buffer.putFloat(
                (azul / 127.5f) - 1.0f
            )
        }

        buffer.rewind()

        return buffer
    }

    private fun carregarImagemComOrientacaoCorreta(
        caminho: String
    ): Bitmap {

        val bitmap =
            BitmapFactory.decodeFile(
                caminho
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
                matrix.postRotate(
                    90f
                )

            ExifInterface.ORIENTATION_ROTATE_180 ->
                matrix.postRotate(
                    180f
                )

            ExifInterface.ORIENTATION_ROTATE_270 ->
                matrix.postRotate(
                    270f
                )

            ExifInterface.ORIENTATION_FLIP_HORIZONTAL ->
                matrix.postScale(
                    -1f,
                    1f
                )

            ExifInterface.ORIENTATION_FLIP_VERTICAL ->
                matrix.postScale(
                    1f,
                    -1f
                )
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

    private fun recortarCentro(
        bitmap: Bitmap
    ): Bitmap {

        val tamanho =
            min(
                bitmap.width,
                bitmap.height
            )

        val x =
            (bitmap.width - tamanho) / 2

        val y =
            (bitmap.height - tamanho) / 2

        return Bitmap.createBitmap(
            bitmap,
            x,
            y,
            tamanho,
            tamanho
        )
    }

    private fun atualizarStatus(
        mensagem: String
    ) {

        runOnUiThread {

            txtStatusProcessamento.text =
                mensagem
        }
    }

    private fun calcularNivelAtencao(
        scoreSuspeita: Float
    ): Pair<String, String> {

        var sinaisLesao = 0
        var fatoresPerfil = 0

        if (
            mudancaTamanho.equals(
                "Sim",
                ignoreCase = true
            )
        ) {
            sinaisLesao++
        }

        if (
            mudancaCor.equals(
                "Sim",
                ignoreCase = true
            )
        ) {
            sinaisLesao++
        }

        if (
            sangramento.equals(
                "Sim",
                ignoreCase = true
            )
        ) {
            sinaisLesao++
        }

        if (
            formatoIrregular.equals(
                "Sim",
                ignoreCase = true
            )
        ) {
            sinaisLesao++
        }

        if (
            dor.equals(
                "Sim",
                ignoreCase = true
            ) ||
            dor.equals(
                "Às vezes",
                ignoreCase = true
            )
        ) {
            sinaisLesao++
        }

        if (
            historicoFamiliar.equals(
                "Sim",
                ignoreCase = true
            )
        ) {
            fatoresPerfil++
        }

        if (
            lesoesAnteriores.equals(
                "Sim",
                ignoreCase = true
            )
        ) {
            fatoresPerfil++
        }

        if (
            exposicaoSolar.contains(
                "alta",
                ignoreCase = true
            ) ||
            exposicaoSolar.contains(
                "elevada",
                ignoreCase = true
            )
        ) {
            fatoresPerfil++
        }

        if (
            trabalhoAoSol.equals(
                "Sim",
                ignoreCase = true
            )
        ) {
            fatoresPerfil++
        }

        val percentualIA =
            (scoreSuspeita * 100)
                .roundToInt()
                .coerceIn(
                    0,
                    100
                )

        val nivel: String
        val justificativa: String

        when {

            percentualIA >= 50 -> {

                nivel =
                    "Nível de atenção elevado"

                justificativa =
                    "A análise visual apresentou maior associação com a classe suspeita. " +
                            "Os dados informados no questionário e no perfil de saúde também foram considerados na orientação final."
            }

            sinaisLesao >= 3 -> {

                nivel =
                    "Nível de atenção elevado"

                justificativa =
                    "Embora a análise visual tenha apresentado menor associação com a classe suspeita, " +
                            "foram relatados vários sinais de atenção relacionados à evolução ou às características da lesão."
            }

            sinaisLesao >= 2 ||
                    (
                            sinaisLesao >= 1 &&
                                    fatoresPerfil >= 2
                            ) -> {

                nivel =
                    "Nível de atenção moderado"

                justificativa =
                    "A análise identificou informações que merecem acompanhamento. " +
                            "Foram considerados os sinais relatados sobre a lesão e os fatores informados no perfil de saúde."
            }

            else -> {

                nivel =
                    "Nível de atenção baixo"

                justificativa =
                    "A análise visual apresentou menor associação com a classe suspeita e foram relatados poucos sinais adicionais de atenção. " +
                            "Esse resultado não exclui a necessidade de avaliação profissional caso ocorram alterações na lesão."
            }
        }

        return Pair(
            nivel,
            justificativa
        )
    }

    /*
     * SALVA UMA ÚNICA VEZ NO HISTÓRICO.
     */
    private fun salvarAnaliseNoHistorico(
        nivelAtencao: String,
        percentualSuspeita: Int,
        descricao: String,
        classificacaoIA: String,
        scoreSuspeitaIA: Float,
        aoConcluir: () -> Unit
    ) {

        val usuario =
            FirebaseAuth
                .getInstance()
                .currentUser

        /*
         * Se não houver usuário,
         * continua para o resultado normalmente.
         */
        if (usuario == null) {

            aoConcluir()
            return
        }

        val timestamp =
            System.currentTimeMillis()

        val imagemHistorico =
            copiarImagemParaHistorico(
                timestamp
            )

        val referencia =
            FirebaseDatabase
                .getInstance()
                .reference
                .child("usuarios")
                .child(usuario.uid)
                .child("historicoAnalises")
                .push()

        val dados =
            hashMapOf<String, Any>(

                "id" to
                        (
                                referencia.key
                                    ?: timestamp.toString()
                                ),

                "timestamp" to
                        timestamp,

                "areaSelecionada" to
                        areaSelecionada,

                "nivelAtencao" to
                        nivelAtencao,

                "riscoPercentual" to
                        "$percentualSuspeita%",

                "riscoDescricao" to
                        descricao,

                "imagePath" to
                        imagemHistorico,

                "tempoLesao" to
                        tempoLesao,

                "mudancaTamanho" to
                        mudancaTamanho,

                "mudancaCor" to
                        mudancaCor,

                "coceira" to
                        coceira,

                "sangramento" to
                        sangramento,

                "dor" to
                        dor,

                "formatoIrregular" to
                        formatoIrregular,

                "classificacaoIA" to
                        classificacaoIA,

                /*
                 * Firebase armazena números
                 * adequadamente como Double.
                 */
                "scoreSuspeitaIA" to
                        scoreSuspeitaIA.toDouble()
            )

        referencia
            .setValue(
                dados
            )
            .addOnSuccessListener {

                android.util.Log.d(
                    "DermaPrev",
                    "Análise salva no histórico com sucesso."
                )

                aoConcluir()
            }
            .addOnFailureListener { erro ->

                android.util.Log.e(
                    "DermaPrev",
                    "Erro ao salvar histórico: ${erro.message}"
                )

                /*
                 * Mesmo com erro no Firebase,
                 * não trava a análise.
                 */
                aoConcluir()
            }
    }

    /*
     * EXISTE SOMENTE UMA VEZ.
     */
    private fun copiarImagemParaHistorico(
        timestamp: Long
    ): String {

        if (imagePath.isBlank()) {
            return ""
        }

        val origem =
            File(
                imagePath
            )

        if (!origem.exists()) {
            return ""
        }

        return try {

            val pasta =
                File(
                    filesDir,
                    "historico"
                )

            if (!pasta.exists()) {
                pasta.mkdirs()
            }

            val destino =
                File(
                    pasta,
                    "analise_$timestamp.jpg"
                )

            origem.copyTo(
                destino,
                overwrite = true
            )

            destino.absolutePath

        } catch (e: Exception) {

            android.util.Log.e(
                "DermaPrev",
                "Erro ao copiar imagem: ${e.message}"
            )

            imagePath
        }
    }

    private fun continuarParaResultado(
        classificacaoIA: String,
        confiancaIA: Float,
        scoreSuspeita: Float
    ) {

        val percentualSuspeita =
            (scoreSuspeita * 100)
                .roundToInt()
                .coerceIn(
                    0,
                    100
                )

        val resultadoTriagem =
            calcularNivelAtencao(
                scoreSuspeita
            )

        val nivelAtencao =
            resultadoTriagem.first

        val justificativaTriagem =
            resultadoTriagem.second

        val confiancaClassificacao =
            (confiancaIA * 100)
                .roundToInt()
                .coerceIn(
                    0,
                    100
                )

        val titulo =
            nivelAtencao

        val descricao =
            "Suspeita visual de lesões cutâneas: $percentualSuspeita%.\n\n" +
                    justificativaTriagem +
                    "\n\nEste resultado é uma triagem auxiliar e não representa diagnóstico médico."

        val intent =
            Intent(
                this,
                ResultadoTriagemActivity::class.java
            )

        intent.putExtra(
            "areaSelecionada",
            areaSelecionada
        )

        intent.putExtra(
            "imagePath",
            imagePath
        )

        intent.putExtra(
            "tempoLesao",
            tempoLesao
        )

        intent.putExtra(
            "mudancaTamanho",
            mudancaTamanho
        )

        intent.putExtra(
            "mudancaCor",
            mudancaCor
        )

        intent.putExtra(
            "coceira",
            coceira
        )

        intent.putExtra(
            "sangramento",
            sangramento
        )

        intent.putExtra(
            "dor",
            dor
        )

        intent.putExtra(
            "formatoIrregular",
            formatoIrregular
        )

        intent.putExtra(
            "riscoTitulo",
            titulo
        )

        intent.putExtra(
            "riscoPercentual",
            "$percentualSuspeita%"
        )

        intent.putExtra(
            "riscoDescricao",
            descricao
        )

        intent.putExtra(
            "nivelAtencao",
            nivelAtencao
        )

        intent.putExtra(
            "classificacaoIA",
            classificacaoIA
        )

        intent.putExtra(
            "confiancaIA",
            confiancaIA
        )

        intent.putExtra(
            "scoreSuspeitaIA",
            scoreSuspeita
        )

        intent.putExtra(
            "confiancaClassificacaoPercentual",
            confiancaClassificacao
        )

        /*
         * PRIMEIRO SALVA.
         */
        txtStatusProcessamento.text =
            "Salvando análise no histórico..."

        salvarAnaliseNoHistorico(
            nivelAtencao = nivelAtencao,
            percentualSuspeita = percentualSuspeita,
            descricao = descricao,
            classificacaoIA = classificacaoIA,
            scoreSuspeitaIA = scoreSuspeita
        ) {

            /*
             * SÓ DEPOIS ABRE O RESULTADO.
             */
            startActivity(
                intent
            )

            finish()
        }
    }
}