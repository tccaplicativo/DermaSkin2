package com.perabru.dermaskin2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream

class CapturaImagemActivity : AppCompatActivity() {

    private lateinit var txtAreaCaptura: TextView
    private lateinit var imgCapturaPreview: ImageView
    private lateinit var btnAbrirCamera: Button
    private lateinit var btnUsarFoto: Button
    private lateinit var btnTirarOutra: Button
    private lateinit var btnVoltarCaptura: Button

    private var areaSelecionada: String = ""
    private var fotoBitmap: Bitmap? = null
    private var imagePath: String = ""

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permitido ->
            if (permitido) {
                abrirCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissão da câmera negada.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                fotoBitmap = bitmap
                imgCapturaPreview.setImageBitmap(bitmap)

                btnUsarFoto.isEnabled = true
                btnUsarFoto.alpha = 1.0f

                btnTirarOutra.isEnabled = true
                btnTirarOutra.alpha = 1.0f
            } else {
                Toast.makeText(
                    this,
                    "Nenhuma imagem foi capturada.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_captura_imagem)

        areaSelecionada = intent.getStringExtra("areaSelecionada") ?: "Não informada"

        iniciarComponentes()
        configurarTela()
        configurarCliques()
        configurarEstadoInicial()
    }

    private fun iniciarComponentes() {
        txtAreaCaptura = findViewById(R.id.txtAreaCaptura)
        imgCapturaPreview = findViewById(R.id.imgCapturaPreview)
        btnAbrirCamera = findViewById(R.id.btnAbrirCamera)
        btnUsarFoto = findViewById(R.id.btnUsarFoto)
        btnTirarOutra = findViewById(R.id.btnTirarOutra)
        btnVoltarCaptura = findViewById(R.id.btnVoltarCaptura)
    }

    private fun configurarTela() {
        txtAreaCaptura.text = "Área da lesão: $areaSelecionada"
    }

    private fun configurarEstadoInicial() {
        btnUsarFoto.isEnabled = false
        btnUsarFoto.alpha = 0.55f

        btnTirarOutra.isEnabled = false
        btnTirarOutra.alpha = 0.55f
    }

    private fun configurarCliques() {
        btnAbrirCamera.setOnClickListener {
            verificarPermissaoCamera()
        }

        btnTirarOutra.setOnClickListener {
            verificarPermissaoCamera()
        }

        btnUsarFoto.setOnClickListener {
            continuarParaValidacao()
        }

        btnVoltarCaptura.setOnClickListener {
            finish()
        }
    }

    private fun verificarPermissaoCamera() {
        val permissao = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )

        if (permissao == PackageManager.PERMISSION_GRANTED) {
            abrirCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun abrirCamera() {
        cameraLauncher.launch(null)
    }

    private fun continuarParaValidacao() {
        val bitmap = fotoBitmap

        if (bitmap == null) {
            Toast.makeText(
                this,
                "Tire uma foto antes de continuar.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        imagePath = salvarImagemNoCache(bitmap)

        if (imagePath.isBlank()) {
            Toast.makeText(
                this,
                "Erro ao salvar a imagem.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val intent = Intent(this, ValidacaoFotoActivity::class.java)
        intent.putExtra("areaSelecionada", areaSelecionada)
        intent.putExtra("imagePath", imagePath)
        startActivity(intent)
    }

    private fun salvarImagemNoCache(bitmap: Bitmap): String {
        return try {
            val pasta = File(cacheDir, "capturas")

            if (!pasta.exists()) {
                pasta.mkdirs()
            }

            val arquivo = File(
                pasta,
                "lesao_${System.currentTimeMillis()}.jpg"
            )

            FileOutputStream(arquivo).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, outputStream)
            }

            arquivo.absolutePath

        } catch (e: Exception) {
            ""
        }
    }
}