package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ConsentActivity : AppCompatActivity() {

    private lateinit var checkConsent: CheckBox
    private lateinit var btnAcceptConsent: Button
    private lateinit var btnDeclineConsent: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consent)

        iniciarComponentes()
        configurarCliques()
    }

    private fun iniciarComponentes() {
        checkConsent = findViewById(R.id.checkConsent)
        btnAcceptConsent = findViewById(R.id.btnAcceptConsent)
        btnDeclineConsent = findViewById(R.id.btnDeclineConsent)
    }

    private fun configurarCliques() {
        btnAcceptConsent.setOnClickListener {
            aceitarTermo()
        }

        btnDeclineConsent.setOnClickListener {
            Toast.makeText(
                this,
                "Para utilizar o aplicativo, é necessário aceitar o termo.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun aceitarTermo() {
        if (!checkConsent.isChecked) {
            Toast.makeText(
                this,
                "Você precisa marcar a opção de aceite para continuar.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        sharedPreferences.edit()
            .putBoolean("lgpd_accepted", true)
            .apply()

        Toast.makeText(
            this,
            "Termo aceito com sucesso.",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this, HealthProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}