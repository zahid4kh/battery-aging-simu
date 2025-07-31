data class SimulationResult(
    val bus: Bus,
    val history: List<BatteryState>,
    val conditions: List<OperatingCondition>
)