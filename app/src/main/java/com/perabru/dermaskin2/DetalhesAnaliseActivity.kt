package com.perabru.dermaskin2

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetalhesAnaliseActivity : AppCompatActivity() {

    private lateinit var imgDetalheLesao: ImageView

    private lateinit var txtAreaDetalhe: TextView
    private lateinit var txtDataDetalhe: TextView

    private lateinit var txtNivelAtencaoDetalhe: TextView
    private lateinit var txtPercentualDetalhe: TextView
    private lateinit var txtDescricaoDetalhe: TextView

    private lateinit var txtTempoLesaoDetalhe: TextView
    private lateinit var txtMudancaTamanhoDetalhe: TextView
    private lateinit var txtMudancaCorDetalhe: TextView
    private lateinit var txtCoceiraDetalhe: TextView
    private lateinit var txtSangramentoDetalhe: TextView
    private lateinit var txtDorDetalhe: TextView
    private lateinit var txtFormatoDetalhe: TextView

    private lateinit var txtClassificacaoIADetalhe: TextView

    private lateinit var btnVerAbcdeDetalhe: Button
    private lateinit var btnVoltarDetalhe: ImageButton

    private var timestamp: Long = 0L

    private var areaSelecionada: String = ""
    private var imagePath: String = ""

    private var riscoTitulo: String = ""
    private var nivelAtencao: String = ""
    private var riscoPercentual: String = ""
    private var riscoDescricao: String = ""

    private var tempoLesao: String = ""
    private var mudancaTamanho: String = ""
    private var mudancaCor: String = ""
    private var coceira: String = ""
    private var sangramento: String = ""
    private var dor: String = ""
    private var formatoIrregular: String = ""

    private var classificacaoIA: String = ""
    private var scoreSuspeitaIA: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_detalhes_analise
        )

        recuperarDados()
        iniciarComponentes()
        configurarTela()
        configurarCliques()
    }

    private fun recuperarDados() {

        timestamp =
            intent.getLongExtra(
                "timestamp",
                0L
            )

        areaSelecionada =
            intent.getStringExtra(
                "areaSelecionada"
            ) ?: "Não informada"

        imagePath =
            intent.getStringExtra(
                "imagePath"
            ) ?: ""

        riscoTitulo =
            intent.getStringExtra(
                "riscoTitulo"
            ) ?: "Não informado"

        nivelAtencao =
            intent.getStringExtra(
                "nivelAtencao"
            ) ?: riscoTitulo

        riscoPercentual =
            intent.getStringExtra(
                "riscoPercentual"
            ) ?: "--"

        riscoDescricao =
            intent.getStringExtra(
                "riscoDescricao"
            ) ?: "Orientação não disponível."

        tempoLesao =
            intent.getStringExtra(
                "tempoLesao"
            ) ?: "Não informado"

        mudancaTamanho =
            intent.getStringExtra(
                "mudancaTamanho"
            ) ?: "Não informado"

        mudancaCor =
            intent.getStringExtra(
                "mudancaCor"
            ) ?: "Não informado"

        coceira =
            intent.getStringExtra(
                "coceira"
            ) ?: "Não informado"

        sangramento =
            intent.getStringExtra(
                "sangramento"
            ) ?: "Não informado"

        dor =
            intent.getStringExtra(
                "dor"
            ) ?: "Não informado"

        formatoIrregular =
            intent.getStringExtra(
                "formatoIrregular"
            ) ?: "Não informado"

        classificacaoIA =
            intent.getStringExtra(
                "classificacaoIA"
            ) ?: "Não informado"

        scoreSuspeitaIA =
            intent.getFloatExtra(
                "scoreSuspeitaIA",
                0f
            )
    }

    private fun iniciarComponentes() {

        imgDetalheLesao =
            findViewById(
                R.id.imgDetalheLesao
            )

        txtAreaDetalhe =
            findViewById(
                R.id.txtAreaDetalhe
            )

        txtDataDetalhe =
            findViewById(
                R.id.txtDataDetalhe
            )

        txtNivelAtencaoDetalhe =
            findViewById(
                R.id.txtNivelAtencaoDetalhe
            )

        txtPercentualDetalhe =
            findViewById(
                R.id.txtPercentualDetalhe
            )

        txtDescricaoDetalhe =
            findViewById(
                R.id.txtDescricaoDetalhe
            )

        txtTempoLesaoDetalhe =
            findViewById(
                R.id.txtTempoLesaoDetalhe
            )

        txtMudancaTamanhoDetalhe =
            findViewById(
                R.id.txtMudancaTamanhoDetalhe
            )

        txtMudancaCorDetalhe =
            findViewById(
                R.id.txtMudancaCorDetalhe
            )

        txtCoceiraDetalhe =
            findViewById(
                R.id.txtCoceiraDetalhe
            )

        txtSangramentoDetalhe =
            findViewById(
                R.id.txtSangramentoDetalhe
            )

        txtDorDetalhe =
            findViewById(
                R.id.txtDorDetalhe
            )

        txtFormatoDetalhe =
            findViewById(
                R.id.txtFormatoDetalhe
            )

        txtClassificacaoIADetalhe =
            findViewById(
                R.id.txtClassificacaoIADetalhe
            )

        btnVerAbcdeDetalhe =
            findViewById(
                R.id.btnVerAbcdeDetalhe
            )

        btnVoltarDetalhe =
            findViewById(
                R.id.btnVoltarDetalhe
            )
    }

    private fun configurarTela() {

        txtAreaDetalhe.text =
            "Área analisada: $areaSelecionada"

        txtDataDetalhe.text =
            formatarData(
                timestamp
            )

        txtNivelAtencaoDetalhe.text =
            nivelAtencao

        txtNivelAtencaoDetalhe.setTextColor(
            corNivelAtencao(
                nivelAtencao
            )
        )

        txtPercentualDetalhe.text =
            "Suspeita visual de lesões cutâneas: $riscoPercentual"

        txtDescricaoDetalhe.text =
            riscoDescricao

        txtTempoLesaoDetalhe.text =
            tempoLesao

        txtMudancaTamanhoDetalhe.text =
            mudancaTamanho

        txtMudancaCorDetalhe.text =
            mudancaCor

        txtCoceiraDetalhe.text =
            coceira

        txtSangramentoDetalhe.text =
            sangramento

        txtDorDetalhe.text =
            dor

        txtFormatoDetalhe.text =
            formatoIrregular

        txtClassificacaoIADetalhe.text =
            classificacaoIA

        carregarImagem()
    }

    private fun carregarImagem() {

        if (imagePath.isBlank()) {

            imgDetalheLesao.setImageResource(
                R.drawable.ic_skin_placeholder
            )

            return
        }

        val arquivo =
            File(
                imagePath
            )

        if (!arquivo.exists()) {

            imgDetalheLesao.setImageResource(
                R.drawable.ic_skin_placeholder
            )

            return
        }

        val bitmap =
            BitmapFactory.decodeFile(
                imagePath
            )

        if (bitmap != null) {

            imgDetalheLesao.setImageBitmap(
                bitmap
            )

        } else {

            imgDetalheLesao.setImageResource(
                R.drawable.ic_skin_placeholder
            )
        }
    }

    private fun configurarCliques() {

        btnVoltarDetalhe.setOnClickListener {

            finish()
        }

        btnVerAbcdeDetalhe.setOnClickListener {

            val intent =
                Intent(
                    this,
                    MetodoAbcdeActivity::class.java
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
                "riscoTitulo",
                riscoTitulo
            )

            intent.putExtra(
                "nivelAtencao",
                nivelAtencao
            )

            intent.putExtra(
                "riscoPercentual",
                riscoPercentual
            )

            intent.putExtra(
                "riscoDescricao",
                riscoDescricao
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
                "classificacaoIA",
                classificacaoIA
            )

            intent.putExtra(
                "scoreSuspeitaIA",
                scoreSuspeitaIA
            )

            startActivity(
                intent
            )
        }
    }

    private fun formatarData(
        timestamp: Long
    ): String {

        if (timestamp <= 0L) {

            return "Data da análise não informada"
        }

        val formato =
            SimpleDateFormat(
                "dd/MM/yyyy • HH:mm",
                Locale("pt", "BR")
            )

        return "Análise realizada em ${
            formato.format(
                Date(timestamp)
            )
        }"
    }

    private fun corNivelAtencao(
        nivel: String
    ): Int {

        return when {

            nivel.contains(
                "elevado",
                ignoreCase = true
            ) -> {

                Color.parseColor(
                    "#D9534F"
                )
            }

            nivel.contains(
                "moderado",
                ignoreCase = true
            ) -> {

                Color.parseColor(
                    "#E6A23C"
                )
            }

            nivel.contains(
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