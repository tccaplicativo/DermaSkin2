package com.perabru.dermaskin2

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RelatoriosActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var btnVoltarRelatorios: ImageButton
    private lateinit var containerRelatorios: LinearLayout
    private lateinit var txtEstadoRelatorios: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relatorios)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            finish()
            return
        }

        iniciarComponentes()
        configurarCliques()
        carregarRelatorios()
    }

    private fun iniciarComponentes() {

        btnVoltarRelatorios =
            findViewById(R.id.btnVoltarRelatorios)

        containerRelatorios =
            findViewById(R.id.containerRelatorios)

        txtEstadoRelatorios =
            findViewById(R.id.txtEstadoRelatorios)
    }

    private fun configurarCliques() {

        btnVoltarRelatorios.setOnClickListener {
            finish()
        }
    }

    private fun carregarRelatorios() {

        val uid = auth.currentUser?.uid ?: return

        txtEstadoRelatorios.visibility = View.VISIBLE
        txtEstadoRelatorios.text = "Carregando relatórios..."

        FirebaseDatabase.getInstance()
            .reference
            .child("usuarios")
            .child(uid)
            .child("historicoAnalises")
            .addListenerForSingleValueEvent(
                object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        containerRelatorios.removeAllViews()

                        val analises =
                            snapshot.children.toList()
                                .sortedByDescending {
                                    it.child("timestamp")
                                        .getValue(Long::class.java)
                                        ?: 0L
                                }

                        if (analises.isEmpty()) {

                            txtEstadoRelatorios.visibility =
                                View.VISIBLE

                            txtEstadoRelatorios.text =
                                "Nenhum relatório disponível.\nRealize uma análise para gerar seu primeiro relatório."

                            return
                        }

                        txtEstadoRelatorios.visibility =
                            View.GONE

                        analises.forEach { analise ->

                            adicionarCardRelatorio(
                                analise
                            )
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                        txtEstadoRelatorios.visibility =
                            View.VISIBLE

                        txtEstadoRelatorios.text =
                            "Não foi possível carregar os relatórios."

                        Toast.makeText(
                            this@RelatoriosActivity,
                            "Erro: ${error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }

    private fun adicionarCardRelatorio(
        analise: DataSnapshot
    ) {

        val areaSelecionada =
            analise.child("areaSelecionada")
                .value?.toString()
                ?: "Área não informada"

        val nivelAtencao =
            analise.child("nivelAtencao")
                .value?.toString()
                ?: "Não informado"

        val timestamp =
            analise.child("timestamp")
                .getValue(Long::class.java)
                ?: 0L

        val card =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    dp(18),
                    dp(17),
                    dp(18),
                    dp(17)
                )

                background =
                    criarFundoCard()

                elevation =
                    dp(4).toFloat()

                isClickable =
                    true

                isFocusable =
                    true
            }

        val paramsCard =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        paramsCard.topMargin =
            dp(14)

        card.layoutParams =
            paramsCard

        val txtArea =
            TextView(this).apply {

                text =
                    areaSelecionada

                textSize =
                    17f

                setTextColor(
                    Color.parseColor("#063B78")
                )

                setTypeface(
                    typeface,
                    Typeface.BOLD
                )
            }

        val txtData =
            TextView(this).apply {

                text =
                    formatarData(timestamp)

                textSize =
                    12f

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

        val linhaNivel =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                setPadding(
                    0,
                    dp(15),
                    0,
                    0
                )
            }

        val txtNivelLabel =
            TextView(this).apply {

                text =
                    "Nível de atenção"

                textSize =
                    13f

                setTextColor(
                    Color.parseColor("#50627A")
                )
            }

        val txtNivel =
            TextView(this).apply {

                text =
                    nivelAtencao

                textSize =
                    13f

                gravity =
                    Gravity.END

                setTypeface(
                    typeface,
                    Typeface.BOLD
                )

                setTextColor(
                    corNivelAtencao(
                        nivelAtencao
                    )
                )
            }

        linhaNivel.addView(
            txtNivelLabel,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        linhaNivel.addView(
            txtNivel,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val divisor =
            View(this).apply {

                setBackgroundColor(
                    Color.parseColor("#E4EDF7")
                )
            }

        val paramsDivisor =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(1)
            )

        paramsDivisor.topMargin =
            dp(15)

        paramsDivisor.bottomMargin =
            dp(12)

        val linhaAbrir =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL

                setPadding(
                    dp(14),
                    dp(11),
                    dp(14),
                    dp(11)
                )

                background =
                    GradientDrawable().apply {

                        shape =
                            GradientDrawable.RECTANGLE

                        setColor(
                            Color.parseColor("#EAF3FF")
                        )

                        cornerRadius =
                            dp(12).toFloat()
                    }
            }

        val txtAbrir =
            TextView(this).apply {

                text =
                    "Visualizar relatório"

                textSize =
                    14f

                setTextColor(
                    Color.parseColor("#0A4BCF")
                )

                setTypeface(
                    typeface,
                    Typeface.BOLD
                )
            }

        val txtSeta =
            TextView(this).apply {

                text =
                    "›"

                textSize =
                    25f

                setTextColor(
                    Color.parseColor("#0A4BCF")
                )
            }

        linhaAbrir.addView(
            txtAbrir,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        linhaAbrir.addView(
            txtSeta
        )

        card.addView(
            txtArea
        )

        card.addView(
            txtData
        )

        card.addView(
            linhaNivel
        )

        card.addView(
            divisor,
            paramsDivisor
        )

        card.addView(
            linhaAbrir
        )

        card.setOnClickListener {

            abrirRelatorio(
                analise
            )
        }

        containerRelatorios.addView(
            card
        )
    }

    private fun abrirRelatorio(
        analise: DataSnapshot
    ) {

        val intent =
            Intent(
                this,
                RelatorioProfissionalActivity::class.java
            )

        intent.putExtra(
            "areaSelecionada",
            analise.child("areaSelecionada")
                .value?.toString()
                ?: "Não informada"
        )

        intent.putExtra(
            "imagePath",
            analise.child("imagePath")
                .value?.toString()
                ?: ""
        )

        intent.putExtra(
            "tempoLesao",
            analise.child("tempoLesao")
                .value?.toString()
                ?: "Não informado"
        )

        intent.putExtra(
            "mudancaTamanho",
            analise.child("mudancaTamanho")
                .value?.toString()
                ?: "Não informado"
        )

        intent.putExtra(
            "mudancaCor",
            analise.child("mudancaCor")
                .value?.toString()
                ?: "Não informado"
        )

        intent.putExtra(
            "coceira",
            analise.child("coceira")
                .value?.toString()
                ?: "Não informado"
        )

        intent.putExtra(
            "sangramento",
            analise.child("sangramento")
                .value?.toString()
                ?: "Não informado"
        )

        intent.putExtra(
            "dor",
            analise.child("dor")
                .value?.toString()
                ?: "Não informado"
        )

        intent.putExtra(
            "formatoIrregular",
            analise.child("formatoIrregular")
                .value?.toString()
                ?: "Não informado"
        )

        val nivelAtencao =
            analise.child("nivelAtencao")
                .value?.toString()
                ?: "Resultado não calculado"

        intent.putExtra(
            "riscoTitulo",
            nivelAtencao
        )

        intent.putExtra(
            "nivelAtencao",
            nivelAtencao
        )

        intent.putExtra(
            "riscoPercentual",
            analise.child("riscoPercentual")
                .value?.toString()
                ?: "--"
        )

        intent.putExtra(
            "riscoDescricao",
            analise.child("riscoDescricao")
                .value?.toString()
                ?: "Sem descrição disponível."
        )

        intent.putExtra(
            "classificacaoIA",
            analise.child("classificacaoIA")
                .value?.toString()
                ?: "Não informado"
        )

        val score =
            (
                    analise.child("scoreSuspeitaIA")
                        .value as? Number
                    )?.toFloat()
                ?: 0f

        intent.putExtra(
            "scoreSuspeitaIA",
            score
        )

        intent.putExtra(
            "confiancaIA",
            0f
        )

        intent.putExtra(
            "confiancaClassificacaoPercentual",
            0
        )

        // Informa ao relatório que ele foi aberto pela lista.
        intent.putExtra(
            "origemRelatorios",
            true
        )

        startActivity(
            intent
        )
    }

    private fun criarFundoCard(): GradientDrawable {

        return GradientDrawable().apply {

            shape = GradientDrawable.RECTANGLE

            setColor(
                Color.WHITE
            )

            cornerRadius =
                dp(18).toFloat()

            setStroke(
                dp(1),
                Color.parseColor("#D9EAF5")
            )
        }
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
            ) ->
                Color.parseColor("#D9534F")

            nivel.contains(
                "moderado",
                ignoreCase = true
            ) ->
                Color.parseColor("#E6A23C")

            nivel.contains(
                "baixo",
                ignoreCase = true
            ) ->
                Color.parseColor("#28A745")

            else ->
                Color.parseColor("#063B78")
        }
    }

    private fun dp(valor: Int): Int {

        return (
                valor *
                        resources.displayMetrics.density
                ).toInt()
    }
}