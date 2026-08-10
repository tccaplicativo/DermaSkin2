package com.perabru.dermaskin2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MaisOrientacoesActivity : AppCompatActivity() {

    private lateinit var btnVoltarMaisOrientacoes: ImageButton

    private lateinit var cardArtigoMelanoma: LinearLayout
    private lateinit var cardArtigoNaoMelanoma: LinearLayout
    private lateinit var cardArtigoDiagnostico: LinearLayout

    private lateinit var cardArtigoDermatofibroma: LinearLayout
    private lateinit var cardArtigoNevos: LinearLayout
    private lateinit var cardArtigoCeratose: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mais_orientacoes)

        iniciarComponentes()
        configurarCliques()
    }

    private fun iniciarComponentes() {

        btnVoltarMaisOrientacoes =
            findViewById(R.id.btnVoltarMaisOrientacoes)

        cardArtigoMelanoma =
            findViewById(R.id.cardArtigoMelanoma)

        cardArtigoNaoMelanoma =
            findViewById(R.id.cardArtigoNaoMelanoma)

        cardArtigoDiagnostico =
            findViewById(R.id.cardArtigoDiagnostico)

        cardArtigoDermatofibroma =
            findViewById(R.id.cardArtigoDermatofibroma)

        cardArtigoNevos =
            findViewById(R.id.cardArtigoNevos)

        cardArtigoCeratose =
            findViewById(R.id.cardArtigoCeratose)
    }

    private fun configurarCliques() {

        btnVoltarMaisOrientacoes.setOnClickListener {
            finish()
        }

        cardArtigoMelanoma.setOnClickListener {
            abrirLink(
                "https://www.gov.br/inca/pt-br/assuntos/cancer/tipos/pele-melanoma/versao-para-populacao"
            )
        }

        cardArtigoNaoMelanoma.setOnClickListener {
            abrirLink(
                "https://www.gov.br/inca/pt-br/assuntos/cancer/tipos/pele-nao-melanoma"
            )
        }

        cardArtigoDiagnostico.setOnClickListener {
            abrirLink(
                "https://www.gov.br/saude/pt-br/assuntos/saude-de-a-a-z/c/cancer-de-pele/diagnostico-precoce"
            )
        }

        cardArtigoDermatofibroma.setOnClickListener {
            abrirLink(
                "https://www.sbd.org.br/doencas/dermatofibroma/"
            )
        }

        cardArtigoNevos.setOnClickListener {
            abrirLink(
                "https://www.sbd.org.br/doencas/nevos-displasicos/"
            )
        }

        cardArtigoCeratose.setOnClickListener {
            abrirLink(
                "https://www.sbd.org.br/doencas/ceratose/"
            )
        }
    }

    private fun abrirLink(url: String) {

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )

        startActivity(intent)
    }
}