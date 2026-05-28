package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PainelProfissionalActivity : AppCompatActivity() {

    private lateinit var txtTotalTriagens: TextView
    private lateinit var txtRiscoAlto: TextView
    private lateinit var txtRiscoMedio: TextView
    private lateinit var txtRiscoBaixo: TextView
    private lateinit var txtResumoParceiro: TextView

    private lateinit var btnMetricasImpacto: Button
    private lateinit var btnHistoricoProfissional: Button
    private lateinit var btnVoltarPainelProfissional: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_painel_profissional)

        iniciarComponentes()
        configurarTela()
        configurarCliques()
    }

    private fun iniciarComponentes() {
        txtTotalTriagens = findViewById(R.id.txtTotalTriagens)
        txtRiscoAlto = findViewById(R.id.txtRiscoAlto)
        txtRiscoMedio = findViewById(R.id.txtRiscoMedio)
        txtRiscoBaixo = findViewById(R.id.txtRiscoBaixo)
        txtResumoParceiro = findViewById(R.id.txtResumoParceiro)

        btnMetricasImpacto = findViewById(R.id.btnMetricasImpacto)
        btnHistoricoProfissional = findViewById(R.id.btnHistoricoProfissional)
        btnVoltarPainelProfissional = findViewById(R.id.btnVoltarPainelProfissional)
    }

    private fun configurarTela() {
        /*
         * Dados demonstrativos por enquanto.
         * Depois podemos conectar isso ao Firebase para puxar dados reais.
         */

        txtTotalTriagens.text = "128"
        txtRiscoAlto.text = "18"
        txtRiscoMedio.text = "46"
        txtRiscoBaixo.text = "64"

        txtResumoParceiro.text =
            "Resumo do painel:\n\n" +
                    "• Total de triagens registradas: 128\n" +
                    "• Casos com prioridade alta: 18\n" +
                    "• Casos com prioridade média: 46\n" +
                    "• Casos com prioridade baixa: 64\n\n" +
                    "Este painel pode ser usado por profissionais, clínicas ou parceiros para visualizar indicadores gerais, acompanhar encaminhamentos e analisar o impacto do aplicativo na triagem inicial."
    }

    private fun configurarCliques() {
        btnMetricasImpacto.setOnClickListener {
            val intent = Intent(this, MetricasImpactoActivity::class.java)
            startActivity(intent)
        }

        btnHistoricoProfissional.setOnClickListener {
            val intent = Intent(this, HistoricoAnalisesActivity::class.java)
            startActivity(intent)
        }

        btnVoltarPainelProfissional.setOnClickListener {
            finish()
        }
    }
}