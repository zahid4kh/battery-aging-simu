import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.horizontalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResultsBox(
    remainingCapacity: Float,
    calendarLoss: Float,
    cyclicLoss: Float,
    lifetime: Float
) {
    val totalLoss = calendarLoss + cyclicLoss
    val capacityColor = when {
        remainingCapacity < 70 -> Color(0xFFE74C3C)
        remainingCapacity < 85 -> Color(0xFFF39C12)
        else -> Color(0xFF2ECC71)
    }

    val lifetimeColor = when {
        lifetime < 3 -> Color(0xFFE74C3C)
        lifetime < 5 -> Color(0xFFF39C12)
        else -> Color(0xFF27AE60)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Output",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text("After 1 Year:", fontWeight = FontWeight.Bold)

        // Capacity bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(Color(0xFFDDDDDD), MaterialTheme.shapes.medium)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(remainingCapacity / 100f)
                    .fillMaxHeight()
                    .background(
                        brush = horizontalGradient(
                            colors = listOf(Color(0xFF2ECC71), Color(0xFFF1C40F), Color(0xFFE74C3C))
                        )
                    )
            )
        }

        Text(
            "Remaining Capacity: ${"%.1f".format(remainingCapacity)}%",
            fontWeight = FontWeight.Bold
        )

        Text("Calendar Aging Loss: ${"%.1f".format(calendarLoss)}%")
        Text("Cyclic Aging Loss: ${"%.1f".format(cyclicLoss)}%")
        Text("Total Loss: ${"%.1f".format(totalLoss)}%")

        Text("Predicted Battery Lifetime:", fontWeight = FontWeight.Bold)
        Text(
            text = if (lifetime.isInfinite()) "∞ years until 80% capacity" else "${"%.1f".format(lifetime)} years until 80% capacity",
            fontSize = 24.sp,
            color = lifetimeColor,
            fontWeight = FontWeight.Bold
        )
    }
}