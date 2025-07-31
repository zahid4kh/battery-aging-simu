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