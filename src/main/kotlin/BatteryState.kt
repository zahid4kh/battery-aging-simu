data class BatteryState(
    val soc: Float,
    val voltage: Float,
    val current: Float,
    val temperature: Float,
    val cycleCount: Float,
    val totalAhThroughput: Float,
    val calendarAge: Float, // days
    val capacity: Float, // current capacity in kWh
    val soh: Float, // SOH in %
    val avgDoD: Float = 0f,
    val cycleStartSoC: Float = 0f,
    val isInDischarge: Boolean = false,
    val completedCycles: Int = 0
)