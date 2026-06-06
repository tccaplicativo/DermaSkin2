package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProcessamentoiaActivity : AppCompatActivity() {

    private lateinit var txtAreaProcessamento: TextView
    private lateinit var txtStatusProcessamento: TextView
    private lateinit var progressProcessamento: ProgressBar

    private var areaSelecionada: String = ""
    private var imagePath: String = ""

    private var tempoLesao: String = ""
    private var mudancaTamanho: String = ""
    private var mudancaCor: String = ""
    private var coceira: String = ""
    private var sangramento: String = ""
    private var dor: String = ""
    private var formatoIrregular: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_processamentoia)

        recuperarDados()
        iniciarComponentes()
        configurarTela()
        iniciarProcessamentoProvisorio()
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
    }

    private fun iniciarComponentes() {
        txtAreaProcessamento = findViewById(R.id.txtAreaProcessamento)
        txtStatusProcessamento = findViewById(R.id.txtStatusProcessamento)
        progressProcessamento = findViewById(R.id.progressProcessamento)
    }

    private fun configurarTela() {
        txtAreaProcessamento.text = "Área da lesão: $areaSelecionada"
        txtStatusProcessamento.text = "Preparando triagem..."
    }

    private fun iniciarProcessamentoProvisorio() {
        Handler(Looper.getMainLooper()).postDelayed({
            txtStatusProcessamento.text = "Verificando respostas do questionário..."
        }, 1000)

        Handler(Looper.getMainLooper()).postDelayed({
            txtStatusProcessamento.text = "Calculando prioridade de atenção..."
        }, 2200)

        Handler(Looper.getMainLooper()).postDelayed({
            txtStatusProcessamento.text = "Gerando resultado..."
        }, 3400)

        Handler(Looper.getMainLooper()).postDelayed({
            continuarParaResultado()
        }, 4500)
    }

    private fun continuarParaResultado() {
        val pontuacao = calcularPontuacaoClinica()

        val riscoTitulo: String
        val riscoPercentual: String
        val riscoDescricao: String

        when {
            pontuacao >= 6 -> {
                riscoTitulo = "Risco alto"
                riscoPercentual = "78%"
                riscoDescricao =
                    "A triagem identificou vários sinais clínicos de atenção, como mudança na lesão, sintomas associados ou formato irregular. Este resultado não é um diagnóstico, mas indica que é importante procurar avaliação dermatológica o quanto antes."
            }

            pontuacao >= 3 -> {
                riscoTitulo = "Risco médio"
                riscoPercentual = "52%"
                riscoDescricao =
                    "A triagem encontrou alguns sinais que merecem acompanhamento, como alterações relatadas ou sintomas leves. Este resultado não confirma doença, mas recomenda atenção e avaliação profissional se houver persistência ou piora."
            }

            else -> {
                riscoTitulo = "Risco baixo"
                riscoPercentual = "24%"
                riscoDescricao =
                    "A triagem encontrou poucos sinais de alerta nas respostas informadas. Mesmo assim, continue observando a lesão e procure um dermatologista se ela mudar de cor, crescer, coçar, sangrar ou não cicatrizar."
            }
        }

        val intent = Intent(this, ResultadoTriagemActivity::class.java)

        intent.putExtra("areaSelecionada", areaSelecionada)
        intent.putExtra("imagePath", imagePath)

        intent.putExtra("tempoLesao", tempoLesao)
        intent.putExtra("mudancaTamanho", mudancaTamanho)
        intent.putExtra("mudancaCor", mudancaCor)
        intent.putExtra("coceira", coceira)
        intent.putExtra("sangramento", sangramento)
        intent.putExtra("dor", dor)
        intent.putExtra("formatoIrregular", formatoIrregular)

        intent.putExtra("riscoTitulo", riscoTitulo)
        intent.putExtra("riscoPercentual", riscoPercentual)
        intent.putExtra("riscoDescricao", riscoDescricao)

        startActivity(intent)
        finish()
    }

    private fun calcularPontuacaoClinica(): Int {
        var pontos = 0

        if (mudancaTamanho.equals("Sim", ignoreCase = true)) {
            pontos += 2
        }

        if (mudancaCor.equals("Sim", ignoreCase = true)) {
            pontos += 2
        }

        if (sangramento.equals("Sim", ignoreCase = true)) {
            pontos += 2
        }

        if (formatoIrregular.equals("Sim", ignoreCase = true)) {
            pontos += 2
        }

        if (coceira.equals("Sim", ignoreCase = true)) {
            pontos += 1
        }

        if (coceira.equals("Às vezes", ignoreCase = true)) {
            pontos += 1
        }

        if (dor.equals("Sim", ignoreCase = true)) {
            pontos += 1
        }

        if (dor.equals("Às vezes", ignoreCase = true)) {
            pontos += 1
        }

        if (tempoLesao.equals("Mais de 6 meses", ignoreCase = true)) {
            pontos += 1
        }

        return pontos
    }
}