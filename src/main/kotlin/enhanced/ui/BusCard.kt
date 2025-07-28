package enhanced.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import enhanced.SimulationResult

@Composable
fun BusCard(
    busResult: SimulationResult,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val currentState = busResult.history.last()
    val socColor = when {
        currentState.soc < 0.2f -> Color(0xFFE74C3C)
        currentState.soc < 0.5f -> Color(0xFFF39C12)
        else -> Color(0xFF2ECC71)
    }

    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
                else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF0F8FF) else Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                busResult.bus.id,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                busResult.bus.routeType.name.replace('_', ' '),
                fontSize = 12.sp,
                color = Color.Gray
            )

            // SoC indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color(0xFFE0E0E0), MaterialTheme.shapes.small)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(currentState.soc)
                        .fillMaxHeight()
                        .background(socColor, MaterialTheme.shapes.small)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${(currentState.soc * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = socColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "SoH: ${currentState.soh.toInt()}%",
                    fontSize = 12.sp,
                    color = if (currentState.soh > 90) Color(0xFF2ECC71) else Color(0xFFE74C3C)
                )
            }
        }
    }
}