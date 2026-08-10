package com.perabru.dermaskin2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
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

    private lateinit var btnVoltarTopoValidacao: ImageButton
    private lateinit var btnVoltarValidacao: Button

    private var areaSelecionada: String = ""
    private var imagePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validacao_foto)

        areaSelecionada =
            intent.getStringExtra("areaSelecionada") ?: "Não informada"

        imagePath =
            intent.getStringExtra("imagePath") ?: ""

        iniciarComponentes()
        configurarTela()
        configurarCliques()
    }

    private fun iniciarComponentes() {

        txtAreaValidacao =
            findViewById(R.id.txtAreaValidacao)

        imgValidacaoFoto =
            findViewById(R.id.imgValidacaoFoto)

        checkNitida =
            findViewById(R.id.checkNitida)

        checkIluminada =
            findViewById(R.id.checkIluminada)

        checkCentralizada =
            findViewById(R.id.checkCentralizada)

        checkSemSombra =
            findViewById(R.id.checkSemSombra)

        btnVoltarTopoValidacao =
            findViewById(R.id.btnVoltarTopoValidacao)

        btnContinuarValidacao =
            findViewById(R.id.btnContinuarValidacao)

        btnRefazerFoto =
            findViewById(R.id.btnRefazerFoto)

        btnVoltarValidacao =
            findViewById(R.id.btnVoltarValidacao)
    }

    private fun configurarTela() {

        txtAreaValidacao.text =
            "Área da lesão: $areaSelecionada"

        if (imagePath.isBlank()) {

            Toast.makeText(
                this,
                "Imagem não encontrada.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        val file = File(imagePath)

        if (!file.exists()) {

            Toast.makeText(
                this,
                "Arquivo da imagem não existe.",
                Toast.LENGTH_LONG
            ).show()

            finish()
            return
        }

        try {

            val bitmapCorrigido =
                carregarImagemComOrientacaoCorreta(imagePath)

            if (bitmapCorrigido != null) {

                imgValidacaoFoto.setImageBitmap(
                    bitmapCorrigido
                )

            } else {

                Toast.makeText(
                    this,
                    "Não foi possível carregar a imagem.",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Erro ao carregar a imagem.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /*
     * Carrega a fotografia respeitando a orientação EXIF
     * registrada pela câmera.
     */
    private fun carregarImagemComOrientacaoCorreta(
        caminho: String
    ): Bitmap? {

        /*
         * Primeiro verificamos o tamanho da foto.
         * Isso evita carregar uma imagem gigantesca inteira
         * apenas para mostrar no preview da tela.
         */
        val optionsBounds =
            BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

        BitmapFactory.decodeFile(
            caminho,
            optionsBounds
        )

        /*
         * Para a tela de validação não precisamos colocar
         * uma foto de 4000 ou 6000 pixels inteira na memória.
         *
         * O arquivo original NÃO é alterado.
         */
        var inSampleSize = 1

        val larguraDesejada = 1200
        val alturaDesejada = 1200

        while (
            optionsBounds.outWidth / inSampleSize > larguraDesejada * 2 ||
            optionsBounds.outHeight / inSampleSize > alturaDesejada * 2
        ) {
            inSampleSize *= 2
        }

        val options =
            BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
            }

        val bitmap =
            BitmapFactory.decodeFile(
                caminho,
                options
            ) ?: return null

        /*
         * Lê a orientação salva pela câmera.
         */
        val exif =
            ExifInterface(caminho)

        val orientation =
            exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

        val matrix = Matrix()

        when (orientation) {

            ExifInterface.ORIENTATION_ROTATE_90 -> {
                matrix.postRotate(90f)
            }

            ExifInterface.ORIENTATION_ROTATE_180 -> {
                matrix.postRotate(180f)
            }

            ExifInterface.ORIENTATION_ROTATE_270 -> {
                matrix.postRotate(270f)
            }

            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
                matrix.postScale(-1f, 1f)
            }

            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.postScale(1f, -1f)
            }

            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postRotate(90f)
                matrix.postScale(-1f, 1f)
            }

            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.postRotate(270f)
                matrix.postScale(-1f, 1f)
            }
        }

        return if (!matrix.isIdentity) {

            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )

        } else {

            bitmap
        }
    }

    private fun configurarCliques() {

        btnContinuarValidacao.setOnClickListener {
            continuarParaQuestionario()
        }

        btnRefazerFoto.setOnClickListener {

            val intent =
                Intent(
                    this,
                    CapturaImagemActivity::class.java
                )

            intent.putExtra(
                "areaSelecionada",
                areaSelecionada
            )

            startActivity(intent)
            finish()
        }

        btnVoltarTopoValidacao.setOnClickListener {
            finish()
        }

        btnVoltarValidacao.setOnClickListener {

            val intent =
                Intent(
                    this,
                    CapturaImagemActivity::class.java
                )

            intent.putExtra(
                "areaSelecionada",
                areaSelecionada
            )

            startActivity(intent)
            finish()
        }
    }

    private fun continuarParaQuestionario() {

        if (
            !checkNitida.isChecked ||
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

        val intent =
            Intent(
                this,
                QuestionarioLesaoActivity::class.java
            )

        intent.putExtra(
            "areaSelecionada",
            areaSelecionada
        )

        intent.putExtra(
            "imagePath",
            imagePath
        )

        startActivity(intent)

        // Impede voltar sozinho para a validação
        finish()
    }
}