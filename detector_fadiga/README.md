# Face Landmarker App

## Descrição do Software
O aplicativo `Face Landmarker` é uma aplicação Android que utiliza técnicas avançadas de visão computacional para detectar e rastrear pontos de referência no rosto de uma pessoa. Este aplicativo é ideal para diversos casos de uso, como monitoramento de fadiga, reconhecimento facial, animação facial e aplicações de realidade aumentada.

## Tecnologias Utilizadas
- **MediaPipe**: Framework de aprendizado de máquina que facilita a construção de pipelines de processamento de mídia.
- **TensorFlow**: Utilizado para suporte e integração com modelos de aprendizado de máquina.
- **Kotlin**: Linguagem de programação usada para desenvolvimento do aplicativo Android.

## Modelos de Inteligência Computacional
O aplicativo utiliza o modelo de marcação de face da MediaPipe (`Face Landmarker`), que é treinado para detectar e rastrear pontos de referência faciais em imagens e vídeos. Este modelo é capaz de identificar diversos pontos chave no rosto, como olhos, nariz e boca, e conectá-los para formar uma estrutura de marcação facial.

## Implementações do Grupo em Destaque
1. **Detecção de Cansaço**:
   - O aplicativo monitora a posição vertical de pontos específicos ao redor dos olhos para determinar se estão fechados por um período prolongado.
   - Quando os olhos permanecem fechados por mais de um segundo, o aplicativo toca um som de alerta para notificar o usuário.

2. **Visualização de Pontos de Referência**:
   - Os pontos de referência detectados são desenhados na tela usando a classe `OverlayView`.
   - Linhas conectando os pontos também são desenhadas para uma melhor visualização da estrutura facial.

3. **Integração de Áudio**:
   - Um `MediaPlayer` é utilizado para tocar um som de alerta quando o usuário é detectado como cansado.

## Recuperação das Coordenadas dos Pontos de Referência da Face
Para a detecção de fadiga, o aplicativo recupera as coordenadas dos pontos de referência ao redor dos olhos, especificamente os índices 145, 159 (olho direito) e 374, 386 (olho esquerdo). O procedimento para recuperar essas coordenadas é o seguinte:

1. **Inicialização dos Pontos de Interesse**:
   ```kotlin
   private var ydp1: Float = 0f
   private var ydp2: Float = 0f
   private var yep1: Float = 0f
   private var yep2: Float = 0f
   ```
2. **Extração das Coordenadas**:
  ```kotlin 
    results?.let { faceLandmarkerResult ->
    for ((landmarkIndex, landmark) in faceLandmarkerResult.faceLandmarks().withIndex()) {
        for ((pointIndex, normalizedLandmark) in landmark.withIndex()) {
            val y = normalizedLandmark.y()
            if (pointIndex == 145) {
                ydp1 = y
            } else if (pointIndex == 159) {
                ydp2 = y
            } else if (pointIndex == 374) {
                yep1 = y
            } else if (pointIndex == 386) {
                yep2 = y
            }
        }
    }
}
```
3. **Detecção de Olhos Fechados**:
  ```kotlin
val diff1 = ydp1 - ydp2
val diff2 = yep1 - yep2
if (diff1 < 0.009f && diff2 < 0.009f) {
    tempoOlhosFechados += 16
    if (tempoOlhosFechados >= intervaloCansado) {
        playSound()
        tempoOlhosFechados = 0
    }
} else {
    tempoOlhosFechados = 0
}
 ```
## Repositório

O código-fonte completo do projeto pode ser encontrado no repositório do GitHub:

