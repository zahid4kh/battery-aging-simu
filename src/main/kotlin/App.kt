
import androidx.compose.runtime.*
import enhanced.ui.EnhancedBatterySimulator


@Composable
fun App() {
    var showEnhanced by remember { mutableStateOf(false) }

//    Column(
//        modifier = Modifier.verticalScroll(rememberScrollState())
//    ) {
//        Row {
//            Button(onClick = { showEnhanced = false }) {
//                Text("Simple Model")
//            }
//            Button(onClick = { showEnhanced = true }) {
//                Text("Enhanced Model")
//            }
//        }
//
//        if (showEnhanced) {
//            EnhancedBatterySimulator()
//        } else {
//            simple.BatteryAgingSimulator()
//        }
//    }

    EnhancedBatterySimulator()
}

