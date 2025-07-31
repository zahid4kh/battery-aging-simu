import kotlin.random.Random

class BusOperationGenerator {

    fun generateOperatingConditions(
        durationHours: Float,
        timeStepHours: Float = 0.1f,
        tempCelsius: Float = 25f,
        overheadCoverage: Float = 0.3f
    ): List<OperatingCondition> {
        val conditions = mutableListOf<OperatingCondition>()
        var currentTime = 0f

        while (currentTime < durationHours) {
            val speed = Random.nextFloat() * 40f + 10f
            val acceleration = (Random.nextFloat() - 0.5f) * 2f
            val gradient = (Random.nextFloat() - 0.5f) * 4f
            val passengers = Random.nextInt(10, 60)
            val isCharging = Random.nextFloat() < overheadCoverage
            val isRegenerating = !isCharging && Random.nextFloat() < 0.2f

            conditions.add(OperatingCondition(
                time = currentTime,
                speed = speed,
                acceleration = acceleration,
                gradient = gradient,
                passengers = passengers,
                ambientTemp = tempCelsius,
                isCharging = isCharging,
                isRegenerating = isRegenerating
            ))

            currentTime += timeStepHours
        }

        return conditions
    }
}