package com.perabru.dermaskin2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class QuestionarioLesaoActivity : AppCompatActivity() {

    private lateinit var spinnerTempoLesao: Spinner
    private lateinit var spinnerMudancaTamanho: Spinner
    private lateinit var spinnerMudancaCor: Spinner
    private lateinit var spinnerCoceira: Spinner
    private lateinit var spinnerSangramento: Spinner
    private lateinit var spinnerDor: Spinner
    private lateinit var spinnerFormato: Spinner

    private lateinit var checkTermoQuestionario: CheckBox

    private lateinit var btnContinuarQuestionario: Button
    private lateinit var btnVoltarQuestionario: Button

    private lateinit var tvProgressQuestionario: TextView
    private lateinit var viewProgressQuestionario: View
    private lateinit var progressTrackQuestionario: FrameLayout
    private lateinit var listaSpinners: List<Spinner>

    private var areaSelecionada: String = ""
    private var imagePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionario_lesao)

        areaSelecionada = intent.getStringExtra("areaSelecionada") ?: "Não informada"
        imagePath = intent.getStringExtra("imagePath") ?: ""

        iniciarComponentes()
        configurarSpinners()
        configurarCliques()
        configurarProgresso()
    }

    private fun iniciarComponentes() {
        spinnerTempoLesao = findViewById(R.id.spinnerTempoLesao)
        spinnerMudancaTamanho = findViewById(R.id.spinnerMudancaTamanho)
        spinnerMudancaCor = findViewById(R.id.spinnerMudancaCor)
        spinnerCoceira = findViewById(R.id.spinnerCoceira)
        spinnerSangramento = findViewById(R.id.spinnerSangramento)
        spinnerDor = findViewById(R.id.spinnerDor)
        spinnerFormato = findViewById(R.id.spinnerFormato)

        checkTermoQuestionario = findViewById(R.id.checkTermoQuestionario)

        btnContinuarQuestionario = findViewById(R.id.btnContinuarQuestionario)
        btnVoltarQuestionario = findViewById(R.id.btnVoltarQuestionario)

        tvProgressQuestionario = findViewById(R.id.tvProgressQuestionario)
        viewProgressQuestionario = findViewById(R.id.viewProgressQuestionario)
        progressTrackQuestionario = findViewById(R.id.progressTrackQuestionario)

        listaSpinners = listOf(
            spinnerTempoLesao,
            spinnerMudancaTamanho,
            spinnerMudancaCor,
            spinnerCoceira,
            spinnerSangramento,
            spinnerDor,
            spinnerFormato
        )
    }

    private fun configurarSpinners() {
        configurarSpinner(
            spinnerTempoLesao,
            listOf(
                "Há quanto tempo percebeu a lesão?",
                "Menos de 1 semana",
                "1 a 4 semanas",
                "1 a 6 meses",
                "Mais de 6 meses",
                "Não sei informar"
            )
        )

        configurarSpinner(
            spinnerMudancaTamanho,
            listOf(
                "A lesão aumentou de tamanho?",
                "Sim",
                "Não",
                "Não sei informar"
            )
        )

        configurarSpinner(
            spinnerMudancaCor,
            listOf(
                "A lesão mudou de cor?",
                "Sim",
                "Não",
                "Não sei informar"
            )
        )

        configurarSpinner(
            spinnerCoceira,
            listOf(
                "A lesão coça?",
                "Sim",
                "Não",
                "Às vezes"
            )
        )

        configurarSpinner(
            spinnerSangramento,
            listOf(
                "A lesão sangra ou já sangrou?",
                "Sim",
                "Não",
                "Não sei informar"
            )
        )

        configurarSpinner(
            spinnerDor,
            listOf(
                "A lesão causa dor?",
                "Sim",
                "Não",
                "Às vezes"
            )
        )

        configurarSpinner(
            spinnerFormato,
            listOf(
                "O formato parece irregular?",
                "Sim",
                "Não",
                "Não sei informar"
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
        btnContinuarQuestionario.setOnClickListener {
            continuarParaProcessamento()
        }

        btnVoltarQuestionario.setOnClickListener {
            finish()
        }
    }

    private fun configurarProgresso() {
        listaSpinners.forEach { spinner ->
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    atualizarProgresso()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        progressTrackQuestionario.post {
            atualizarProgresso()
        }
    }

    private fun atualizarProgresso() {
        val total = listaSpinners.size
        val respondidas = listaSpinners.count { it.selectedItemPosition > 0 }

        tvProgressQuestionario.text = "$respondidas/$total"

        progressTrackQuestionario.post {
            val larguraTrack = progressTrackQuestionario.width

            val novaLargura = if (respondidas == 0) {
                0
            } else {
                ((larguraTrack * respondidas.toFloat()) / total).toInt()
            }

            val params = viewProgressQuestionario.layoutParams
            params.width = novaLargura
            viewProgressQuestionario.layoutParams = params
        }
    }

    private fun continuarParaProcessamento() {
        if (!validarCampos()) {
            return
        }

        if (!checkTermoQuestionario.isChecked) {
            Toast.makeText(
                this,
                "Confirme que entende que o app não substitui consulta médica.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val intent = Intent(this, ProcessamentoiaActivity::class.java)

        intent.putExtra("areaSelecionada", areaSelecionada)
        intent.putExtra("imagePath", imagePath)

        intent.putExtra("tempoLesao", spinnerTempoLesao.selectedItem.toString())
        intent.putExtra("mudancaTamanho", spinnerMudancaTamanho.selectedItem.toString())
        intent.putExtra("mudancaCor", spinnerMudancaCor.selectedItem.toString())
        intent.putExtra("coceira", spinnerCoceira.selectedItem.toString())
        intent.putExtra("sangramento", spinnerSangramento.selectedItem.toString())
        intent.putExtra("dor", spinnerDor.selectedItem.toString())
        intent.putExtra("formatoIrregular", spinnerFormato.selectedItem.toString())

        startActivity(intent)
        finish()
    }

    private fun validarCampos(): Boolean {
        if (imagePath.isBlank()) {
            Toast.makeText(this, "Imagem não encontrada.", Toast.LENGTH_LONG).show()
            return false
        }

        if (spinnerTempoLesao.selectedItemPosition == 0) {
            Toast.makeText(this, "Informe há quanto tempo percebeu a lesão.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerMudancaTamanho.selectedItemPosition == 0) {
            Toast.makeText(this, "Informe se a lesão aumentou de tamanho.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerMudancaCor.selectedItemPosition == 0) {
            Toast.makeText(this, "Informe se a lesão mudou de cor.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerCoceira.selectedItemPosition == 0) {
            Toast.makeText(this, "Informe se a lesão coça.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerSangramento.selectedItemPosition == 0) {
            Toast.makeText(this, "Informe se a lesão sangra.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerDor.selectedItemPosition == 0) {
            Toast.makeText(this, "Informe se a lesão causa dor.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (spinnerFormato.selectedItemPosition == 0) {
            Toast.makeText(this, "Informe se o formato parece irregular.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}