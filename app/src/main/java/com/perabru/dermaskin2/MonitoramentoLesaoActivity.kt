package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MonitoramentoLesaoActivity : AppCompatActivity() {

    private lateinit var txtAreaMonitoramento: TextView
    private lateinit var txtRiscoMonitoramento: TextView
    private lateinit var txtDescricaoMonitoramento: TextView
    private lateinit var txtChecklistMonitoramento: TextView

    private lateinit var btnNovaFotoMonitoramento: Button
    private lateinit var btnOrientacoesMonitoramento: Button
    private lateinit var btnVoltarMonitoramento: Button

    private var areaSelecionada: String = ""
    private var riscoTitulo: String = ""
    private var riscoPercentual: String = ""
    private var riscoDescricao: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitoramento_lesao)

        recuperarDados()
        iniciarComponentes()
        configurarTela()
        configurarCliques()
    }

    private fun recuperarDados() {
        areaSelecionada = intent.getStringExtra("areaSelecionada") ?: "Área não informada"
        riscoTitulo = intent.getStringExtra("riscoTitulo") ?: "Risco não informado"
        riscoPercentual = intent.getStringExtra("riscoPercentual") ?: "--"
        riscoDescricao = intent.getStringExtra("riscoDescricao") ?: "Sem descrição disponível."
    }

    private fun iniciarComponentes() {
        txtAreaMonitoramento = findViewById(R.id.txtAreaMonitoramento)
        txtRiscoMonitoramento = findViewById(R.id.txtRiscoMonitoramento)
        txtDescricaoMonitoramento = findViewById(R.id.txtDescricaoMonitoramento)
        txtChecklistMonitoramento = findViewById(R.id.txtChecklistMonitoramento)

        btnNovaFotoMonitoramento = findViewById(R.id.btnNovaFotoMonitoramento)
        btnOrientacoesMonitoramento = findViewById(R.id.btnOrientacoesMonitoramento)
        btnVoltarMonitoramento = findViewById(R.id.btnVoltarMonitoramento)
    }

    private fun configurarTela() {
        txtAreaMonitoramento.text = "Área acompanhada: $areaSelecionada"

        txtRiscoMonitoramento.text =
            "$riscoTitulo • $riscoPercentual"

        txtDescricaoMonitoramento.text = riscoDescricao

        txtChecklistMonitoramento.text =
            "Observe se a lesão apresentou:\n\n" +
                    "• Aumento de tamanho\n" +
                    "• Mudança de cor\n" +
                    "• Alteração no formato\n" +
                    "• Bordas mais irregulares\n" +
                    "• Coceira frequente\n" +
                    "• Dor ou sensibilidade\n" +
                    "• Sangramento\n" +
                    "• Ferida que não cicatriza\n\n" +
                    "Caso algum desses sinais apareça ou piore, procure avaliação dermatológica."
    }

    private fun configurarCliques() {
        btnNovaFotoMonitoramento.setOnClickListener {
            val intent = Intent(this, SelecionarAreaActivity::class.java)
            startActivity(intent)
        }

        btnOrientacoesMonitoramento.setOnClickListener {
            val intent = Intent(this, OrientacoesPrevencaoActivity::class.java)
            startActivity(intent)
        }

        btnVoltarMonitoramento.setOnClickListener {
            finish()
        }
    }
}