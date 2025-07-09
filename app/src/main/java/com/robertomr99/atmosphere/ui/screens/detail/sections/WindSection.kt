package com.robertomr99.atmosphere.ui.screens.detail.sections

import android.annotation.SuppressLint
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robertomr99.atmosphere.R
import com.robertomr99.atmosphere.domain.weather.Wind
import com.robertomr99.atmosphere.ui.common.CompactSectionBackground
import kotlin.math.cos
import kotlin.math.sin

@SuppressLint("DefaultLocale")
@Composable
fun WindSection(wind: Wind?, modifier: Modifier) {
    val windSpeed = wind?.speed ?: 0.0
    val windGust = wind?.gust ?: 0.0
    val windDegrees = wind?.deg ?: 0

    CompactSectionBackground(modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally){
            Row{
                Icon(
                    painter = painterResource(id = R.drawable.ic_snow),
                    contentDescription = "Viento",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text ="Viento",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    WindDataContent(windSpeed, windGust, windDegrees)
                }

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .padding(start = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WindCompass(windSpeed, windDegrees.toFloat())
                }
            }
        }
    }
}

@Composable
private fun WindDataContent(windSpeed: Double, windGust: Double, windDegrees: Int) {
    Spacer(modifier = Modifier.height(12.dp))
    ItemWindData("$windSpeed km/h", "Viento")
    Spacer(modifier = Modifier.height(12.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.5f))
    )

    Spacer(modifier = Modifier.height(12.dp))
    ItemWindData("$windGust km/h", "Rachas")
    Spacer(modifier = Modifier.height(12.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.5f))
    )

    Spacer(modifier = Modifier.height(12.dp))
    ItemWindData("$windDegrees° N", "Dirección")
}

@Composable
private fun ItemWindData(item: String, title: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text= title,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
            )
        Text(
            text = item,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun WindCompass(windSpeed: Double, rotationAngle: Float) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2 - 4.dp.toPx()

            drawCircle(
                color = Color.Transparent,
                radius = radius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            val tickLength = 4.dp.toPx()
            val outerRadius = radius - 2.dp.toPx()

            for (i in 0 until 360 step 10) {
                val angle = Math.toRadians(i.toDouble())
                val startX = center.x + (outerRadius - tickLength / 2) * cos(angle).toFloat()
                val startY = center.y + (outerRadius - tickLength / 2) * sin(angle).toFloat()
                val endX = center.x + outerRadius * cos(angle).toFloat()
                val endY = center.y + outerRadius * sin(angle).toFloat()

                drawLine(
                    color = Color.White.copy(alpha = 0.8f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val cardinalPoints = listOf("N", "E", "S", "O")
            val cardinalAngles = listOf(270f, 0f, 90f, 180f)

            cardinalPoints.forEachIndexed { index, point ->
                val angle = Math.toRadians(cardinalAngles[index].toDouble())
                val textX = center.x + (radius - 12.dp.toPx()) * cos(angle).toFloat()
                val textY = center.y + (radius - 12.dp.toPx()) * sin(angle).toFloat() + 5.dp.toPx()

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        point,
                        textX,
                        textY,
                        Paint().apply {
                            color = android.graphics.Color.WHITE
                            textAlign = Paint.Align.CENTER
                            textSize = 10.sp.toPx()
                        }
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = "$windSpeed",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "km/h",
                color = Color.White,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Canvas(modifier = Modifier
                .fillMaxSize()
                .rotate(rotationAngle)
            ) {
                val center = Offset(size.width / 2, size.height / 2)


                val arrowLength = size.height * 0.14f
                val arrowHeadWidth = 8.dp.toPx()
                val arrowBodyWidth = 2.dp.toPx()

                val startDistance = 24.dp.toPx()

                val arrowStartX = center.x
                val arrowStartY = center.y - startDistance

                val arrowEndX = center.x
                val arrowEndY = center.y - startDistance - arrowLength

                val path = Path().apply {
                    moveTo(arrowEndX, arrowEndY)
                    lineTo(arrowEndX - arrowHeadWidth / 2, arrowEndY + arrowHeadWidth * 0.7f)
                    lineTo(arrowEndX, arrowEndY + arrowHeadWidth * 0.4f)
                    lineTo(arrowEndX + arrowHeadWidth / 2, arrowEndY + arrowHeadWidth * 0.7f)
                    close()

                    moveTo(arrowStartX - arrowBodyWidth / 2, arrowStartY)
                    lineTo(arrowStartX + arrowBodyWidth / 2, arrowStartY)
                    lineTo(arrowEndX + arrowBodyWidth / 2, arrowEndY + arrowHeadWidth * 0.7f)
                    lineTo(arrowEndX - arrowBodyWidth / 2, arrowEndY + arrowHeadWidth * 0.7f)
                    close()
                }

                drawPath(path, Color.White)
            }
        }
    }
}

@Composable
@Preview(widthDp = 400, heightDp = 250)
fun PreviewWindSection(){
    WindSection(Wind(1, 1.0, 1.0), modifier = Modifier.fillMaxSize())
}