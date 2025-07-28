package enhanced.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import enhanced.BatteryState
import enhanced.SimulationResult

@Composable
fun ChartCard(
    title: String,
    busResult: SimulationResult,
    currentStep: Int,
    dataExtractor: (BatteryState) -> List<Pair<Float, Color>>,
    yAxisLabel: String
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = remember {
        TextStyle(
            fontSize = 14.sp,
            color = Color.Black
        )
    }
    val boldTextStyle = remember {
        TextStyle(
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(top = 16.dp)
            ) {
                val width = size.width
                val height = size.height
                val padding = 100f

                drawLine(
                    color = Color.Gray,
                    start = Offset(padding, height - padding),
                    end = Offset(width - padding/2, height - padding),
                    strokeWidth = 4f
                )
                drawLine(
                    color = Color.Gray,
                    start = Offset(padding, padding/2),
                    end = Offset(padding, height - padding),
                    strokeWidth = 4f
                )

                val numYLabels = 6
                for (i in 0..numYLabels) {
                    val y = height - padding - (i * (height - 1.5f * padding) / numYLabels)
                    val value = (i * 100f / numYLabels).toInt()

                    drawLine(
                        color = Color.LightGray,
                        start = Offset(padding, y),
                        end = Offset(width - padding/2, y),
                        strokeWidth = 2f
                    )

                    val yLabelText = textMeasurer.measure(
                        text = "$value%",
                        style = textStyle
                    )
                    drawText(
                        textLayoutResult = yLabelText,
                        topLeft = Offset(padding - 60f, y - yLabelText.size.height/2f)
                    )
                }

                val numXLabels = 8
                val totalMinutes = busResult.conditions.size
                for (i in 0..numXLabels) {
                    val x = padding + (i * (width - 1.5f * padding) / numXLabels)
                    val timeHours = (i * totalMinutes / numXLabels / 60f)

                    val xLabelText = textMeasurer.measure(
                        text = "${String.format("%.1f", timeHours)}h",
                        style = textStyle
                    )
                    drawText(
                        textLayoutResult = xLabelText,
                        topLeft = Offset(x - xLabelText.size.width/2f, height - padding + 20f)
                    )
                }

                val yAxisLabelText = textMeasurer.measure(
                    text = yAxisLabel,
                    style = boldTextStyle
                )
                drawText(
                    textLayoutResult = yAxisLabelText,
                    topLeft = Offset(15f, height / 2f - yAxisLabelText.size.height/2f)
                )

                val xAxisLabelText = textMeasurer.measure(
                    text = "Time (hours)",
                    style = boldTextStyle
                )
                drawText(
                    textLayoutResult = xAxisLabelText,
                    topLeft = Offset(width / 2f - xAxisLabelText.size.width/2f, height - 30f)
                )

                val chargingLegendText = textMeasurer.measure(
                    text = "● Charging",
                    style = textStyle
                )
                val regenLegendText = textMeasurer.measure(
                    text = "● Regen",
                    style = textStyle
                )

                drawText(
                    textLayoutResult = chargingLegendText,
                    topLeft = Offset(width - chargingLegendText.size.width - 20f, 30f)
                )
                drawText(
                    textLayoutResult = regenLegendText,
                    topLeft = Offset(width - regenLegendText.size.width - 20f, 60f)
                )

                drawCircle(
                    color = Color(0xFF00AA00),
                    radius = 8f,
                    center = Offset(width - chargingLegendText.size.width - 30f, 38f)
                )
                drawCircle(
                    color = Color(0xFF0055FF),
                    radius = 8f,
                    center = Offset(width - regenLegendText.size.width - 30f, 68f)
                )

                val historyToShow = busResult.history.take(minOf(currentStep + 1, busResult.history.size))
                if (historyToShow.size > 1) {
                    val xStep = (width - 1.5f * padding) / maxOf(1, busResult.conditions.size - 1)

                    historyToShow.first().let { firstState ->
                        val datasets = dataExtractor(firstState)

                        datasets.forEach { (_, color) ->
                            val path = Path()
                            var isFirst = true

                            historyToShow.forEachIndexed { index, state ->
                                val values = dataExtractor(state)
                                values.find { it.second == color }?.let { (value, _) ->
                                    val x = padding + index * xStep
                                    val y = height - padding - (value / 100f) * (height - 1.5f * padding)

                                    if (isFirst) {
                                        path.moveTo(x, y)
                                        isFirst = false
                                    } else {
                                        path.lineTo(x, y)
                                    }
                                }
                            }

                            drawPath(
                                path = path,
                                color = color,
                                style = Stroke(width = 6f)
                            )
                        }
                    }

                    historyToShow.forEachIndexed { index, _ ->
                        if (index < busResult.conditions.size) {
                            val condition = busResult.conditions[index]
                            val x = padding + index * xStep

                            if (condition.isCharging) {
                                drawCircle(
                                    color = Color(0xFF00AA00),
                                    radius = 3f,
                                    center = Offset(x, padding + 30f)
                                )
                                drawLine(
                                    color = Color(0xFF00AA00),
                                    start = Offset(x, padding + 25f),
                                    end = Offset(x, padding + 35f),
                                    strokeWidth = 2f
                                )
                            }

                            if (condition.isRegenerating) {
                                drawCircle(
                                    color = Color(0xFF0055FF),
                                    radius = 3f,
                                    center = Offset(x, padding + 50f)
                                )
                                drawLine(
                                    color = Color(0xFF0055FF),
                                    start = Offset(x, padding + 45f),
                                    end = Offset(x, padding + 55f),
                                    strokeWidth = 2f
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}