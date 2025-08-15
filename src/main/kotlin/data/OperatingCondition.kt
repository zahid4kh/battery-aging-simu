package data

data class OperatingCondition(
    val time: Float,
    val ambientTemperature: Float,
    val powerConsumptionKw: Float,
    val hasCatenary: Boolean,
    val batteryOperationMode: BatteryOperationMode,
    val isCharging: Boolean,
    val isRegenerating: Boolean
)


enum class BatteryOperationMode {
    DISCHARGING,
    REGENERATING,
    CATENARY_POWERED
}