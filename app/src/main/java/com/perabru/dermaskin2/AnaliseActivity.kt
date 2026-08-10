package com.perabru.dermaskin2

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class AnaliseActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var cardNovaTriagem: View
    private lateinit var btnIniciarAgora: View
    private lateinit var btnScanCentral: View

    private lateinit var cardAcompanharLesao: View
    private lateinit var cardOrientacoes: View
    private lateinit var cardRelatorio: View
    private lateinit var cardClinicas: View

    private lateinit var cardInca: View
    private lateinit var cardMaisOrientacoes: View

    private lateinit var navInicio: View
    private lateinit var navHistorico: View
    private lateinit var navPerfil: View
    private lateinit var navMais: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analise)

        window.navigationBarColor = Color.WHITE
        window.statusBarColor = Color.parseColor("#F6FBFF")

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            voltarParaLogin()
            return
        }

        iniciarComponentes()
        configurarCliques()
    }

    private fun iniciarComponentes() {

        cardNovaTriagem = findViewById(R.id.cardNovaTriagem)
        btnIniciarAgora = findViewById(R.id.btnIniciarAgora)
        btnScanCentral = findViewById(R.id.btnScanCentral)

        cardAcompanharLesao = findViewById(R.id.cardAcompanharLesao)
        cardOrientacoes = findViewById(R.id.cardOrientacoes)
        cardRelatorio = findViewById(R.id.cardRelatorio)
        cardClinicas = findViewById(R.id.cardClinicas)

        cardInca = findViewById(R.id.cardInca)
        cardMaisOrientacoes = findViewById(R.id.cardMaisOrientacoes)

        navInicio = findViewById(R.id.navInicio)
        navHistorico = findViewById(R.id.navHistorico)
        navPerfil = findViewById(R.id.navPerfil)
        navMais = findViewById(R.id.navMais)
    }

    private fun configurarCliques() {

        cardNovaTriagem.setOnClickListener {
            abrirNovaTriagem()
        }

        btnIniciarAgora.setOnClickListener {
            abrirNovaTriagem()
        }

        btnScanCentral.setOnClickListener {
            abrirNovaTriagem()
        }

        cardAcompanharLesao.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MonitoramentoLesaoActivity::class.java
                )
            )
        }

        cardOrientacoes.setOnClickListener {
            abrirOrientacoes()
        }

        cardRelatorio.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RelatoriosActivity::class.java
                )
            )
        }

        cardClinicas.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    EncaminhamentoActivity::class.java
                )
            )
        }

        cardMaisOrientacoes.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MaisOrientacoesActivity::class.java
                )
            )
        }

        cardInca.setOnClickListener {

            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.gov.br/inca/pt-br")
            )

            startActivity(intent)
        }

        navInicio.setOnClickListener {
            Toast.makeText(
                this,
                "Você já está no início.",
                Toast.LENGTH_SHORT
            ).show()
        }

        navHistorico.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    HistoricoAnalisesActivity::class.java
                )
            )
        }

        navPerfil.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    PerfilPrivacidadeActivity::class.java
                )
            )
        }

        // MAIS
        navMais.setOnClickListener {
            mostrarMenuMais()
        }
    }

    private fun mostrarMenuMais() {

        val dialog = BottomSheetDialog(this)

        val view = LayoutInflater.from(this)
            .inflate(R.layout.bottom_sheet_mais, null)

        val btnSobreDermaPrev =
            view.findViewById<LinearLayout>(
                R.id.btnSobreDermaPrev
            )

        val btnComoUsarDermaPrev =
            view.findViewById<LinearLayout>(
                R.id.btnComoUsarDermaPrev
            )

        val btnContatoDermaPrev =
            view.findViewById<LinearLayout>(
                R.id.btnContatoDermaPrev
            )

        // SOBRE O DERMAPREV
        btnSobreDermaPrev.setOnClickListener {

            dialog.dismiss()

            mostrarInformacao(
                "Sobre o DermaPrev",
                """
                O DermaPrev é um aplicativo desenvolvido para apoiar a triagem visual de lesões cutâneas.

                A ferramenta utiliza análise de imagem e informações fornecidas pelo usuário para indicar um nível de atenção e auxiliar na organização do acompanhamento.

                O DermaPrev não fornece diagnóstico e não substitui a avaliação realizada por um profissional de saúde.
                """.trimIndent()
            )
        }

        // COMO USAR O DERMAPREV
        btnComoUsarDermaPrev.setOnClickListener {

            dialog.dismiss()

            mostrarInformacao(
                "Como usar o DermaPrev",
                """
                1. Toque em Escanear para iniciar uma nova análise.

                2. Selecione a região do corpo e siga as orientações para fotografar a pele.

                3. Confira se a imagem está adequada antes de continuar.

                4. Responda às perguntas sobre a lesão.

                5. O DermaPrev realizará a análise e apresentará um nível de atenção.

                6. Consulte o Histórico para visualizar análises anteriores.

                7. Em Acompanhar lesão, você pode registrar novas fotos e comparar os registros ao longo do tempo.

                8. Utilize Relatório para organizar as informações que podem ser apresentadas a um profissional de saúde.
                """.trimIndent()
            )
        }

        // DÚVIDAS OU SUGESTÕES
        btnContatoDermaPrev.setOnClickListener {

            dialog.dismiss()

            val intent = Intent(
                Intent.ACTION_SENDTO
            ).apply {

                data = Uri.parse(
                    "mailto:dermaprev.app@gmail.com"
                )

                putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Dúvida ou sugestão — DermaPrev"
                )

                putExtra(
                    Intent.EXTRA_TEXT,
                    """
                    Olá, equipe DermaPrev.

                    
                    
                    """.trimIndent()
                )
            }

            if (intent.resolveActivity(packageManager) != null) {

                startActivity(intent)

            } else {

                Toast.makeText(
                    this,
                    "Nenhum aplicativo de e-mail encontrado.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun mostrarInformacao(
        titulo: String,
        mensagem: String
    ) {

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensagem)
            .setPositiveButton("Entendi", null)
            .show()
    }

    private fun abrirOrientacoes() {

        startActivity(
            Intent(
                this,
                OrientacoesPrevencaoActivity::class.java
            )
        )
    }

    private fun abrirNovaTriagem() {

        val intent = Intent(
            this,
            GuiaCapturaActivity::class.java
        )

        intent.putExtra(
            "areaSelecionada",
            "Não informada"
        )

        startActivity(intent)
    }

    private fun voltarParaLogin() {

        val intent = Intent(
            this,
            MainActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}