
import utils.hoursToDays
import kotlin.math.abs

class BusSimulation(private val agingModel: AgingModel = AgingModel()) {

    fun simulateBattery(
        bus: Bus,
        conditions: List<OperatingCondition>,
        socWindow: Pair<Float, Float> = Pair(0.2f, 0.8f)
    ): SimulationResult {
        var batteryState = BatteryState(
            soc = bus.initialSoC,
            voltage = 360f,
            current = 0f,
            temperature = conditions.first().ambientTemp,
            cycleCount = 0f,
            totalAhThroughput = 0f,
            calendarAge = 0f,
            capacity = bus.batteryCapacity,
            soh = 100f
        )

        val history = mutableListOf(batteryState)
        var totalEFC = 0f
        var avgSoC = bus.initialSoC
        var avgDoD = 0f
        var cycleCount = 0

        for (i in 1 until conditions.size) {
            val condition = conditions[i]
            val prevCondition = conditions[i-1]
            val dt = condition.time - prevCondition.time

            batteryState = updateBatteryState(
                batteryState,
                condition,
                dt,
                socWindow,
                bus.batteryCapacity
            )

            if (i % 100 == 0) { // every 100 steps
                avgSoC = history.takeLast(100).map { it.soc }.average().toFloat()
                avgDoD = calculateAverageDoD(history.takeLast(100))
                totalEFC += avgDoD / 2f

                val calendarLoss = agingModel.calculateCalendarAging(
                    condition.time,
                    condition.ambientTemp,
                    avgSoC
                )

                val cyclicLoss = agingModel.calculateCyclicAging(
                    totalEFC,
                    condition.ambientTemp,
                    avgSoC,
                    avgDoD
                )

                val totalLoss = calendarLoss + cyclicLoss
                val newCapacity = bus.batteryCapacity * (1f - totalLoss)
                val newSoH = (newCapacity / bus.batteryCapacity) * 100f

                batteryState = batteryState.copy(
                    capacity = newCapacity,
                    soh = newSoH,
                    calendarAge = hoursToDays(condition.time),
                    cycleCount = totalEFC,
                    avgDoD = avgDoD
                )
            }

            history.add(batteryState)
        }

        return SimulationResult(bus, history, conditions)
    }

    private fun updateBatteryState(
        state: BatteryState,
        condition: OperatingCondition,
        dt: Float,
        socWindow: Pair<Float, Float>,
        nominalCapacity: Float
    ): BatteryState {
        var newSoC = state.soc
        var current = 0f

        when {
            condition.isCharging && state.soc < socWindow.second -> {
                current = -100f
                newSoC = minOf(socWindow.second, state.soc + (abs(current) * dt / nominalCapacity))
            }
            condition.isRegenerating && state.soc < socWindow.second -> {
                current = -50f
                newSoC = minOf(socWindow.second, state.soc + (abs(current) * dt / nominalCapacity))
            }
            !condition.isCharging -> {
                current = 80f + condition.passengers * 1.5f
                newSoC = maxOf(socWindow.first, state.soc - (current * dt / nominalCapacity))
            }
        }

        val voltage = 300f + (newSoC * 100f)
        val throughput = state.totalAhThroughput + abs(current) * dt

        return state.copy(
            soc = newSoC,
            voltage = voltage,
            current = current,
            temperature = condition.ambientTemp,
            totalAhThroughput = throughput
        )
    }

    private fun calculateAverageDoD(history: List<BatteryState>): Float {
        if (history.size < 2) return 0f
        val socValues = history.map { it.soc }
        val maxSoC = socValues.maxOrNull() ?: 0f
        val minSoC = socValues.minOrNull() ?: 0f
        return maxSoC - minSoC
    }
}