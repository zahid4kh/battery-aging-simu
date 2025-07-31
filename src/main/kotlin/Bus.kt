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
    CITY,
    SUBURBAN,
    EXPRESS
}