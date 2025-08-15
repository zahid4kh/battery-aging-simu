package data

data class SimulationResult(
    val bus: Bus,
    val history: MutableList<BatteryState> = mutableListOf(),
    val conditions: MutableList<OperatingCondition> = mutableListOf()
)
