package com.perabru.dermaskin2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

class CapturaImagemActivity : AppCompatActivity() {

    private lateinit var spinnerAreaLesao: Spinner
    private lateinit var imgCapturaPreview: ImageView

    private lateinit var btnAbrirCamera: Button
    private lateinit var btnUsarFoto: Button
    private lateinit var btnTirarOutra: Button

    private lateinit var btnVoltarCaptura: Button
    private lateinit var btnVoltarTopoCaptura: ImageButton

    private var areaSelecionada: String = ""

    // Foto que foi realmente confirmada pela câmera
    private var imagePath: String = ""

    // Arquivo temporário enquanto a câmera está aberta
    private var fotoPendentePath: String = ""


    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { permitido ->

            if (permitido) {
                prepararEAbrirCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissão da câmera negada.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    /*
     * TakePicture() salva a fotografia completa no arquivo.
     * Não é mais uma miniatura como TakePicturePreview().
     */
    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { sucesso ->

            if (sucesso && fotoPendentePath.isNotBlank()) {

                imagePath = fotoPendentePath

                imgCapturaPreview.setImageURI(
                    Uri.fromFile(File(imagePath))
                )

                // Depois que já existe uma foto,
                // não precisamos mais do botão "Abrir câmera"
                btnAbrirCamera.visibility = View.GONE

                btnUsarFoto.isEnabled = true
                btnUsarFoto.alpha = 1.0f

                btnAbrirCamera.visibility = View.GONE

                btnUsarFoto.visibility = View.VISIBLE
                btnUsarFoto.isEnabled = true
                btnUsarFoto.alpha = 1.0f

                btnTirarOutra.isEnabled = true
                btnTirarOutra.alpha = 1.0f

                btnTirarOutra.isEnabled = true
                btnTirarOutra.alpha = 1.0f

            } else {

                // Se cancelar a câmera, apaga somente o arquivo temporário
                if (fotoPendentePath.isNotBlank()) {

                    val arquivoPendente = File(fotoPendentePath)

                    if (arquivoPendente.exists() &&
                        fotoPendentePath != imagePath
                    ) {
                        arquivoPendente.delete()
                    }
                }

                Toast.makeText(
                    this,
                    "Captura cancelada.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            fotoPendentePath = ""

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_captura_imagem)

        iniciarComponentes()
        configurarSpinnerArea()
        configurarEstadoInicial()
        configurarCliques()
    }

    private fun iniciarComponentes() {

        spinnerAreaLesao =
            findViewById(R.id.spinnerAreaLesao)

        imgCapturaPreview =
            findViewById(R.id.imgCapturaPreview)

        btnAbrirCamera =
            findViewById(R.id.btnAbrirCamera)

        btnUsarFoto =
            findViewById(R.id.btnUsarFoto)

        btnTirarOutra =
            findViewById(R.id.btnTirarOutra)

        btnVoltarCaptura =
            findViewById(R.id.btnVoltarCaptura)

        btnVoltarTopoCaptura =
            findViewById(R.id.btnVoltarTopoCaptura)
    }

    private fun configurarSpinnerArea() {

        val areas = listOf(
            "Selecione a região",
            "Rosto / cabeça",
            "Pescoço",
            "Braço",
            "Mão",
            "Tórax / abdômen",
            "Costas",
            "Perna",
            "Pé",
            "Outra região"
        )

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            areas
        )

        adapter.setDropDownViewResource(
            R.layout.spinner_dropdown_item
        )

        spinnerAreaLesao.adapter = adapter

        spinnerAreaLesao.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    areaSelecionada =
                        if (position == 0) {
                            ""
                        } else {
                            areas[position]
                        }
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                    areaSelecionada = ""
                }
            }
    }

    private fun configurarEstadoInicial() {

        btnAbrirCamera.visibility = View.VISIBLE

        btnUsarFoto.visibility = View.GONE
        btnUsarFoto.isEnabled = false
        btnUsarFoto.alpha = 0.55f

        btnTirarOutra.isEnabled = false
        btnTirarOutra.alpha = 0.55f
    }

    private fun configurarCliques() {

        btnAbrirCamera.setOnClickListener {
            verificarAreaAntesDaCamera()
        }

        btnTirarOutra.setOnClickListener {
            verificarAreaAntesDaCamera()
        }

        btnUsarFoto.setOnClickListener {
            continuarParaValidacao()
        }

        btnVoltarCaptura.setOnClickListener {
            finish()
        }

        btnVoltarTopoCaptura.setOnClickListener {
            finish()
        }
    }

    private fun verificarAreaAntesDaCamera() {

        if (areaSelecionada.isBlank()) {

            Toast.makeText(
                this,
                "Selecione a região da lesão antes de abrir a câmera.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        verificarPermissaoCamera()
    }

    private fun verificarPermissaoCamera() {

        val permissao = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )

        if (permissao == PackageManager.PERMISSION_GRANTED) {

            prepararEAbrirCamera()

        } else {

            cameraPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    private fun prepararEAbrirCamera() {

        try {

            val pasta = File(
                cacheDir,
                "capturas"
            )

            if (!pasta.exists()) {
                pasta.mkdirs()
            }

            val arquivo = File(
                pasta,
                "lesao_${System.currentTimeMillis()}.jpg"
            )

            fotoPendentePath = arquivo.absolutePath

            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                arquivo
            )

            cameraLauncher.launch(uri)

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Não foi possível abrir a câmera.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun continuarParaValidacao() {

        if (areaSelecionada.isBlank()) {

            Toast.makeText(
                this,
                "Selecione a região da lesão.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (imagePath.isBlank()) {

            Toast.makeText(
                this,
                "Tire uma foto antes de continuar.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val arquivo = File(imagePath)

        if (!arquivo.exists() || arquivo.length() == 0L) {

            Toast.makeText(
                this,
                "A imagem capturada não foi encontrada.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val intent =
            Intent(this, ValidacaoFotoActivity::class.java)

        intent.putExtra(
            "areaSelecionada",
            areaSelecionada
        )

        intent.putExtra(
            "imagePath",
            imagePath
        )

        startActivity(intent)
    }
}