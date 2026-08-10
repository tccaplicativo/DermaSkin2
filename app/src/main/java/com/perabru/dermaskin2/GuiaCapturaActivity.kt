package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class GuiaCapturaActivity : AppCompatActivity() {

    private lateinit var btnContinuarCaptura: Button
    private lateinit var btnVoltarGuia: Button
    private lateinit var btnVoltarTopo: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guia_captura)

        iniciarComponentes()
        configurarCliques()
    }

    private fun iniciarComponentes() {
        btnContinuarCaptura = findViewById(R.id.btnContinuarCaptura)
        btnVoltarGuia = findViewById(R.id.btnVoltarGuia)
        btnVoltarTopo = findViewById(R.id.btnVoltarTopo)
    }

    private fun configurarCliques() {

        btnContinuarCaptura.setOnClickListener {
            val intent = Intent(this, CapturaImagemActivity::class.java)
            startActivity(intent)
        }

        btnVoltarGuia.setOnClickListener {
            finish()
        }

        btnVoltarTopo.setOnClickListener {
            finish()
        }
    }
}