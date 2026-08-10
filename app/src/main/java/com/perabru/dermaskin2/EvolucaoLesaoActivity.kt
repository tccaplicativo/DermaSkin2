package com.perabru.dermaskin2

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EvolucaoLesaoActivity : AppCompatActivity() {

    private lateinit var btnVoltarEvolucao: ImageButton

    private lateinit var txtNomeLesaoEvolucao: TextView
    private lateinit var txtResumoEvolucao: TextView

    private lateinit var imgPrimeiroRegistro: ImageView
    private lateinit var imgUltimoRegistro: ImageView

    private lateinit var txtDataPrimeiroRegistro: TextView
    private lateinit var txtDataUltimoRegistro: TextView

    private lateinit var graficoEvolucao: GraficoEvolucaoView

    private lateinit var containerRegistrosEvolucao: LinearLayout

    private lateinit var btnRegistrarNovaFoto: LinearLayout

    private lateinit var layoutCarregandoEvolucao: LinearLayout
    private lateinit var layoutConteudoEvolucao: LinearLayout

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val database by lazy {
        FirebaseDatabase.getInstance().reference
    }

    private var monitoramentoId: String = ""

    private var nomeLesao: String = ""
    private var areaSelecionada: String = ""

    private val registros = mutableListOf<RegistroEvolucao>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_evolucao_lesao
        )

        monitoramentoId =
            intent.getStringExtra(
                "monitoramentoId"
            ) ?: ""

        iniciarComponentes()
        configurarCliques()

        if (monitoramentoId.isBlank()) {

            Toast.makeText(
                this,
                "Não foi possível identificar esta lesão.",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }

        carregarEvolucao()
    }

    private fun iniciarComponentes() {

        btnVoltarEvolucao =
            findViewById(
                R.id.btnVoltarEvolucao
            )

        txtNomeLesaoEvolucao =
            findViewById(
                R.id.txtNomeLesaoEvolucao
            )

        txtResumoEvolucao =
            findViewById(
                R.id.txtResumoEvolucao
            )

        imgPrimeiroRegistro =
            findViewById(
                R.id.imgPrimeiroRegistro
            )

        imgUltimoRegistro =
            findViewById(
                R.id.imgUltimoRegistro
            )

        txtDataPrimeiroRegistro =
            findViewById(
                R.id.txtDataPrimeiroRegistro
            )

        txtDataUltimoRegistro =
            findViewById(
                R.id.txtDataUltimoRegistro
            )

        graficoEvolucao =
            findViewById(
                R.id.graficoEvolucao
            )

        containerRegistrosEvolucao =
            findViewById(
                R.id.containerRegistrosEvolucao
            )

        btnRegistrarNovaFoto =
            findViewById(
                R.id.btnRegistrarNovaFoto
            )

        layoutCarregandoEvolucao =
            findViewById(
                R.id.layoutCarregandoEvolucao
            )

        layoutConteudoEvolucao =
            findViewById(
                R.id.layoutConteudoEvolucao
            )
    }

    private fun configurarCliques() {

        btnVoltarEvolucao.setOnClickListener {

            finish()
        }

        btnRegistrarNovaFoto.setOnClickListener {

            registrarNovaFoto()
        }
    }

    private fun carregarEvolucao() {

        val uid =
            auth.currentUser?.uid

        if (uid == null) {

            Toast.makeText(
                this,
                "Usuário não identificado.",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }

        layoutCarregandoEvolucao.visibility =
            View.VISIBLE

        layoutConteudoEvolucao.visibility =
            View.GONE

        val referenciaMonitoramento =
            database
                .child("usuarios")
                .child(uid)
                .child("monitoramentos")
                .child(monitoramentoId)

        referenciaMonitoramento
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        if (!snapshot.exists()) {

                            Toast.makeText(
                                this@EvolucaoLesaoActivity,
                                "Acompanhamento não encontrado.",
                                Toast.LENGTH_LONG
                            ).show()

                            finish()

                            return
                        }

                        nomeLesao =
                            snapshot
                                .child("nome")
                                .getValue(String::class.java)
                                ?: "Lesão acompanhada"

                        areaSelecionada =
                            snapshot
                                .child("areaSelecionada")
                                .getValue(String::class.java)
                                ?: "Área não informada"

                        val idsRegistros =
                            snapshot
                                .child("registros")
                                .children
                                .mapNotNull {

                                    it.child("analiseId")
                                        .getValue(String::class.java)
                                }

                        if (idsRegistros.isEmpty()) {

                            mostrarSemRegistros()

                            return
                        }

                        carregarAnalisesHistorico(
                            uid,
                            idsRegistros
                        )
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        Toast.makeText(
                            this@EvolucaoLesaoActivity,
                            "Não foi possível carregar o acompanhamento.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }

    private fun carregarAnalisesHistorico(
        uid: String,
        idsRegistros: List<String>
    ) {

        val referenciaHistorico =
            database
                .child("usuarios")
                .child(uid)
                .child("historicoAnalises")

        referenciaHistorico
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(
                        snapshot: DataSnapshot
                    ) {

                        registros.clear()

                        idsRegistros.forEach { analiseId ->

                            val analise =
                                encontrarAnalise(
                                    snapshot,
                                    analiseId
                                )

                            if (analise != null) {

                                registros.add(
                                    converterRegistro(
                                        analise
                                    )
                                )
                            }
                        }

                        registros.sortBy {
                            it.timestamp
                        }

                        configurarTela()
                    }

                    override fun onCancelled(
                        error: DatabaseError
                    ) {

                        Toast.makeText(
                            this@EvolucaoLesaoActivity,
                            "Não foi possível carregar as análises.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }

    private fun encontrarAnalise(
        historico: DataSnapshot,
        analiseId: String
    ): DataSnapshot? {

        val peloIdDireto =
            historico.child(
                analiseId
            )

        if (peloIdDireto.exists()) {

            return peloIdDireto
        }

        return historico.children.firstOrNull {

            it.child("id")
                .getValue(String::class.java) ==
                    analiseId
        }
    }

    private fun converterRegistro(
        snapshot: DataSnapshot
    ): RegistroEvolucao {

        val id =
            snapshot.child("id")
                .getValue(String::class.java)
                ?: snapshot.key
                ?: ""

        val timestamp =
            snapshot.child("timestamp")
                .getValue(Long::class.java)
                ?: 0L

        val imagePath =
            snapshot.child("imagePath")
                .getValue(String::class.java)
                ?: ""

        val nivelAtencao =
            snapshot.child("nivelAtencao")
                .getValue(String::class.java)
                ?: "Não informado"

        val riscoDescricao =
            snapshot.child("riscoDescricao")
                .getValue(String::class.java)
                ?: ""

        val classificacaoIA =
            snapshot.child("classificacaoIA")
                .getValue(String::class.java)
                ?: "Não informada"

        val score =
            snapshot.child("scoreSuspeitaIA")
                .getValue(Double::class.java)
                ?: 0.0

        val tempoLesao =
            snapshot.child("tempoLesao")
                .getValue(String::class.java)
                ?: "Não informado"

        val mudancaTamanho =
            snapshot.child("mudancaTamanho")
                .getValue(String::class.java)
                ?: "Não informado"

        val mudancaCor =
            snapshot.child("mudancaCor")
                .getValue(String::class.java)
                ?: "Não informado"

        val coceira =
            snapshot.child("coceira")
                .getValue(String::class.java)
                ?: "Não informado"

        val sangramento =
            snapshot.child("sangramento")
                .getValue(String::class.java)
                ?: "Não informado"

        val dor =
            snapshot.child("dor")
                .getValue(String::class.java)
                ?: "Não informado"

        val formatoIrregular =
            snapshot.child("formatoIrregular")
                .getValue(String::class.java)
                ?: "Não informado"

        return RegistroEvolucao(
            id = id,
            timestamp = timestamp,
            imagePath = imagePath,
            nivelAtencao = nivelAtencao,
            riscoDescricao = riscoDescricao,
            classificacaoIA = classificacaoIA,
            scoreSuspeitaIA = score,
            tempoLesao = tempoLesao,
            mudancaTamanho = mudancaTamanho,
            mudancaCor = mudancaCor,
            coceira = coceira,
            sangramento = sangramento,
            dor = dor,
            formatoIrregular = formatoIrregular
        )
    }

    private fun configurarTela() {

        layoutCarregandoEvolucao.visibility =
            View.GONE

        layoutConteudoEvolucao.visibility =
            View.VISIBLE

        txtNomeLesaoEvolucao.text =
            nomeLesao

        txtResumoEvolucao.text =
            if (registros.size == 1) {

                "1 registro no acompanhamento"

            } else {

                "${registros.size} registros no acompanhamento"
            }

        if (registros.isEmpty()) {

            mostrarSemRegistros()

            return
        }

        val primeiro =
            registros.first()

        val ultimo =
            registros.last()

        carregarImagem(
            primeiro.imagePath,
            imgPrimeiroRegistro
        )

        carregarImagem(
            ultimo.imagePath,
            imgUltimoRegistro
        )

        txtDataPrimeiroRegistro.text =
            formatarData(
                primeiro.timestamp
            )

        txtDataUltimoRegistro.text =
            formatarData(
                ultimo.timestamp
            )

        configurarGrafico()
        criarTimeline()
    }

    private fun configurarGrafico() {

        val pontos =
            registros.map {

                PontoGrafico(
                    data = formatarDataCurta(
                        it.timestamp
                    ),
                    valor = it.scoreSuspeitaIA.toFloat()
                )
            }

        graficoEvolucao.setDados(
            pontos
        )
    }

    private fun criarTimeline() {

        containerRegistrosEvolucao.removeAllViews()

        registros
            .sortedByDescending {
                it.timestamp
            }
            .forEachIndexed { index, registro ->

                val view =
                    LayoutInflater
                        .from(this)
                        .inflate(
                            R.layout.item_registro_evolucao,
                            containerRegistrosEvolucao,
                            false
                        )

                val imgRegistro =
                    view.findViewById<ImageView>(
                        R.id.imgRegistroEvolucao
                    )

                val txtData =
                    view.findViewById<TextView>(
                        R.id.txtDataRegistroEvolucao
                    )

                val txtNivel =
                    view.findViewById<TextView>(
                        R.id.txtNivelRegistroEvolucao
                    )

                val txtScore =
                    view.findViewById<TextView>(
                        R.id.txtScoreRegistroEvolucao
                    )

                val txtTipoRegistro =
                    view.findViewById<TextView>(
                        R.id.txtTipoRegistroEvolucao
                    )

                val btnVerAnalise =
                    view.findViewById<LinearLayout>(
                        R.id.btnVerAnaliseRegistro
                    )

                carregarImagem(
                    registro.imagePath,
                    imgRegistro
                )

                txtData.text =
                    formatarDataCompleta(
                        registro.timestamp
                    )

                txtNivel.text =
                    registro.nivelAtencao

                txtScore.text =
                    "Score visual da IA: ${
                        formatarScore(
                            registro.scoreSuspeitaIA
                        )
                    }"

                txtTipoRegistro.text =
                    if (
                        index ==
                        registros.size - 1
                    ) {

                        "Registro inicial"

                    } else {

                        "Acompanhamento"
                    }

                btnVerAnalise.setOnClickListener {

                    abrirDetalhesAnalise(
                        registro
                    )
                }

                containerRegistrosEvolucao.addView(
                    view
                )
            }
    }

    private fun abrirDetalhesAnalise(
        registro: RegistroEvolucao
    ) {

        val intent =
            Intent(
                this,
                DetalhesAnaliseActivity::class.java
            )

        intent.putExtra(
            "timestamp",
            registro.timestamp
        )

        intent.putExtra(
            "areaSelecionada",
            areaSelecionada
        )

        intent.putExtra(
            "imagePath",
            registro.imagePath
        )

        intent.putExtra(
            "nivelAtencao",
            registro.nivelAtencao
        )

        intent.putExtra(
            "riscoTitulo",
            registro.nivelAtencao
        )

        intent.putExtra(
            "riscoDescricao",
            registro.riscoDescricao
        )

        intent.putExtra(
            "classificacaoIA",
            registro.classificacaoIA
        )

        intent.putExtra(
            "scoreSuspeitaIA",
            registro.scoreSuspeitaIA.toFloat()
        )

        intent.putExtra(
            "riscoPercentual",
            formatarScore(
                registro.scoreSuspeitaIA
            )
        )

        intent.putExtra(
            "tempoLesao",
            registro.tempoLesao
        )

        intent.putExtra(
            "mudancaTamanho",
            registro.mudancaTamanho
        )

        intent.putExtra(
            "mudancaCor",
            registro.mudancaCor
        )

        intent.putExtra(
            "coceira",
            registro.coceira
        )

        intent.putExtra(
            "sangramento",
            registro.sangramento
        )

        intent.putExtra(
            "dor",
            registro.dor
        )

        intent.putExtra(
            "formatoIrregular",
            registro.formatoIrregular
        )

        startActivity(
            intent
        )
    }

    private fun registrarNovaFoto() {

        val intent =
            Intent(
                this,
                GuiaCapturaActivity::class.java
            )

        intent.putExtra(
            "modoMonitoramento",
            true
        )

        intent.putExtra(
            "monitoramentoId",
            monitoramentoId
        )

        intent.putExtra(
            "areaSelecionada",
            areaSelecionada
        )

        intent.putExtra(
            "nomeLesao",
            nomeLesao
        )

        startActivity(
            intent
        )
    }

    private fun carregarImagem(
        imagePath: String,
        imageView: ImageView
    ) {

        if (imagePath.isBlank()) {

            imageView.setImageResource(
                R.drawable.ic_skin_placeholder
            )

            return
        }

        val arquivo =
            File(
                imagePath
            )

        if (!arquivo.exists()) {

            imageView.setImageResource(
                R.drawable.ic_skin_placeholder
            )

            return
        }

        val bitmap =
            BitmapFactory.decodeFile(
                imagePath
            )

        if (bitmap != null) {

            imageView.setImageBitmap(
                bitmap
            )

        } else {

            imageView.setImageResource(
                R.drawable.ic_skin_placeholder
            )
        }
    }

    private fun mostrarSemRegistros() {

        layoutCarregandoEvolucao.visibility =
            View.GONE

        layoutConteudoEvolucao.visibility =
            View.VISIBLE

        txtNomeLesaoEvolucao.text =
            nomeLesao

        txtResumoEvolucao.text =
            "Nenhum registro disponível."
    }

    private fun formatarData(
        timestamp: Long
    ): String {

        if (timestamp <= 0L) {

            return "--/--/----"
        }

        return SimpleDateFormat(
            "dd/MM/yyyy",
            Locale("pt", "BR")
        ).format(
            Date(timestamp)
        )
    }

    private fun formatarDataCurta(
        timestamp: Long
    ): String {

        if (timestamp <= 0L) {

            return "--/--"
        }

        return SimpleDateFormat(
            "dd/MM",
            Locale("pt", "BR")
        ).format(
            Date(timestamp)
        )
    }

    private fun formatarDataCompleta(
        timestamp: Long
    ): String {

        if (timestamp <= 0L) {

            return "Data não informada"
        }

        return SimpleDateFormat(
            "dd/MM/yyyy • HH:mm",
            Locale("pt", "BR")
        ).format(
            Date(timestamp)
        )
    }

    private fun formatarScore(
        score: Double
    ): String {

        val percentual =
            if (score <= 1.0) {

                score * 100.0

            } else {

                score
            }

        return String.format(
            Locale("pt", "BR"),
            "%.1f%%",
            percentual
        )
    }
}

data class RegistroEvolucao(
    val id: String,
    val timestamp: Long,
    val imagePath: String,
    val nivelAtencao: String,
    val riscoDescricao: String,
    val classificacaoIA: String,
    val scoreSuspeitaIA: Double,
    val tempoLesao: String,
    val mudancaTamanho: String,
    val mudancaCor: String,
    val coceira: String,
    val sangramento: String,
    val dor: String,
    val formatoIrregular: String
)