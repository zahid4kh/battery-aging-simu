package data

data class BatteryState(
    val soc: Float,
    val voltage: Float,
    val current: Float,
    val temperature: Float,
    val cycleCount: Float,
    val totalAhThroughput: Float,
    val calendarAge: Float,
    val capacity: Float,
    val soh: Float,
    val averageDod: Float
)
