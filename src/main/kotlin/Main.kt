@file:JvmName("BatteryAgingSimu")
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import theme.AppTheme
import java.awt.Dimension


fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(size = DpSize(800.dp, 900.dp)),
        alwaysOnTop = true,
        title = "Battery Aging Simu",
        icon = null
    ) {
        window.minimumSize = Dimension(800, 900)
        AppTheme {
            App()
        }
    }
}