package enhanced

import kotlin.math.sin

class RouteProfile {

    fun generateDrivingProfile(
        bus: Bus,
        duration: Float // hours
    ): List<OperatingCondition> {
        val conditions = mutableListOf<OperatingCondition>()
        val timeSteps = (duration * 60).toInt() // 1-minute resolution

        for (i in 0 until timeSteps) {
            val time = i / 60f
            val positionKm = (time * bus.avgSpeed) % bus.routeLength
            val stopPhase = (positionKm / (bus.routeLength / bus.stopsPerRoute)).toInt()

            // Determine if at stop, accelerating, cruising, or braking
            val phase = when ((i % 10)) {
                0, 1 -> "stop"
                2, 3 -> "accelerate"
                4, 5, 6, 7 -> "cruise"
                else -> "brake"
            }

            val (speed, accel) = when (phase) {
                "stop" -> Pair(0f, 0f)
                "accelerate" -> Pair(bus.avgSpeed * 0.5f, 1.5f)
                "cruise" -> Pair(bus.avgSpeed, 0f)
                else -> Pair(bus.avgSpeed * 0.7f, -2f)
            }

            // Check if under overhead line
            val overheadPosition = positionKm / bus.routeLength
            val isCharging = when (bus.routeType) {
                RouteType.CITY_CENTER -> overheadPosition < 0.5f // City center coverage
                RouteType.EXPRESS -> overheadPosition > 0.8f // End terminals
                else -> (overheadPosition < bus.overheadCoverage)
            }

            conditions.add(
                OperatingCondition(
                    time = time,
                    speed = speed,
                    acceleration = accel,
                    gradient = sin(positionKm * 0.5f) * 3f, // ±3% grade
                    passengers = (bus.passengersAvg * (0.7f + 0.6f * sin(time * 2))).toInt(),
                    ambientTemp = 20f + 5f * sin(time * 0.1f), // Daily temp variation
                    isCharging = isCharging && phase == "stop",
                    isRegenerating = phase == "brake"
                )
            )
        }

        return conditions
    }
}