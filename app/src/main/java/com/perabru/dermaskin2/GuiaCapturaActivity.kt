package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GuiaCapturaActivity : AppCompatActivity() {

    private lateinit var txtAreaSelecionadaGuia: TextView
    private lateinit var btnContinuarCaptura: Button
    private lateinit var btnVoltarGuia: Button

    private var areaSelecionada: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guia_captura)

        areaSelecionada = intent.getStringExtra("areaSelecionada") ?: "Área não informada"

        iniciarComponentes()
        configurarTela()
        configurarCliques()
    }

    private fun iniciarComponentes() {
        txtAreaSelecionadaGuia = findViewById(R.id.txtAreaSelecionadaGuia)
        btnContinuarCaptura = findViewById(R.id.btnContinuarCaptura)
        btnVoltarGuia = findViewById(R.id.btnVoltarGuia)
    }

    private fun configurarTela() {
        txtAreaSelecionadaGuia.text = "Área selecionada: $areaSelecionada"
    }

    private fun configurarCliques() {
        btnContinuarCaptura.setOnClickListener {
            val intent = Intent(this, CapturaImagemActivity::class.java)
            intent.putExtra("areaSelecionada", areaSelecionada)
            startActivity(intent)
        }

        btnVoltarGuia.setOnClickListener {
            finish()
        }
    }
}