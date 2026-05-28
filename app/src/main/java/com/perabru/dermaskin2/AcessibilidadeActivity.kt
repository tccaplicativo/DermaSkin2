package com.perabru.dermaskin2

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AcessibilidadeActivity : AppCompatActivity() {

    private lateinit var switchFonteGrande: Switch
    private lateinit var switchAltoContraste: Switch
    private lateinit var switchLeituraFacil: Switch

    private lateinit var txtPreviewAcessibilidade: TextView

    private lateinit var btnSalvarAcessibilidade: Button
    private lateinit var btnVoltarAcessibilidade: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acessibilidade)

        iniciarComponentes()
        carregarPreferencias()
        configurarCliques()
    }

    private fun iniciarComponentes() {
        switchFonteGrande = findViewById(R.id.switchFonteGrande)
        switchAltoContraste = findViewById(R.id.switchAltoContraste)
        switchLeituraFacil = findViewById(R.id.switchLeituraFacil)

        txtPreviewAcessibilidade = findViewById(R.id.txtPreviewAcessibilidade)

        btnSalvarAcessibilidade = findViewById(R.id.btnSalvarAcessibilidade)
        btnVoltarAcessibilidade = findViewById(R.id.btnVoltarAcessibilidade)
    }

    private fun carregarPreferencias() {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        switchFonteGrande.isChecked = sharedPreferences.getBoolean("fonte_grande", false)
        switchAltoContraste.isChecked = sharedPreferences.getBoolean("alto_contraste", false)
        switchLeituraFacil.isChecked = sharedPreferences.getBoolean("leitura_facil", false)

        atualizarPreview()
    }

    private fun configurarCliques() {
        switchFonteGrande.setOnCheckedChangeListener { _, _ ->
            atualizarPreview()
        }

        switchAltoContraste.setOnCheckedChangeListener { _, _ ->
            atualizarPreview()
        }

        switchLeituraFacil.setOnCheckedChangeListener { _, _ ->
            atualizarPreview()
        }

        btnSalvarAcessibilidade.setOnClickListener {
            salvarPreferencias()
        }

        btnVoltarAcessibilidade.setOnClickListener {
            finish()
        }
    }

    private fun atualizarPreview() {
        val fonteGrande = switchFonteGrande.isChecked
        val altoContraste = switchAltoContraste.isChecked
        val leituraFacil = switchLeituraFacil.isChecked

        txtPreviewAcessibilidade.textSize = if (fonteGrande) {
            20f
        } else {
            15f
        }

        if (altoContraste) {
            txtPreviewAcessibilidade.setTextColor(android.graphics.Color.BLACK)
            txtPreviewAcessibilidade.setBackgroundColor(android.graphics.Color.WHITE)
        } else {
            txtPreviewAcessibilidade.setTextColor(android.graphics.Color.parseColor("#3A2A24"))
            txtPreviewAcessibilidade.setBackgroundColor(android.graphics.Color.parseColor("#FFF8F2"))
        }

        txtPreviewAcessibilidade.text = if (leituraFacil) {
            "Texto simples: o app ajuda a observar sinais na pele. Ele não substitui médico."
        } else {
            "O DermaSkin realiza uma triagem visual da pele com apoio de tecnologia, mas não substitui avaliação médica profissional."
        }
    }

    private fun salvarPreferencias() {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        sharedPreferences.edit()
            .putBoolean("fonte_grande", switchFonteGrande.isChecked)
            .putBoolean("alto_contraste", switchAltoContraste.isChecked)
            .putBoolean("leitura_facil", switchLeituraFacil.isChecked)
            .apply()

        Toast.makeText(
            this,
            "Preferências de acessibilidade salvas.",
            Toast.LENGTH_SHORT
        ).show()
    }
}