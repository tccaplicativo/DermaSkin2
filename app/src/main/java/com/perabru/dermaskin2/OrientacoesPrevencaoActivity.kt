package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OrientacoesPrevencaoActivity : AppCompatActivity() {

    private lateinit var btnEncaminhamentoPrevencao: Button
    private lateinit var btnNovaAnalisePrevencao: Button
    private lateinit var btnVoltarPrevencao: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orientacoes_prevencao)

        iniciarComponentes()
        configurarCliques()
    }

    private fun iniciarComponentes() {
        btnEncaminhamentoPrevencao = findViewById(R.id.btnEncaminhamentoPrevencao)
        btnNovaAnalisePrevencao = findViewById(R.id.btnNovaAnalisePrevencao)
        btnVoltarPrevencao = findViewById(R.id.btnVoltarPrevencao)
    }

    private fun configurarCliques() {
        btnEncaminhamentoPrevencao.setOnClickListener {
            val intent = Intent(this, EncaminhamentoActivity::class.java)
            startActivity(intent)
        }

        btnNovaAnalisePrevencao.setOnClickListener {
            val intent = Intent(this, SelecionarAreaActivity::class.java)
            startActivity(intent)
        }

        btnVoltarPrevencao.setOnClickListener {
            finish()
        }
    }
}