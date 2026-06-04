package com.perabru.dermaskin2

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ValidacaoFotoActivity : AppCompatActivity() {

    private lateinit var txtAreaValidacao: TextView
    private lateinit var imgValidacaoFoto: ImageView

    private lateinit var checkNitida: CheckBox
    private lateinit var checkIluminada: CheckBox
    private lateinit var checkCentralizada: CheckBox
    private lateinit var checkSemSombra: CheckBox

    private lateinit var btnContinuarValidacao: Button
    private lateinit var btnRefazerFoto: Button
    private lateinit var btnVoltarValidacao: Button

    private var areaSelecionada: String = ""
    private var imagePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validacao_foto)

        areaSelecionada = intent.getStringExtra("areaSelecionada") ?: "Não informada"
        imagePath = intent.getStringExtra("imagePath") ?: ""

        iniciarComponentes()
        configurarTela()
        configurarCliques()
    }

    private fun iniciarComponentes() {
        txtAreaValidacao = findViewById(R.id.txtAreaValidacao)
        imgValidacaoFoto = findViewById(R.id.imgValidacaoFoto)

        checkNitida = findViewById(R.id.checkNitida)
        checkIluminada = findViewById(R.id.checkIluminada)
        checkCentralizada = findViewById(R.id.checkCentralizada)
        checkSemSombra = findViewById(R.id.checkSemSombra)

        btnContinuarValidacao = findViewById(R.id.btnContinuarValidacao)
        btnRefazerFoto = findViewById(R.id.btnRefazerFoto)
        btnVoltarValidacao = findViewById(R.id.btnVoltarValidacao)
    }

    private fun configurarTela() {
        txtAreaValidacao.text = "Área da lesão: $areaSelecionada"

        if (imagePath.isBlank()) {
            Toast.makeText(this, "Imagem não encontrada.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val file = File(imagePath)

        if (!file.exists()) {
            Toast.makeText(this, "Arquivo da imagem não existe.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val bitmap = BitmapFactory.decodeFile(imagePath)
        imgValidacaoFoto.setImageBitmap(bitmap)
    }

    private fun configurarCliques() {
        btnContinuarValidacao.setOnClickListener {
            continuarParaQuestionario()
        }

        btnRefazerFoto.setOnClickListener {
            val intent = Intent(this, CapturaImagemActivity::class.java)
            intent.putExtra("areaSelecionada", areaSelecionada)
            startActivity(intent)
            finish()
        }

        btnVoltarValidacao.setOnClickListener {
            val intent = Intent(this, CapturaImagemActivity::class.java)
            intent.putExtra("areaSelecionada", areaSelecionada)
            startActivity(intent)
            finish()
        }

    }

    private fun continuarParaQuestionario() {
        if (!checkNitida.isChecked ||
            !checkIluminada.isChecked ||
            !checkCentralizada.isChecked ||
            !checkSemSombra.isChecked
        ) {
            Toast.makeText(
                this,
                "Confirme todos os critérios de qualidade antes de continuar.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val intent = Intent(this, QuestionarioLesaoActivity::class.java)
        intent.putExtra("areaSelecionada", areaSelecionada)
        intent.putExtra("imagePath", imagePath)
        startActivity(intent)

        // Isso impede voltar sozinho para a validação
        finish()
    }
}