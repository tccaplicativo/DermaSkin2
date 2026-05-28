package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MetricasImpactoActivity : AppCompatActivity() {

    private lateinit var txtTotalMetricas: TextView
    private lateinit var txtEncaminhamentosMetricas: TextView
    private lateinit var txtPrevencaoMetricas: TextView
    private lateinit var txtRelatoriosMetricas: TextView

    private lateinit var txtResumoImpacto: TextView
    private lateinit var txtIndicadoresImpacto: TextView

    private lateinit var btnPainelProfissionalMetricas: Button
    private lateinit var btnVoltarMetricas: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metricas_impacto)

        iniciarComponentes()
        configurarTela()
        configurarCliques()
    }

    private fun iniciarComponentes() {
        txtTotalMetricas = findViewById(R.id.txtTotalMetricas)
        txtEncaminhamentosMetricas = findViewById(R.id.txtEncaminhamentosMetricas)
        txtPrevencaoMetricas = findViewById(R.id.txtPrevencaoMetricas)
        txtRelatoriosMetricas = findViewById(R.id.txtRelatoriosMetricas)

        txtResumoImpacto = findViewById(R.id.txtResumoImpacto)
        txtIndicadoresImpacto = findViewById(R.id.txtIndicadoresImpacto)

        btnPainelProfissionalMetricas = findViewById(R.id.btnPainelProfissionalMetricas)
        btnVoltarMetricas = findViewById(R.id.btnVoltarMetricas)
    }

    private fun configurarTela() {
        /*
         * Dados demonstrativos por enquanto.
         * Depois podemos conectar ao Firebase para calcular métricas reais.
         */

        txtTotalMetricas.text = "128"
        txtEncaminhamentosMetricas.text = "42"
        txtPrevencaoMetricas.text = "89"
        txtRelatoriosMetricas.text = "57"

        txtResumoImpacto.text =
            "O DermaSkin pode contribuir para ampliar o acesso à triagem inicial, " +
                    "organizar informações para consulta médica e incentivar a busca por atendimento profissional."

        txtIndicadoresImpacto.text =
            "Indicadores demonstrativos:\n\n" +
                    "• 128 triagens visuais realizadas.\n" +
                    "• 42 usuários orientados a buscar encaminhamento.\n" +
                    "• 89 acessos às orientações de prevenção.\n" +
                    "• 57 relatórios gerados para apoio à consulta.\n\n" +
                    "Esses dados ajudam a observar o impacto do aplicativo na educação em saúde, " +
                    "na prevenção e na comunicação entre usuário e profissional."
    }

    private fun configurarCliques() {
        btnPainelProfissionalMetricas.setOnClickListener {
            val intent = Intent(this, PainelProfissionalActivity::class.java)
            startActivity(intent)
        }

        btnVoltarMetricas.setOnClickListener {
            finish()
        }
    }
}