package enhanced.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import enhanced.SimulationResult

@Composable
fun AgingAnalysisCard(busResult: SimulationResult) {
    val initialState = busResult.history.first()
    val currentState = busResult.history.last()

    val initialCapacity = initialState.capacity
    val currentCapacity = currentState.capacity
    val capacityLoss = ((initialCapacity - currentCapacity) / initialCapacity) * 100f

    val currentSoH = currentState.soh
    val avgDoD = currentState.avgDoD

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Aging Analysis",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            MetricRow("State of Health:", "${String.format("%.1f", currentSoH)}%",
                color = when {
                    currentSoH < 80f -> Color(0xFFE74C3C)
                    currentSoH < 90f -> Color(0xFFF39C12)
                    else -> Color(0xFF2ECC71)
                }
            )
            MetricRow("Capacity Loss:", "${String.format("%.2f", capacityLoss)}%",
                color = when {
                    capacityLoss > 20f -> Color(0xFFE74C3C)
                    capacityLoss > 10f -> Color(0xFFF39C12)
                    else -> Color(0xFF2ECC71)
                }
            )
            MetricRow("Equivalent Cycles:", String.format("%.1f", currentState.cycleCount))
            MetricRow("Total Ah Throughput:", "${currentState.totalAhThroughput.toInt()} Ah")
            MetricRow("Average DoD:", "${String.format("%.1f", avgDoD)}%")
            MetricRow("Calendar Age:", "${String.format("%.2f", currentState.calendarAge)} days")

            // capacity for debugging
            MetricRow("Current Capacity:", "${String.format("%.1f", currentCapacity)} kWh")
            MetricRow("Initial Capacity:", "${String.format("%.1f", initialCapacity)} kWh")
        }
    }
}