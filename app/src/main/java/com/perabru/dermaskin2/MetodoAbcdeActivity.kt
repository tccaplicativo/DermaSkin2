package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MetodoAbcdeActivity : AppCompatActivity() {

    private lateinit var txtAreaAbcde: TextView
    private lateinit var txtResumoAbcde: TextView
    private lateinit var txtResultadoAbcde: TextView

    private lateinit var btnRelatorioAbcde: Button
    private lateinit var btnVoltarAbcde: Button

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metodo_abcde)

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
        txtAreaAbcde = findViewById(R.id.txtAreaAbcde)
        txtResumoAbcde = findViewById(R.id.txtResumoAbcde)
        txtResultadoAbcde = findViewById(R.id.txtResultadoAbcde)

        btnRelatorioAbcde = findViewById(R.id.btnRelatorioAbcde)
        btnVoltarAbcde = findViewById(R.id.btnVoltarAbcde)
    }

    private fun configurarTela() {
        txtAreaAbcde.text = "Área selecionada: $areaSelecionada"

        txtResumoAbcde.text =
            "Resultado da triagem: $riscoTitulo\n" +
                    "Percentual estimado: $riscoPercentual\n\n" +
                    riscoDescricao

        txtResultadoAbcde.text =
            "A - Assimetria\n" +
                    avaliarAssimetria() + "\n\n" +

                    "B - Bordas\n" +
                    avaliarBordas() + "\n\n" +

                    "C - Cor\n" +
                    avaliarCor() + "\n\n" +

                    "D - Diâmetro\n" +
                    avaliarDiametro() + "\n\n" +

                    "E - Evolução\n" +
                    avaliarEvolucao()
    }

    private fun configurarCliques() {
        btnRelatorioAbcde.setOnClickListener {
            val intent = Intent(this, RelatorioProfissionalActivity::class.java)

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
        }

        btnVoltarAbcde.setOnClickListener {
            finish()
        }
    }

    private fun avaliarAssimetria(): String {
        return if (formatoIrregular.equals("Sim", ignoreCase = true)) {
            "A lesão foi informada como visualmente irregular, o que pode indicar assimetria e merece atenção profissional."
        } else {
            "Não houve indicação clara de assimetria pelo questionário. Mesmo assim, a avaliação visual profissional é importante."
        }
    }

    private fun avaliarBordas(): String {
        return if (formatoIrregular.equals("Sim", ignoreCase = true)) {
            "Bordas irregulares podem ser um sinal de alerta. Observe se a lesão possui contornos mal definidos."
        } else {
            "Não foi relatado formato irregular. Bordas aparentemente regulares costumam ser menos preocupantes, mas não descartam avaliação."
        }
    }

    private fun avaliarCor(): String {
        return if (mudancaCor.equals("Sim", ignoreCase = true)) {
            "A mudança de cor é um sinal importante de evolução da lesão e deve ser avaliada por um dermatologista."
        } else {
            "Não foi relatada mudança de cor. Continue observando se aparecem tons diferentes na mesma lesão."
        }
    }

    private fun avaliarDiametro(): String {
        return "O aplicativo ainda não mede o diâmetro real em milímetros nesta etapa. Para precisão, use uma régua ou procure avaliação médica."
    }

    private fun avaliarEvolucao(): String {
        val sinais = mutableListOf<String>()

        if (mudancaTamanho.equals("Sim", ignoreCase = true)) sinais.add("aumento de tamanho")
        if (mudancaCor.equals("Sim", ignoreCase = true)) sinais.add("mudança de cor")
        if (coceira.equals("Sim", ignoreCase = true) || coceira.equals("Às vezes", ignoreCase = true)) sinais.add("coceira")
        if (sangramento.equals("Sim", ignoreCase = true)) sinais.add("sangramento")
        if (dor.equals("Sim", ignoreCase = true) || dor.equals("Às vezes", ignoreCase = true)) sinais.add("dor")

        return if (sinais.isNotEmpty()) {
            "Foram relatados sinais de evolução: ${sinais.joinToString(", ")}. Isso aumenta a necessidade de acompanhamento profissional."
        } else {
            "Não foram relatados sinais importantes de evolução recente. Continue observando a lesão ao longo do tempo."
        }
    }
}