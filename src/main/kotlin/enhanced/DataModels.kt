package enhanced

data class Bus(
    val id: String,
    val routeType: RouteType,
    val batteryCapacity: Float = 300f, // kWh
    val initialSoC: Float = 0.9f, // 90%
    val avgSpeed: Float = 30f, // km/h
    val routeLength: Float = 50f, // km
    val overheadCoverage: Float = 0.3f, // assuming 30% of route has overhead lines
    val stopsPerRoute: Int = 25,
    val passengersAvg: Int = 40
)

enum class RouteType {
    CITY_CENTER,
    SUBURBAN,
    EXPRESS,
    MIXED
}

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

// at each time step
data class OperatingCondition(
    val time: Float, // hours
    val speed: Float, // km/h
    val acceleration: Float, // m/s²
    val gradient: Float, // road gradient %
    val passengers: Int,
    val ambientTemp: Float, // °C
    val isCharging: Boolean, // under overhead line
    val isRegenerating: Boolean // regenerative braking
)

data class SimulationResult(
    val bus: Bus,
    val history: List<BatteryState>,
    val conditions: List<OperatingCondition>
)