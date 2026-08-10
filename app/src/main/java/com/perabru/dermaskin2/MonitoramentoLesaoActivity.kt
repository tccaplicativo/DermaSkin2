package com.perabru.dermaskin2

import android.app.AlertDialog
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
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MonitoramentoLesaoActivity : AppCompatActivity() {

    private lateinit var btnVoltarMonitoramento: ImageButton
    private lateinit var btnNovoAcompanhamento: LinearLayout

    private lateinit var containerMonitoramentos: LinearLayout
    private lateinit var layoutSemMonitoramentos: LinearLayout
    private lateinit var txtCarregandoMonitoramentos: TextView

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val database by lazy {
        FirebaseDatabase.getInstance().reference
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_monitoramento_lesao
        )

        iniciarComponentes()
        configurarCliques()
        carregarMonitoramentos()
    }

    private fun iniciarComponentes() {

        btnVoltarMonitoramento =
            findViewById(
                R.id.btnVoltarMonitoramento
            )

        btnNovoAcompanhamento =
            findViewById(
                R.id.btnNovoAcompanhamento
            )

        containerMonitoramentos =
            findViewById(
                R.id.containerMonitoramentos
            )

        layoutSemMonitoramentos =
            findViewById(
                R.id.layoutSemMonitoramentos
            )

        txtCarregandoMonitoramentos =
            findViewById(
                R.id.txtCarregandoMonitoramentos
            )
    }

    private fun configurarCliques() {

        btnVoltarMonitoramento.setOnClickListener {

            finish()
        }

        btnNovoAcompanhamento.setOnClickListener {

            abrirSelecaoDeAnalise()
        }
    }

    /*
     * CARREGA TODOS OS MONITORAMENTOS
     */

    private fun carregarMonitoramentos() {

        val uid =
            auth.currentUser?.uid

        if (uid == null) {

            txtCarregandoMonitoramentos.visibility =
                View.GONE

            layoutSemMonitoramentos.visibility =
                View.VISIBLE

            return
        }

        val referencia =
            database
                .child("usuarios")
                .child(uid)
                .child("monitoramentos")

        referencia.addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    txtCarregandoMonitoramentos.visibility =
                        View.GONE

                    containerMonitoramentos.removeAllViews()

                    if (!snapshot.exists()) {

                        layoutSemMonitoramentos.visibility =
                            View.VISIBLE

                        return
                    }

                    layoutSemMonitoramentos.visibility =
                        View.GONE

                    val lista =
                        snapshot.children
                            .toList()
                            .sortedByDescending {

                                it.child("ultimoRegistroTimestamp")
                                    .getValue(Long::class.java)
                                    ?: 0L
                            }

                    lista.forEach {

                        adicionarCardMonitoramento(
                            it
                        )
                    }
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {

                    txtCarregandoMonitoramentos.visibility =
                        View.GONE

                    Toast.makeText(
                        this@MonitoramentoLesaoActivity,
                        "Não foi possível carregar os acompanhamentos.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    /*
     * CRIA O CARD DE UMA LESÃO
     */

    private fun adicionarCardMonitoramento(
        snapshot: DataSnapshot
    ) {

        val view =
            LayoutInflater
                .from(this)
                .inflate(
                    R.layout.item_monitoramento_lesao,
                    containerMonitoramentos,
                    false
                )

        val imgLesao =
            view.findViewById<ImageView>(
                R.id.imgMonitoramentoLesao
            )

        val txtNome =
            view.findViewById<TextView>(
                R.id.txtNomeLesao
            )

        val txtDataInicio =
            view.findViewById<TextView>(
                R.id.txtDataInicioMonitoramento
            )

        val txtQuantidade =
            view.findViewById<TextView>(
                R.id.txtQuantidadeRegistros
            )

        val txtUltimoRegistro =
            view.findViewById<TextView>(
                R.id.txtUltimoRegistro
            )

        val btnVerEvolucao =
            view.findViewById<LinearLayout>(
                R.id.btnVerEvolucao
            )

        val monitoramentoId =
            snapshot.child("id")
                .getValue(String::class.java)
                ?: snapshot.key
                ?: ""

        val nome =
            snapshot.child("nome")
                .getValue(String::class.java)
                ?: "Lesão acompanhada"

        val imagePathInicial =
            snapshot.child("imagePathInicial")
                .getValue(String::class.java)
                ?: ""

        val dataInicio =
            snapshot.child("dataInicio")
                .getValue(Long::class.java)
                ?: 0L

        val ultimoRegistro =
            snapshot.child("ultimoRegistroTimestamp")
                .getValue(Long::class.java)
                ?: dataInicio

        val quantidadeRegistros =
            snapshot.child("quantidadeRegistros")
                .getValue(Int::class.java)
                ?: snapshot.child("registros")
                    .childrenCount
                    .toInt()

        txtNome.text =
            nome

        txtDataInicio.text =
            "Iniciado em ${formatarData(dataInicio)}"

        txtQuantidade.text =
            if (quantidadeRegistros == 1) {

                "1 registro"

            } else {

                "$quantidadeRegistros registros"
            }

        txtUltimoRegistro.text =
            "Último registro: ${formatarData(ultimoRegistro)}"

        carregarImagem(
            imagePathInicial,
            imgLesao
        )

        btnVerEvolucao.setOnClickListener {

            val intent = Intent(
                this,
                EvolucaoLesaoActivity::class.java
            )

            intent.putExtra(
                "monitoramentoId",
                monitoramentoId
            )

            startActivity(intent)
        }

        containerMonitoramentos.addView(
            view
        )
    }

    /*
     * ESCOLHER UMA ANÁLISE DO HISTÓRICO
     */

    private fun abrirSelecaoDeAnalise() {

        val uid =
            auth.currentUser?.uid

        if (uid == null) {

            Toast.makeText(
                this,
                "Usuário não identificado.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val referenciaHistorico =
            database
                .child("usuarios")
                .child(uid)
                .child("historicoAnalises")

        referenciaHistorico.addListenerForSingleValueEvent(
            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    if (!snapshot.exists()) {

                        Toast.makeText(
                            this@MonitoramentoLesaoActivity,
                            "Você ainda não possui análises no histórico.",
                            Toast.LENGTH_LONG
                        ).show()

                        return
                    }

                    val analises =
                        snapshot.children
                            .toList()
                            .sortedByDescending {

                                it.child("timestamp")
                                    .getValue(Long::class.java)
                                    ?: 0L
                            }

                    mostrarDialogAnalises(
                        analises
                    )
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {

                    Toast.makeText(
                        this@MonitoramentoLesaoActivity,
                        "Não foi possível carregar o histórico.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    /*
     * DIALOG PARA ESCOLHER QUAL MANCHA
     */

    private fun mostrarDialogAnalises(
        analises: List<DataSnapshot>
    ) {

        if (analises.isEmpty()) {

            Toast.makeText(
                this,
                "Você ainda não possui análises disponíveis para acompanhamento.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val itens =
            analises.map { analise ->

                val area =
                    analise.child("areaSelecionada")
                        .getValue(String::class.java)
                        ?: "Área não informada"

                val timestamp =
                    analise.child("timestamp")
                        .getValue(Long::class.java)
                        ?: 0L

                "$area • ${formatarData(timestamp)}"
            }
                .toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Qual lesão deseja acompanhar?")
            .setItems(itens) { _, position ->

                verificarECriarMonitoramento(
                    analises[position]
                )
            }
            .setNegativeButton(
                "Cancelar",
                null
            )
            .show()
    }

    /*
     * EVITA USAR A MESMA ANÁLISE
     * COMO INÍCIO DE DOIS MONITORAMENTOS
     */

    private fun verificarECriarMonitoramento(
        analise: DataSnapshot
    ) {

        val uid =
            auth.currentUser?.uid
                ?: return

        val analiseId =
            analise.child("id")
                .getValue(String::class.java)
                ?: analise.key
                ?: return

        val referencia =
            database
                .child("usuarios")
                .child(uid)
                .child("monitoramentos")

        referencia.addListenerForSingleValueEvent(
            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    val jaExiste =
                        snapshot.children.any {

                            it.child("analiseInicialId")
                                .getValue(String::class.java) ==
                                    analiseId
                        }

                    if (jaExiste) {

                        Toast.makeText(
                            this@MonitoramentoLesaoActivity,
                            "Essa lesão já está em acompanhamento.",
                            Toast.LENGTH_LONG
                        ).show()

                        return
                    }

                    criarMonitoramento(
                        analise,
                        snapshot
                    )
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {

                    Toast.makeText(
                        this@MonitoramentoLesaoActivity,
                        "Não foi possível iniciar o acompanhamento.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    /*
     * CRIA UM MONITORAMENTO ÚNICO
     */

    private fun criarMonitoramento(
        analise: DataSnapshot,
        monitoramentosExistentes: DataSnapshot
    ) {

        val uid =
            auth.currentUser?.uid
                ?: return

        val referencia =
            database
                .child("usuarios")
                .child(uid)
                .child("monitoramentos")

        val novoId =
            referencia
                .push()
                .key
                ?: return

        val analiseId =
            analise.child("id")
                .getValue(String::class.java)
                ?: analise.key
                ?: return

        val area =
            analise.child("areaSelecionada")
                .getValue(String::class.java)
                ?: "Área não informada"

        val imagePath =
            analise.child("imagePath")
                .getValue(String::class.java)
                ?: ""

        val timestamp =
            analise.child("timestamp")
                .getValue(Long::class.java)
                ?: System.currentTimeMillis()

        /*
         * Conta quantas lesões dessa mesma área
         * já estão sendo acompanhadas.
         */

        val quantidadeMesmaArea =
            monitoramentosExistentes
                .children
                .count {

                    it.child("areaSelecionada")
                        .getValue(String::class.java)
                        ?.equals(
                            area,
                            ignoreCase = true
                        ) == true
                }

        val numeroLesao =
            quantidadeMesmaArea + 1

        val nome =
            "$area • Lesão $numeroLesao"

        val dados =
            hashMapOf<String, Any>(
                "id" to novoId,
                "nome" to nome,
                "areaSelecionada" to area,
                "dataInicio" to timestamp,
                "status" to "ativo",
                "analiseInicialId" to analiseId,
                "imagePathInicial" to imagePath,
                "ultimoRegistroTimestamp" to timestamp,
                "quantidadeRegistros" to 1
            )

        referencia
            .child(novoId)
            .setValue(dados)
            .addOnSuccessListener {

                val registro =
                    hashMapOf<String, Any>(
                        "analiseId" to analiseId,
                        "timestamp" to timestamp
                    )

                referencia
                    .child(novoId)
                    .child("registros")
                    .child(analiseId)
                    .setValue(registro)

                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "$nome adicionado ao acompanhamento.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Não foi possível iniciar o acompanhamento.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    /*
     * FOTO
     */

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

    /*
     * DATA
     */

    private fun formatarData(
        timestamp: Long
    ): String {

        if (timestamp <= 0L) {

            return "--/--/----"
        }

        val formato =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale("pt", "BR")
            )

        return formato.format(
            Date(timestamp)
        )
    }
}