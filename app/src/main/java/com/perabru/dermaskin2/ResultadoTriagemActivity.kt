package com.perabru.dermaskin2

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ResultadoTriagemActivity : AppCompatActivity() {

    private lateinit var txtAreaResultado: TextView
    private lateinit var imgResultadoFoto: ImageView

    private lateinit var txtRiscoTitulo: TextView
    private lateinit var txtRiscoPercentual: TextView
    private lateinit var txtRiscoDescricao: TextView
    private lateinit var txtResumoClinico: TextView

    private lateinit var btnVerAbcde: Button
    private lateinit var btnGerarRelatorio: Button
    private lateinit var btnNovaAnalise: Button


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

    // Resultado apresentado na tela
    private var riscoTitulo: String = ""
    private var riscoPercentual: String = ""
    private var riscoDescricao: String = ""

    // Dados reais retornados pela IA
    private var classificacaoIA: String = ""
    private var confiancaIA: Float = 0f
    private var scoreSuspeitaIA: Float = 0f
    private var confiancaClassificacaoPercentual: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_triagem)

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

        // Questionário da lesão
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

        // Resultado já preparado pela ProcessamentoiaActivity
        riscoTitulo =
            intent.getStringExtra("riscoTitulo")
                ?: "Resultado não calculado"

        riscoPercentual =
            intent.getStringExtra("riscoPercentual")
                ?: "--"

        riscoDescricao =
            intent.getStringExtra("riscoDescricao")
                ?: "Não foi possível gerar a descrição da triagem."

        // Dados técnicos reais da IA
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

        txtAreaResultado =
            findViewById(R.id.txtAreaResultado)

        imgResultadoFoto =
            findViewById(R.id.imgResultadoFoto)

        txtRiscoTitulo =
            findViewById(R.id.txtRiscoTitulo)

        txtRiscoPercentual =
            findViewById(R.id.txtRiscoPercentual)

        txtRiscoDescricao =
            findViewById(R.id.txtRiscoDescricao)

        txtResumoClinico =
            findViewById(R.id.txtResumoClinico)

        btnVerAbcde =
            findViewById(R.id.btnVerAbcde)

        btnGerarRelatorio =
            findViewById(R.id.btnGerarRelatorio)

        btnNovaAnalise =
            findViewById(R.id.btnNovaAnalise)

    }

    private fun configurarTela() {

        txtAreaResultado.text =
            "Área da lesão: $areaSelecionada"

        /*
         * Agora o percentual recebido representa
         * especificamente o score visual da classe suspeita.
         */
        txtRiscoTitulo.text =
            riscoTitulo

        txtRiscoPercentual.text =
            riscoPercentual

        txtRiscoDescricao.text =
            riscoDescricao

        txtResumoClinico.text =
            "Tempo da lesão: $tempoLesao\n\n" +
                    "Mudança de tamanho: $mudancaTamanho\n" +
                    "Mudança de cor: $mudancaCor\n" +
                    "Coceira: $coceira\n" +
                    "Sangramento: $sangramento\n" +
                    "Dor: $dor\n" +
                    "Formato irregular: $formatoIrregular"

        carregarImagem()
        configurarCorResultado()
    }

    private fun carregarImagem() {

        if (imagePath.isBlank()) {
            return
        }

        val file =
            File(imagePath)

        if (file.exists()) {

            val bitmap =
                BitmapFactory.decodeFile(
                    imagePath
                )

            imgResultadoFoto.setImageBitmap(
                bitmap
            )
        }
    }

    /*
     * Como agora não estamos mais trabalhando com
     * "Risco alto / médio / baixo",
     * a cor acompanha o score visual da IA.
     */
    private fun configurarCorResultado() {

        val percentual =
            (scoreSuspeitaIA * 100)
                .toInt()
                .coerceIn(0, 100)

        if (percentual >= 50) {

            txtRiscoPercentual.setTextColor(
                android.graphics.Color.parseColor(
                    "#D9534F"
                )
            )

        } else {

            txtRiscoPercentual.setTextColor(
                android.graphics.Color.parseColor(
                    "#5CB85C"
                )
            )
        }
    }

    private fun configurarCliques() {

        btnVerAbcde.setOnClickListener {

            val intent =
                Intent(
                    this,
                    MetodoAbcdeActivity::class.java
                )

            enviarDados(intent)

            startActivity(intent)
        }

        btnGerarRelatorio.setOnClickListener {

            val intent =
                Intent(
                    this,
                    RelatorioProfissionalActivity::class.java
                )

            enviarDados(intent)

            startActivity(intent)
        }

        btnNovaAnalise.setOnClickListener {

            val intent =
                Intent(
                    this,
                    AnaliseActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP

            startActivity(intent)

            finish()
        }

    }

    /*
     * Mantemos todos os dados disponíveis
     * para ABCDE e relatório profissional.
     */
    private fun enviarDados(intent: Intent) {

        intent.putExtra(
            "areaSelecionada",
            areaSelecionada
        )

        intent.putExtra(
            "imagePath",
            imagePath
        )

        // Questionário da lesão
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

        // Resultado apresentado
        intent.putExtra(
            "riscoTitulo",
            riscoTitulo
        )

        intent.putExtra(
            "riscoPercentual",
            riscoPercentual
        )

        intent.putExtra(
            "riscoDescricao",
            riscoDescricao
        )

        // Dados reais da IA
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
            scoreSuspeitaIA
        )

        intent.putExtra(
            "confiancaClassificacaoPercentual",
            confiancaClassificacaoPercentual
        )
    }
}