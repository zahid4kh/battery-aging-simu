@file:JvmName("BatteryAgingSimu")
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import theme.AppTheme


fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(size = DpSize(800.dp, 1000.dp)),
        alwaysOnTop = true,
        title = "Battery Aging Simu",
        icon = null
    ) {
        AppTheme {
            App()
        }
    }
}