package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SelecionarAreaActivity : AppCompatActivity() {

    private lateinit var btnRosto: LinearLayout
    private lateinit var btnPescoco: LinearLayout
    private lateinit var btnBraco: LinearLayout
    private lateinit var btnMao: LinearLayout
    private lateinit var btnCostas: LinearLayout
    private lateinit var btnPeito: LinearLayout
    private lateinit var btnBarriga: LinearLayout
    private lateinit var btnPerna: LinearLayout
    private lateinit var btnPe: LinearLayout
    private lateinit var btnOutraArea: LinearLayout

    private lateinit var txtAreaSelecionada: TextView
    private lateinit var btnContinuar: Button
    private lateinit var btnVoltar: Button

    private var areaSelecionada: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selecionar_area)

        iniciarComponentes()
        configurarCliques()
    }

    private fun iniciarComponentes() {
        btnRosto = findViewById(R.id.btnRosto)
        btnPescoco = findViewById(R.id.btnPescoco)
        btnBraco = findViewById(R.id.btnBraco)
        btnMao = findViewById(R.id.btnMao)
        btnCostas = findViewById(R.id.btnCostas)
        btnPeito = findViewById(R.id.btnPeito)
        btnBarriga = findViewById(R.id.btnBarriga)
        btnPerna = findViewById(R.id.btnPerna)
        btnPe = findViewById(R.id.btnPe)
        btnOutraArea = findViewById(R.id.btnOutraArea)

        txtAreaSelecionada = findViewById(R.id.txtAreaSelecionada)
        btnContinuar = findViewById(R.id.btnContinuarArea)
        btnVoltar = findViewById(R.id.btnVoltarArea)
    }

    private fun configurarCliques() {
        btnRosto.setOnClickListener {
            selecionarArea("Rosto")
        }

        btnPescoco.setOnClickListener {
            selecionarArea("Pescoço")
        }

        btnBraco.setOnClickListener {
            selecionarArea("Braço")
        }

        btnMao.setOnClickListener {
            selecionarArea("Mão")
        }

        btnCostas.setOnClickListener {
            selecionarArea("Costas")
        }

        btnPeito.setOnClickListener {
            selecionarArea("Peito")
        }

        btnBarriga.setOnClickListener {
            selecionarArea("Barriga")
        }

        btnPerna.setOnClickListener {
            selecionarArea("Perna")
        }

        btnPe.setOnClickListener {
            selecionarArea("Pé")
        }

        btnOutraArea.setOnClickListener {
            selecionarArea("Outra área")
        }

        btnContinuar.setOnClickListener {
            continuarParaGuia()
        }

        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun selecionarArea(area: String) {
        areaSelecionada = area
        txtAreaSelecionada.text = "Área selecionada: $area"

        Toast.makeText(
            this,
            "$area selecionado(a)",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun continuarParaGuia() {
        if (areaSelecionada.isBlank()) {
            Toast.makeText(
                this,
                "Selecione a área do corpo antes de continuar.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val intent = Intent(this, GuiaCapturaActivity::class.java)
        intent.putExtra("areaSelecionada", areaSelecionada)
        startActivity(intent)
    }
}