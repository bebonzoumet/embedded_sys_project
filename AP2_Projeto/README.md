# Hand Landmarker App

Este projeto é um aplicativo Android que utiliza a biblioteca MediaPipe para detecção e rastreamento das articulações das mãos em tempo real. O aplicativo é desenvolvido em Kotlin e utiliza o TensorFlow Lite para inferência rápida e eficiente em dispositivos móveis.

## Descrição do Hardware Utilizado

Para executar este aplicativo, é necessário um dispositivo com as seguintes especificações mínimas:

- **Processador**: Quad-core 1.4 GHz ou superior
- **Memória RAM**: 2 GB ou superior
- **Sistema Operacional**: Android 7.0 (Nougat) ou superior
- **Câmera**: Necessária para a captura de imagens para o processamento de marcadores de mão

## Descrição do Software Utilizado

O aplicativo foi desenvolvido com as seguintes tecnologias e bibliotecas:

- **Linguagem de Programação**: Kotlin
- **Ambiente de Desenvolvimento Integrado (IDE)**: Android Studio
- **Biblioteca de Processamento de Imagens**: MediaPipe da Google
- **Framework de Inteligência Computacional**: TensorFlow Lite

## Modelos de Inteligência Computacional

O aplicativo utiliza o modelo de detecção de pontos de articulação das mãos da MediaPipe. Este modelo é projetado para detectar e rastrear as posições das articulações da mão em tempo real. Os modelos são otimizados para dispositivos móveis utilizando o TensorFlow Lite, garantindo que a inferência seja rápida e eficiente.

## Repositório
O código-fonte completo do projeto pode ser encontrado no repositório do GitHub:

[Repositório no GitHub](https://github.com/google/mediapipe)

## Documentação
[Documentação do MediaPipe](https://ai.google.dev/edge/mediapipe/solutions/guide?hl=pt-br)

## Código
```kotlin
/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.mediapipe.examples.handlandmarker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var results: HandLandmarkerResult? = null
    private var linePaint = Paint()
    private var pointPaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    private var arraylistX = ArrayList<Float>()
    private var arraylistY = ArrayList<Float>()

    init {
        initPaints()
    }

    fun clear() {
        results = null
        linePaint.reset()
        pointPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        linePaint.color =
            ContextCompat.getColor(context!!, R.color.mp_color_primary)
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        results?.let { handLandmarkerResult ->
            for (landmark in handLandmarkerResult.landmarks()) {
                // Encontrar o índice do ponto do dedo indicador
                val indexFingerPoint = landmark.get(8) // 8 é o índice do dedo indicador

                // Acessar as coordenadas x e y do ponto do dedo indicador
                val indexFingerX = indexFingerPoint.x() * imageWidth * scaleFactor
                val indexFingerY = indexFingerPoint.y() * imageHeight * scaleFactor

                if (arraylistX.size <= 30){

                }
                else{
                    arraylistX = arraylistX.drop(0) as ArrayList<Float>
                    arraylistY = arraylistY.drop(0) as ArrayList<Float>
                }

                arraylistX.add(indexFingerX)
                arraylistY.add(indexFingerY)
                println(indexFingerX);

                // Renderizar o ponto do dedo indicador
                for (pontoX in arraylistX){
                    canvas.drawColor(Color.TRANSPARENT)
                    canvas.drawPoint(pontoX, arraylistY[arraylistX.indexOf(pontoX)], pointPaint)
                }

                // Renderizar as conexões dos dedos
                HandLandmarker.HAND_CONNECTIONS.forEach { connection ->
                    val startPoint = landmark.get(connection!!.start())
                    val endPoint = landmark.get(connection.end())
                    canvas.drawLine(
                        startPoint.x() * imageWidth * scaleFactor,
                        startPoint.y() * imageHeight * scaleFactor,
                        endPoint.x() * imageWidth * scaleFactor,
                        endPoint.y() * imageHeight * scaleFactor,
                        linePaint
                    )
                }
            }
        }
    }

    fun setResults(
        handLandmarkerResults: HandLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = handLandmarkerResults

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE,
            RunningMode.VIDEO -> {
                min(width * 1f / imageWidth, height * 1f / imageHeight)
            }
            RunningMode.LIVE_STREAM -> {
                // PreviewView is in FILL_START mode. So we need to scale up the
                // landmarks to match with the size that the captured images will be
                // displayed.
                max(width * 1f / imageWidth, height * 1f / imageHeight)
            }
        }
        invalidate()
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 8F
    }
}
```
