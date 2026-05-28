package com.perabru.dermaskin2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EncaminhamentoActivity : AppCompatActivity() {

    private lateinit var btnBuscarUbs: Button
    private lateinit var btnBuscarDermatologista: Button
    private lateinit var btnLigarSus: Button
    private lateinit var btnPerfilPrivacidade: Button
    private lateinit var btnVoltarEncaminhamento: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encaminhamento)

        iniciarComponentes()
        configurarCliques()
    }

    private fun iniciarComponentes() {
        btnBuscarUbs = findViewById(R.id.btnBuscarUbs)
        btnBuscarDermatologista = findViewById(R.id.btnBuscarDermatologista)
        btnLigarSus = findViewById(R.id.btnLigarSus)
        btnPerfilPrivacidade = findViewById(R.id.btnPerfilPrivacidade)
        btnVoltarEncaminhamento = findViewById(R.id.btnVoltarEncaminhamento)
    }

    private fun configurarCliques() {
        btnBuscarUbs.setOnClickListener {
            abrirMapa("UBS próxima")
        }

        btnBuscarDermatologista.setOnClickListener {
            abrirMapa("dermatologista próximo")
        }

        btnLigarSus.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:136")
            startActivity(intent)
        }

        btnPerfilPrivacidade.setOnClickListener {
            val intent = Intent(this, PerfilPrivacidadeActivity::class.java)
            startActivity(intent)
        }

        btnVoltarEncaminhamento.setOnClickListener {
            finish()
        }
    }

    private fun abrirMapa(pesquisa: String) {
        val uri = Uri.parse("geo:0,0?q=${Uri.encode(pesquisa)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        try {
            startActivity(intent)
        } catch (e: Exception) {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/${Uri.encode(pesquisa)}")
            )
            startActivity(browserIntent)
        }
    }
}