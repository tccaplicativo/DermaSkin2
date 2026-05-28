package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PerfilPrivacidadeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var txtEmailUsuario: TextView
    private lateinit var txtStatusLgpd: TextView
    private lateinit var txtStatusPerfilSaude: TextView

    private lateinit var btnEditarPerfilSaude: Button
    private lateinit var btnAcessibilidade: Button
    private lateinit var btnExcluirDados: Button
    private lateinit var btnSairContaPrivacidade: Button
    private lateinit var btnVoltarPrivacidade: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_privacidade)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            voltarParaLogin()
            return
        }

        iniciarComponentes()
        configurarTela()
        configurarCliques()
    }

    private fun iniciarComponentes() {
        txtEmailUsuario = findViewById(R.id.txtEmailUsuario)
        txtStatusLgpd = findViewById(R.id.txtStatusLgpd)
        txtStatusPerfilSaude = findViewById(R.id.txtStatusPerfilSaude)

        btnEditarPerfilSaude = findViewById(R.id.btnEditarPerfilSaude)
        btnAcessibilidade = findViewById(R.id.btnAcessibilidade)
        btnExcluirDados = findViewById(R.id.btnExcluirDados)
        btnSairContaPrivacidade = findViewById(R.id.btnSairContaPrivacidade)
        btnVoltarPrivacidade = findViewById(R.id.btnVoltarPrivacidade)
    }

    private fun configurarTela() {
        val usuario = auth.currentUser
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        val lgpdAccepted = sharedPreferences.getBoolean("lgpd_accepted", false)
        val healthProfileCompleted = sharedPreferences.getBoolean("health_profile_completed", false)

        txtEmailUsuario.text = "E-mail da conta: ${usuario?.email ?: "Não informado"}"

        txtStatusLgpd.text = if (lgpdAccepted) {
            "Termo LGPD: aceito"
        } else {
            "Termo LGPD: pendente"
        }

        txtStatusPerfilSaude.text = if (healthProfileCompleted) {
            "Perfil de saúde: preenchido"
        } else {
            "Perfil de saúde: não preenchido"
        }
    }

    private fun configurarCliques() {
        btnEditarPerfilSaude.setOnClickListener {
            val intent = Intent(this, HealthProfileActivity::class.java)
            startActivity(intent)
        }

        btnAcessibilidade.setOnClickListener {
            val intent = Intent(this, AcessibilidadeActivity::class.java)
            startActivity(intent)
        }

        btnExcluirDados.setOnClickListener {
            confirmarExclusaoDados()
        }

        btnSairContaPrivacidade.setOnClickListener {
            confirmarSaida()
        }

        btnVoltarPrivacidade.setOnClickListener {
            finish()
        }
    }

    private fun confirmarExclusaoDados() {
        AlertDialog.Builder(this)
            .setTitle("Excluir dados")
            .setMessage(
                "Deseja apagar seus dados salvos neste aplicativo? " +
                        "Essa ação removerá o perfil de saúde e registros vinculados ao usuário no banco de dados."
            )
            .setPositiveButton("Excluir") { _, _ ->
                excluirDadosUsuario()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun excluirDadosUsuario() {
        val userId = auth.currentUser?.uid

        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_LONG).show()
            return
        }

        FirebaseDatabase.getInstance().reference
            .child("usuarios")
            .child(userId)
            .removeValue()
            .addOnSuccessListener {
                val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

                sharedPreferences.edit()
                    .putBoolean("health_profile_completed", false)
                    .apply()

                Toast.makeText(
                    this,
                    "Dados do usuário removidos com sucesso.",
                    Toast.LENGTH_LONG
                ).show()

                configurarTela()
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this,
                    "Erro ao excluir dados: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun confirmarSaida() {
        AlertDialog.Builder(this)
            .setTitle("Sair da conta")
            .setMessage("Deseja realmente sair da sua conta?")
            .setPositiveButton("Sair") { _, _ ->
                auth.signOut()
                Toast.makeText(this, "Você saiu da conta.", Toast.LENGTH_SHORT).show()
                voltarParaLogin()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun voltarParaLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}