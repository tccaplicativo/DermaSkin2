package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class OrientacoesPrevencaoActivity : AppCompatActivity() {

    private lateinit var btnEncaminhamentoPrevencao: LinearLayout
    private lateinit var btnNovaAnalisePrevencao: LinearLayout
    private lateinit var btnVoltarPrevencao: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_orientacoes_prevencao
        )

        iniciarComponentes()
        configurarCliques()
    }

    private fun iniciarComponentes() {

        btnEncaminhamentoPrevencao =
            findViewById(
                R.id.btnEncaminhamentoPrevencao
            )

        btnNovaAnalisePrevencao =
            findViewById(
                R.id.btnNovaAnalisePrevencao
            )

        btnVoltarPrevencao =
            findViewById(
                R.id.btnVoltarPrevencao
            )
    }

    private fun configurarCliques() {

        btnVoltarPrevencao.setOnClickListener {

            finish()
        }

        btnEncaminhamentoPrevencao.setOnClickListener {

            val intent =
                Intent(
                    this,
                    EncaminhamentoActivity::class.java
                )

            startActivity(
                intent
            )
        }

        btnNovaAnalisePrevencao.setOnClickListener {

            val intent =
                Intent(
                    this,
                    GuiaCapturaActivity::class.java
                )

            startActivity(
                intent
            )
        }
    }
}