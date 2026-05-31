package com.perabru.dermaskin2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AnaliseActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var cardNovaTriagem: View
    private lateinit var btnIniciarAgora: View

    private lateinit var cardAcompanharLesao: View
    private lateinit var cardOrientacoes: View
    private lateinit var cardRelatorio: View
    private lateinit var cardClinicas: View

    private lateinit var navInicio: View
    private lateinit var navHistorico: View
    private lateinit var navOrientacoes: View
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

        cardAcompanharLesao = findViewById(R.id.cardAcompanharLesao)
        cardOrientacoes = findViewById(R.id.cardOrientacoes)
        cardRelatorio = findViewById(R.id.cardRelatorio)
        cardClinicas = findViewById(R.id.cardClinicas)

        navInicio = findViewById(R.id.navInicio)
        navHistorico = findViewById(R.id.navHistorico)
        navOrientacoes = findViewById(R.id.navOrientacoes)
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

        cardAcompanharLesao.setOnClickListener {
            startActivity(Intent(this, MonitoramentoLesaoActivity::class.java))
        }

        cardOrientacoes.setOnClickListener {
            startActivity(Intent(this, OrientacoesPrevencaoActivity::class.java))
        }

        cardRelatorio.setOnClickListener {
            startActivity(Intent(this, RelatorioProfissionalActivity::class.java))
        }

        cardClinicas.setOnClickListener {
            startActivity(Intent(this, EncaminhamentoActivity::class.java))
        }

        navInicio.setOnClickListener {
            Toast.makeText(this, "Você já está no início.", Toast.LENGTH_SHORT).show()
        }

        navHistorico.setOnClickListener {
            startActivity(Intent(this, HistoricoAnalisesActivity::class.java))
        }

        navOrientacoes.setOnClickListener {
            startActivity(Intent(this, OrientacoesPrevencaoActivity::class.java))
        }

        navPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilPrivacidadeActivity::class.java))
        }

        navMais.setOnClickListener {
            startActivity(Intent(this, AcessibilidadeActivity::class.java))
        }
    }

    private fun abrirNovaTriagem() {
        startActivity(Intent(this, SelecionarAreaActivity::class.java))
    }

    private fun voltarParaLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}