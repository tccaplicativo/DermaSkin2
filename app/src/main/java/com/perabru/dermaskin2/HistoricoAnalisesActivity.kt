package com.perabru.dermaskin2

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoricoAnalisesActivity : AppCompatActivity() {

    private lateinit var containerHistorico: LinearLayout
    private lateinit var layoutEstadoVazio: LinearLayout
    private lateinit var txtEstadoVazioTitulo: TextView
    private lateinit var txtEstadoVazioDescricao: TextView

    private lateinit var btnNovaAnaliseHistorico: Button
    private lateinit var btnVoltarHistorico: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_historico_analises)

        iniciarComponentes()
        configurarCliques()
    }

    /*
     * Sempre que entrar ou voltar para esta tela,
     * o histórico é consultado novamente.
     */
    override fun onResume() {
        super.onResume()

        carregarHistorico()
    }

    private fun iniciarComponentes() {

        containerHistorico =
            findViewById(R.id.containerHistorico)

        layoutEstadoVazio =
            findViewById(R.id.layoutEstadoVazio)

        txtEstadoVazioTitulo =
            findViewById(R.id.txtEstadoVazioTitulo)

        txtEstadoVazioDescricao =
            findViewById(R.id.txtEstadoVazioDescricao)

        btnNovaAnaliseHistorico =
            findViewById(R.id.btnNovaAnaliseHistorico)

        btnVoltarHistorico =
            findViewById(R.id.btnVoltarHistorico)
    }

    private fun configurarCliques() {

        btnNovaAnaliseHistorico.setOnClickListener {

            val intent =
                Intent(
                    this,
                    AnaliseActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)

            finish()
        }

        btnVoltarHistorico.setOnClickListener {

            val intent =
                Intent(
                    this,
                    AnaliseActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)

            finish()
        }
    }

    private fun carregarHistorico() {

        containerHistorico.removeAllViews()

        val usuario =
            FirebaseAuth
                .getInstance()
                .currentUser

        if (usuario == null) {

            mostrarEstadoVazio(
                titulo = "Histórico indisponível",
                descricao = "Não foi possível identificar o usuário conectado."
            )

            return
        }

        val referencia =
            FirebaseDatabase
                .getInstance()
                .reference
                .child("usuarios")
                .child(usuario.uid)
                .child("historicoAnalises")

        referencia
            .get()
            .addOnSuccessListener { snapshot ->

                containerHistorico.removeAllViews()

                if (!snapshot.exists() || snapshot.childrenCount == 0L) {

                    mostrarEstadoVazio(
                        titulo = "Nenhuma análise realizada ainda",
                        descricao = "Quando você concluir sua primeira triagem, ela aparecerá aqui."
                    )

                    return@addOnSuccessListener
                }

                val analises =
                    snapshot.children.map { item ->

                        /*
                         * Firebase costuma devolver números
                         * como Long ou Double.
                         *
                         * Por isso usamos ".value as? Number"
                         * em vez de getValue(Number::class.java).
                         */
                        val timestamp =
                            (item.child("timestamp").value as? Number)
                                ?.toLong()
                                ?: 0L

                        val score =
                            (item.child("scoreSuspeitaIA").value as? Number)
                                ?.toFloat()
                                ?: 0f

                        AnaliseHistorico(

                            id =
                                item.key ?: "",

                            timestamp =
                                timestamp,

                            area =
                                item.child("areaSelecionada")
                                    .getValue(String::class.java)
                                    ?: "Não informada",

                            nivelAtencao =
                                item.child("nivelAtencao")
                                    .getValue(String::class.java)
                                    ?: "Não informado",

                            percentual =
                                item.child("riscoPercentual")
                                    .getValue(String::class.java)
                                    ?: "--",

                            descricao =
                                item.child("riscoDescricao")
                                    .getValue(String::class.java)
                                    ?: "",

                            imagePath =
                                item.child("imagePath")
                                    .getValue(String::class.java)
                                    ?: "",

                            tempoLesao =
                                item.child("tempoLesao")
                                    .getValue(String::class.java)
                                    ?: "Não informado",

                            mudancaTamanho =
                                item.child("mudancaTamanho")
                                    .getValue(String::class.java)
                                    ?: "Não informado",

                            mudancaCor =
                                item.child("mudancaCor")
                                    .getValue(String::class.java)
                                    ?: "Não informado",

                            coceira =
                                item.child("coceira")
                                    .getValue(String::class.java)
                                    ?: "Não informado",

                            sangramento =
                                item.child("sangramento")
                                    .getValue(String::class.java)
                                    ?: "Não informado",

                            dor =
                                item.child("dor")
                                    .getValue(String::class.java)
                                    ?: "Não informado",

                            formatoIrregular =
                                item.child("formatoIrregular")
                                    .getValue(String::class.java)
                                    ?: "Não informado",

                            classificacaoIA =
                                item.child("classificacaoIA")
                                    .getValue(String::class.java)
                                    ?: "Não informado",

                            scoreSuspeitaIA =
                                score
                        )
                    }
                        .sortedByDescending {
                            it.timestamp
                        }

                if (analises.isEmpty()) {

                    mostrarEstadoVazio(
                        titulo = "Nenhuma análise realizada ainda",
                        descricao = "Quando você concluir sua primeira triagem, ela aparecerá aqui."
                    )

                    return@addOnSuccessListener
                }

                layoutEstadoVazio.visibility =
                    View.GONE

                containerHistorico.visibility =
                    View.VISIBLE

                analises.forEach { analise ->

                    adicionarCardHistorico(
                        analise
                    )
                }
            }
            .addOnFailureListener {

                mostrarEstadoVazio(
                    titulo = "Não foi possível carregar o histórico",
                    descricao = "Verifique sua conexão e tente novamente."
                )
            }
    }

    private fun mostrarEstadoVazio(
        titulo: String,
        descricao: String
    ) {

        containerHistorico.removeAllViews()

        containerHistorico.visibility =
            View.GONE

        layoutEstadoVazio.visibility =
            View.VISIBLE

        txtEstadoVazioTitulo.text =
            titulo

        txtEstadoVazioDescricao.text =
            descricao
    }

    private fun adicionarCardHistorico(
        analise: AnaliseHistorico
    ) {

        val card =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    dp(20),
                    dp(20),
                    dp(20),
                    dp(20)
                )

                setBackgroundResource(
                    R.drawable.bg_card_login
                )

                elevation =
                    dp(7).toFloat()
            }

        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        cardParams.setMargins(
            0,
            0,
            0,
            dp(16)
        )

        card.layoutParams =
            cardParams

        /*
         * DATA
         */
        val txtData =
            TextView(this).apply {

                text =
                    formatarData(
                        analise.timestamp
                    )

                textSize =
                    13f

                setTextColor(
                    Color.parseColor("#60738A")
                )
            }

        /*
         * NÍVEL DE ATENÇÃO
         */
        val txtNivel =
            TextView(this).apply {

                text =
                    analise.nivelAtencao

                textSize =
                    21f

                setTypeface(
                    null,
                    Typeface.BOLD
                )

                setTextColor(
                    corNivelAtencao(
                        analise.nivelAtencao
                    )
                )

                setPadding(
                    0,
                    dp(6),
                    0,
                    0
                )
            }

        /*
         * SCORE VISUAL
         */
        val txtPercentual =
            TextView(this).apply {

                text =
                    "Suspeita visual de lesões cutâneas: ${analise.percentual}"

                textSize =
                    14f

                setTextColor(
                    Color.parseColor("#173B70")
                )

                setPadding(
                    0,
                    dp(7),
                    0,
                    0
                )
            }

        /*
         * ÁREA
         */
        val txtArea =
            TextView(this).apply {

                text =
                    "Área analisada: ${analise.area}"

                textSize =
                    14f

                setTextColor(
                    Color.parseColor("#50627A")
                )

                setPadding(
                    0,
                    dp(5),
                    0,
                    0
                )
            }

        /*
         * BOTÃO VER DETALHES
         */
        val btnDetalhes =
            Button(this).apply {

                text =
                    "Ver detalhes"

                textSize =
                    15f

                isAllCaps =
                    false

                setTextColor(
                    Color.parseColor("#0A4BCF")
                )

                setBackgroundResource(
                    R.drawable.bg_button_outline_dermaprev
                )
            }

        val btnParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
            )

        btnParams.setMargins(
            0,
            dp(16),
            0,
            0
        )

        btnDetalhes.layoutParams =
            btnParams

        btnDetalhes.setOnClickListener {

            abrirDetalhes(
                analise
            )
        }

        card.addView(txtData)
        card.addView(txtNivel)
        card.addView(txtPercentual)
        card.addView(txtArea)
        card.addView(btnDetalhes)

        containerHistorico.addView(
            card
        )
    }

    private fun abrirDetalhes(
        analise: AnaliseHistorico
    ) {

        val intent =
            Intent(
                this,
                DetalhesAnaliseActivity::class.java
            )

        intent.putExtra(
            "areaSelecionada",
            analise.area
        )

        intent.putExtra(
            "imagePath",
            analise.imagePath
        )

        intent.putExtra(
            "riscoTitulo",
            analise.nivelAtencao
        )

        intent.putExtra(
            "nivelAtencao",
            analise.nivelAtencao
        )

        intent.putExtra(
            "riscoPercentual",
            analise.percentual
        )

        intent.putExtra(
            "riscoDescricao",
            analise.descricao
        )

        intent.putExtra(
            "tempoLesao",
            analise.tempoLesao
        )

        intent.putExtra(
            "mudancaTamanho",
            analise.mudancaTamanho
        )

        intent.putExtra(
            "mudancaCor",
            analise.mudancaCor
        )

        intent.putExtra(
            "coceira",
            analise.coceira
        )

        intent.putExtra(
            "sangramento",
            analise.sangramento
        )

        intent.putExtra(
            "dor",
            analise.dor
        )

        intent.putExtra(
            "formatoIrregular",
            analise.formatoIrregular
        )

        intent.putExtra(
            "classificacaoIA",
            analise.classificacaoIA
        )

        intent.putExtra(
            "scoreSuspeitaIA",
            analise.scoreSuspeitaIA
        )

        startActivity(intent)
    }

    private fun formatarData(
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

    private fun corNivelAtencao(
        nivel: String
    ): Int {

        return when {

            nivel.contains(
                "elevado",
                ignoreCase = true
            ) -> {

                Color.parseColor(
                    "#D9534F"
                )
            }

            nivel.contains(
                "moderado",
                ignoreCase = true
            ) -> {

                Color.parseColor(
                    "#E6A23C"
                )
            }

            nivel.contains(
                "baixo",
                ignoreCase = true
            ) -> {

                Color.parseColor(
                    "#28A745"
                )
            }

            else -> {

                Color.parseColor(
                    "#063B78"
                )
            }
        }
    }

    private fun dp(
        valor: Int
    ): Int {

        return (
                valor *
                        resources.displayMetrics.density
                ).toInt()
    }

    data class AnaliseHistorico(

        val id: String,

        val timestamp: Long,

        val area: String,

        val nivelAtencao: String,

        val percentual: String,

        val descricao: String,

        val imagePath: String,

        val tempoLesao: String,

        val mudancaTamanho: String,

        val mudancaCor: String,

        val coceira: String,

        val sangramento: String,

        val dor: String,

        val formatoIrregular: String,

        val classificacaoIA: String,

        val scoreSuspeitaIA: Float
    )
}