[Repositório no GitHub](https://github.com/google/mediapipe)

## Documentação
[Documentação do MediaPipe](https://ai.google.dev/edge/mediapipe/solutions/guide?hl=pt-br)

## Código
```kotlin
package com.google.mediapipe.examples.facelandmarker

/*
 * Copyright 2023 The TensorFlow Authors. All Rights Reserved.
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
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import kotlin.math.max
import kotlin.math.min
import android.media.AudioManager;
import android.media.MediaPlayer;
import kotlin.reflect.typeOf

class OverlayView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {

    private var results: FaceLandmarkerResult? = null
    private var linePaint = Paint()
    private var pointPaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    private var ydp1: Float = 0f
    private var ydp2: Float = 0f
    private var yep1: Float = 0f
    private var yep2: Float = 0f

    private var tempoOlhosFechados: Long = 0
    private val intervaloCansado = 800000 //1 segundo = 100000

    private var mediaPlayer: MediaPlayer? = null

    init {
        initPaints()
        initMediaPlayer(context)
    }

    private fun initMediaPlayer(context: Context?) {
        // Inicializa o MediaPlayer com o arquivo de áudio
        mediaPlayer = MediaPlayer.create(context, R.raw.windowsxp)
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
        if(results == null || results!!.faceLandmarks().isEmpty()) {
            clear()
            return
        }

        results?.let { faceLandmarkerResult ->

            for(landmark in faceLandmarkerResult.faceLandmarks()) {
                for(normalizedLandmark in landmark) {
                    canvas.drawPoint(normalizedLandmark.x() * imageWidth * scaleFactor, normalizedLandmark.y() * imageHeight * scaleFactor, pointPaint)
                }
            }

            FaceLandmarker.FACE_LANDMARKS_CONNECTORS.forEach {
                canvas.drawLine(
                    faceLandmarkerResult.faceLandmarks().get(0).get(it!!.start()).x() * imageWidth * scaleFactor,
                    faceLandmarkerResult.faceLandmarks().get(0).get(it.start()).y() * imageHeight * scaleFactor,
                    faceLandmarkerResult.faceLandmarks().get(0).get(it.end()).x() * imageWidth * scaleFactor,
                    faceLandmarkerResult.faceLandmarks().get(0).get(it.end()).y() * imageHeight * scaleFactor,
                    linePaint)
            }
        }
    }

    private fun playSound() {
        mediaPlayer?.start()
    }

    fun setResults(
        faceLandmarkerResults: FaceLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = faceLandmarkerResults
        // Printing the landmarks with their indices
        results?.let { faceLandmarkerResult ->
            for ((landmarkIndex, landmark) in faceLandmarkerResult.faceLandmarks().withIndex()) {
                for ((pointIndex, normalizedLandmark) in landmark.withIndex()) {
                    val x = normalizedLandmark.x()
                    val y = normalizedLandmark.y()
                    val z = normalizedLandmark.z()
                    val indexDesejadoDireito1 = 145
                    val indexDesejadoDireito2 = 159
                    val indexDesejadoEsquerdo1 = 374
                    val indexDesejadoEsquerdo2 = 386

                    if(pointIndex == indexDesejadoDireito1 || pointIndex == indexDesejadoDireito2 || pointIndex == indexDesejadoEsquerdo1 || pointIndex == indexDesejadoEsquerdo2) {

                        //println("Point $pointIndex - x: $x, y: $y, z: $z")
                        if(pointIndex == indexDesejadoDireito1) {
                             ydp1 = y;
                        } else if(pointIndex == indexDesejadoDireito2) {
                             ydp2 = y;
                        } else if(pointIndex == indexDesejadoEsquerdo1) {
                             yep1 = y;
                        } else if(pointIndex == indexDesejadoEsquerdo2) {
                            yep2 = y;
                        }

                        val diff1 = ydp1 - ydp2;
                        val diff2 = yep1 - yep2;
                        if (diff1 < 0.009f && diff2 < 0.009f ) {
                            tempoOlhosFechados += 16
                            //println("Olho fechado!")
                            if (tempoOlhosFechados >= intervaloCansado) {
                                println("Você está cansado")
                                playSound()
                                // Reseta o tempo
                                tempoOlhosFechados = 0
                            }

                        } else {
                            tempoOlhosFechados = 0
                            //println("Olho aberto!")
                        }
                    }
                }
            }
        }


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
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Liberar recursos do MediaPlayer quando a View é removida da janela
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 8F
        private const val TAG = "Face Landmarker Overlay"
    }
}
```


