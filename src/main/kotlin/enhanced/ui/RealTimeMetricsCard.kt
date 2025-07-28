package enhanced.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import enhanced.SimulationResult


@Composable
fun RealTimeMetricsCard(
    busResult: SimulationResult,
    currentStep: Int
) {
    val state = if (currentStep < busResult.history.size)
        busResult.history[currentStep]
    else busResult.history.last()

    val condition = if (currentStep < busResult.conditions.size)
        busResult.conditions[currentStep]
    else busResult.conditions.last()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Real-Time Metrics - ${busResult.bus.id}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Battery metrics
            MetricRow("State of Charge:", "${(state.soc * 100).toInt()}%",
                color = when {
                    state.soc < 0.2f -> Color(0xFFE74C3C)
                    state.soc < 0.5f -> Color(0xFFF39C12)
                    else -> Color(0xFF2ECC71)
                }
            )
            MetricRow("Voltage:", "${state.voltage.toInt()} V")
            MetricRow("Current:", "${state.current.toInt()} A",
                color = if (state.current > 0) Color(0xFFE74C3C) else Color(0xFF2ECC71)
            )
            MetricRow("Temperature:", "${state.temperature.toInt()}°C",
                color = when {
                    state.temperature > 35f -> Color(0xFFE74C3C)
                    state.temperature < 15f -> Color(0xFF3498DB)
                    else -> Color(0xFF2ECC71)
                }
            )

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            // Operating conditions
            MetricRow("Speed:", "${condition.speed.toInt()} km/h")
            MetricRow("Passengers:", "${condition.passengers}")
            MetricRow("Overhead Line:", if (condition.isCharging) "Connected" else "Disconnected",
                color = if (condition.isCharging) Color(0xFF2ECC71) else Color.Gray
            )
            MetricRow("Regeneration:", if (condition.isRegenerating) "Active" else "Inactive",
                color = if (condition.isRegenerating) Color(0xFF3498DB) else Color.Gray
            )
        }
    }
}


@Composable
fun MetricRow(
    label: String,
    value: String,
    color: Color = Color.Black
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value, color = color, fontWeight = FontWeight.Bold)
    }
}