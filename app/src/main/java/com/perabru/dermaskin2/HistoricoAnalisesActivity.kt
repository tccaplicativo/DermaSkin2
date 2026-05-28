package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoricoAnalisesActivity : AppCompatActivity() {

    private lateinit var containerHistorico: LinearLayout
    private lateinit var btnNovaAnaliseHistorico: Button
    private lateinit var btnVoltarHistorico: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico_analises)

        iniciarComponentes()
        carregarHistoricoExemplo()
        configurarCliques()
    }

    private fun iniciarComponentes() {
        containerHistorico = findViewById(R.id.containerHistorico)
        btnNovaAnaliseHistorico = findViewById(R.id.btnNovaAnaliseHistorico)
        btnVoltarHistorico = findViewById(R.id.btnVoltarHistorico)
    }

    private fun configurarCliques() {
        btnNovaAnaliseHistorico.setOnClickListener {
            val intent = Intent(this, AnaliseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        btnVoltarHistorico.setOnClickListener {
            finish()
        }
    }

    private fun carregarHistoricoExemplo() {
        adicionarCardHistorico(
            data = "Hoje, 14:30",
            area = "Braço",
            risco = "Risco médio",
            percentual = "48%",
            descricao = "Lesão com sinais que merecem acompanhamento."
        )

        adicionarCardHistorico(
            data = "Ontem, 09:15",
            area = "Costas",
            risco = "Risco baixo",
            percentual = "22%",
            descricao = "Triagem com baixa compatibilidade de risco."
        )

        adicionarCardHistorico(
            data = "12/05/2026, 18:40",
            area = "Rosto",
            risco = "Risco alto",
            percentual = "76%",
            descricao = "Recomendado procurar avaliação dermatológica."
        )
    }

    private fun adicionarCardHistorico(
        data: String,
        area: String,
        risco: String,
        percentual: String,
        descricao: String
    ) {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(28, 24, 28, 24)
        card.setBackgroundResource(R.drawable.bg_card)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 22)
        card.layoutParams = params
        card.elevation = 5f

        val txtData = TextView(this)
        txtData.text = data
        txtData.textSize = 14f
        txtData.setTextColor(android.graphics.Color.parseColor("#6B4A3A"))

        val txtRisco = TextView(this)
        txtRisco.text = "$risco • $percentual"
        txtRisco.textSize = 22f
        txtRisco.setTypeface(null, android.graphics.Typeface.BOLD)

        when {
            risco.contains("alto", ignoreCase = true) ->
                txtRisco.setTextColor(android.graphics.Color.parseColor("#D9534F"))

            risco.contains("médio", ignoreCase = true) ||
                    risco.contains("medio", ignoreCase = true) ->
                txtRisco.setTextColor(android.graphics.Color.parseColor("#E6A23C"))

            else ->
                txtRisco.setTextColor(android.graphics.Color.parseColor("#5CB85C"))
        }

        val txtArea = TextView(this)
        txtArea.text = "Área analisada: $area"
        txtArea.textSize = 15f
        txtArea.setTextColor(android.graphics.Color.parseColor("#4A2A1A"))
        txtArea.setPadding(0, 10, 0, 0)

        val txtDescricao = TextView(this)
        txtDescricao.text = descricao
        txtDescricao.textSize = 14f
        txtDescricao.setTextColor(android.graphics.Color.parseColor("#3A2A24"))
        txtDescricao.setPadding(0, 10, 0, 0)

        val btnDetalhes = Button(this)
        btnDetalhes.text = "Ver detalhes"
        btnDetalhes.textSize = 15f
        btnDetalhes.isAllCaps = false
        btnDetalhes.setTextColor(android.graphics.Color.WHITE)
        btnDetalhes.backgroundTintList =
            android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#8B4A2F")
            )

        val btnParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            58
        )
        btnParams.setMargins(0, 18, 0, 0)
        btnDetalhes.layoutParams = btnParams

        btnDetalhes.setOnClickListener {
            val intent = Intent(this, MonitoramentoLesaoActivity::class.java)
            intent.putExtra("areaSelecionada", area)
            intent.putExtra("riscoTitulo", risco)
            intent.putExtra("riscoPercentual", percentual)
            intent.putExtra("riscoDescricao", descricao)
            startActivity(intent)
        }

        card.addView(txtData)
        card.addView(txtRisco)
        card.addView(txtArea)
        card.addView(txtDescricao)
        card.addView(btnDetalhes)

        containerHistorico.addView(card)
    }
}