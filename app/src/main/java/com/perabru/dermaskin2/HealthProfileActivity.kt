package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HealthProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var spinnerAge: Spinner
    private lateinit var spinnerSkinType: Spinner
    private lateinit var spinnerSunExposure: Spinner
    private lateinit var spinnerSunscreen: Spinner
    private lateinit var spinnerFamilyHistory: Spinner
    private lateinit var spinnerPreviousLesions: Spinner
    private lateinit var spinnerOutdoorWork: Spinner
    private lateinit var btnSaveProfile: Button
    private lateinit var btnSkipProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_profile)

        auth = FirebaseAuth.getInstance()

        iniciarComponentes()
        configurarSpinners()
        configurarCliques()
    }

    private fun iniciarComponentes() {
        spinnerAge = findViewById(R.id.spinnerAge)
        spinnerSkinType = findViewById(R.id.spinnerSkinType)
        spinnerSunExposure = findViewById(R.id.spinnerSunExposure)
        spinnerSunscreen = findViewById(R.id.spinnerSunscreen)
        spinnerFamilyHistory = findViewById(R.id.spinnerFamilyHistory)
        spinnerPreviousLesions = findViewById(R.id.spinnerPreviousLesions)
        spinnerOutdoorWork = findViewById(R.id.spinnerOutdoorWork)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)
        btnSkipProfile = findViewById(R.id.btnSkipProfile)
    }

    private fun configurarSpinners() {
        configurarSpinner(
            spinnerAge,
            listOf(
                "Selecione sua faixa etária",
                "Menos de 18 anos",
                "18 a 29 anos",
                "30 a 39 anos",
                "40 a 49 anos",
                "50 a 59 anos",
                "60 anos ou mais"
            )
        )

        configurarSpinner(
            spinnerSkinType,
            listOf(
                "Selecione seu tipo de pele",
                "Muito clara",
                "Clara",
                "Morena clara",
                "Morena",
                "Negra",
                "Não sei informar"
            )
        )

        configurarSpinner(
            spinnerSunExposure,
            listOf(
                "Exposição solar diária",
                "Baixa",
                "Moderada",
                "Alta",
                "Muito alta"
            )
        )

        configurarSpinner(
            spinnerSunscreen,
            listOf(
                "Uso de protetor solar",
                "Uso diariamente",
                "Uso às vezes",
                "Uso raramente",
                "Não uso"
            )
        )

        configurarSpinner(
            spinnerFamilyHistory,
            listOf(
                "Histórico familiar de câncer de pele?",
                "Sim",
                "Não",
                "Não sei informar"
            )
        )

        configurarSpinner(
            spinnerPreviousLesions,
            listOf(
                "Já teve lesões suspeitas antes?",
                "Sim",
                "Não",
                "Não sei informar"
            )
        )

        configurarSpinner(
            spinnerOutdoorWork,
            listOf(
                "Trabalha ou passa muito tempo ao sol?",
                "Sim",
                "Não",
                "Às vezes"
            )
        )
    }

    private fun configurarSpinner(spinner: Spinner, items: List<String>) {
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            items
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun configurarCliques() {
        btnSaveProfile.setOnClickListener {
            salvarPerfil()
        }

        btnSkipProfile.setOnClickListener {
            salvarComoConcluidoEContinuar()
        }
    }

    private fun salvarPerfil() {
        if (!validarCampos()) {
            return
        }

        val userId = auth.currentUser?.uid

        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_LONG).show()
            voltarParaLogin()
            return
        }

        val profileData = hashMapOf(
            "faixaEtaria" to spinnerAge.selectedItem.toString(),
            "tipoPele" to spinnerSkinType.selectedItem.toString(),
            "exposicaoSolar" to spinnerSunExposure.selectedItem.toString(),
            "usoProtetor" to spinnerSunscreen.selectedItem.toString(),
            "historicoFamiliar" to spinnerFamilyHistory.selectedItem.toString(),
            "lesoesAnteriores" to spinnerPreviousLesions.selectedItem.toString(),
            "trabalhoAoSol" to spinnerOutdoorWork.selectedItem.toString(),
            "dataAtualizacao" to System.currentTimeMillis()
        )

        FirebaseDatabase.getInstance().reference
            .child("usuarios")
            .child(userId)
            .child("perfilSaude")
            .setValue(profileData)
            .addOnSuccessListener {
                Toast.makeText(this, "Perfil salvo com sucesso.", Toast.LENGTH_SHORT).show()
                salvarComoConcluidoEContinuar()
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this,
                    "Erro ao salvar perfil: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun validarCampos(): Boolean {
        if (spinnerAge.selectedItemPosition == 0) {
            Toast.makeText(this, "Selecione sua faixa etária.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerSkinType.selectedItemPosition == 0) {
            Toast.makeText(this, "Selecione seu tipo de pele.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerSunExposure.selectedItemPosition == 0) {
            Toast.makeText(this, "Informe sua exposição solar.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerSunscreen.selectedItemPosition == 0) {
            Toast.makeText(this, "Informe o uso de protetor solar.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerFamilyHistory.selectedItemPosition == 0) {
            Toast.makeText(this, "Informe o histórico familiar.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerPreviousLesions.selectedItemPosition == 0) {
            Toast.makeText(this, "Informe se já teve lesões suspeitas.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerOutdoorWork.selectedItemPosition == 0) {
            Toast.makeText(this, "Informe se trabalha ou passa muito tempo ao sol.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun salvarComoConcluidoEContinuar() {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        sharedPreferences.edit()
            .putBoolean("health_profile_completed", true)
            .apply()

        val intent = Intent(this, AnaliseActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun voltarParaLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}