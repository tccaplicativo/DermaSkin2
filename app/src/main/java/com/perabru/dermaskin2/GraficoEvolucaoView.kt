package com.perabru.dermaskin2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

data class PontoGrafico(
    val data: String,
    val valor: Float
)

class GraficoEvolucaoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context,
    attrs,
    defStyleAttr
) {

    private val pontos =
        mutableListOf<PontoGrafico>()

    private val paintLinha =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            color =
                Color.parseColor(
                    "#2E8B67"
                )

            strokeWidth =
                5f

            style =
                Paint.Style.STROKE

            strokeCap =
                Paint.Cap.ROUND

            strokeJoin =
                Paint.Join.ROUND
        }

    private val paintPonto =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            color =
                Color.parseColor(
                    "#2E8B67"
                )

            style =
                Paint.Style.FILL
        }

    private val paintGrade =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            color =
                Color.parseColor(
                    "#DDE7E2"
                )

            strokeWidth =
                2f
        }

    private val paintTexto =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            color =
                Color.parseColor(
                    "#687990"
                )

            textSize =
                27f
        }

    private val paintValor =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            color =
                Color.parseColor(
                    "#174D3B"
                )

            textSize =
                25f

            textAlign =
                Paint.Align.CENTER
        }

    fun setDados(
        novosPontos: List<PontoGrafico>
    ) {

        pontos.clear()

        pontos.addAll(
            novosPontos
        )

        invalidate()
    }

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(
            canvas
        )

        if (pontos.isEmpty()) {

            paintTexto.textAlign =
                Paint.Align.CENTER

            canvas.drawText(
                "Ainda não há dados suficientes.",
                width / 2f,
                height / 2f,
                paintTexto
            )

            return
        }

        val margemEsquerda =
            70f

        val margemDireita =
            25f

        val margemSuperior =
            35f

        val margemInferior =
            55f

        val larguraGrafico =
            width -
                    margemEsquerda -
                    margemDireita

        val alturaGrafico =
            height -
                    margemSuperior -
                    margemInferior

        /*
         * Linhas horizontais:
         * 0%, 25%, 50%, 75%, 100%
         */

        for (i in 0..4) {

            val valor =
                i * 25

            val y =
                margemSuperior +
                        alturaGrafico -
                        (
                                valor / 100f *
                                        alturaGrafico
                                )

            canvas.drawLine(
                margemEsquerda,
                y,
                width - margemDireita,
                y,
                paintGrade
            )

            paintTexto.textAlign =
                Paint.Align.RIGHT

            canvas.drawText(
                "$valor%",
                margemEsquerda - 10f,
                y + 8f,
                paintTexto
            )
        }

        val divisor =
            max(
                pontos.size - 1,
                1
            )

        val espacamentoX =
            larguraGrafico /
                    divisor

        val path =
            Path()

        pontos.forEachIndexed { index, ponto ->

            val x =
                if (pontos.size == 1) {

                    margemEsquerda +
                            larguraGrafico / 2f

                } else {

                    margemEsquerda +
                            index *
                            espacamentoX
                }

            var percentual =
                ponto.valor

            if (percentual <= 1f) {

                percentual *=
                    100f
            }

            percentual =
                percentual.coerceIn(
                    0f,
                    100f
                )

            val y =
                margemSuperior +
                        alturaGrafico -
                        (
                                percentual /
                                        100f *
                                        alturaGrafico
                                )

            if (index == 0) {

                path.moveTo(
                    x,
                    y
                )

            } else {

                path.lineTo(
                    x,
                    y
                )
            }

            canvas.drawCircle(
                x,
                y,
                9f,
                paintPonto
            )

            canvas.drawText(
                "${percentual.toInt()}%",
                x,
                y - 16f,
                paintValor
            )

            paintTexto.textAlign =
                Paint.Align.CENTER

            canvas.drawText(
                ponto.data,
                x,
                height - 15f,
                paintTexto
            )
        }

        if (pontos.size > 1) {

            canvas.drawPath(
                path,
                paintLinha
            )
        }
    }
}