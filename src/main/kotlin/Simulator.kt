
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BatteryAgingSimulator() {
    var temp by remember { mutableStateOf(25f) }
    var soc by remember { mutableStateOf(50f) }
    var dailyCycles by remember { mutableStateOf(2f) }
    var dod by remember { mutableStateOf(40f) }

    val (calendarLoss, cyclicLoss, remainingCapacity, lifetime) = calculateAging(temp, soc, dailyCycles, dod)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "Interactive Battery Aging Simulator",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        item {
            InfoBox(
                title = "Aging models",
                content = "Take operating conditions as inputs (temperature, charge level, usage) and predict how fast the battery degrades",
                backgroundColor = Color(0xFFFFEAA7)
            )
        }

        item {
            Text(
                "Input Params",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            ParameterSlider(
                label = "Temperature:",
                value = temp,
                onValueChange = { temp = it },
                valueRange = 0f..50f,
                valueDisplay = "${temp.toInt()}°C",
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            ParameterSlider(
                label = "Storage SoC:",
                value = soc,
                onValueChange = { soc = it },
                valueRange = 0f..100f,
                valueDisplay = "${soc.toInt()}%",
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            ParameterSlider(
                label = "Daily Cycles:",
                value = dailyCycles,
                onValueChange = { dailyCycles = it },
                valueRange = 0f..10f,
                step = 0.5f,
                valueDisplay = "%.1f".format(dailyCycles),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            ParameterSlider(
                label = "Depth of Discharge:",
                value = dod,
                onValueChange = { dod = it },
                valueRange = 10f..90f,
                valueDisplay = "${dod.toInt()}%",
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            ResultsBox(
                remainingCapacity = remainingCapacity,
                calendarLoss = calendarLoss,
                cyclicLoss = cyclicLoss,
                lifetime = lifetime
            )
        }

        item {
            InfoBox(
                title = "Simplified from papers:",
                content = """
                    Calendar Loss = k1 x (T/25) x ((SoC-50)/50)^2 x sqrt(days)
                    Cyclic Loss = k2 x cycles x (DoD/100)^1.5 x (T/25)
                """.trimIndent(),
                backgroundColor = Color(0xFFF8F8F8)
            )
        }

        item {
            InfoBox(
                title = null,
                content = """
                    Keeping batteries cool -> last longer
                    Smaller discharge cycles -> less cyclic damage
                    Overhead charging helps → keeps DoD low
                """.trimIndent(),
                backgroundColor = Color(0xFFD1F2EB)
            )
        }
    }
}