package enhanced

import kotlin.math.abs
import kotlin.math.pow

class BusFleetSimulation {
    private val agingModel = AgingModel()
    private val powerModel = PowerDemand()

    // each time step
    fun simulateTimeStep(
        bus: Bus,
        state: BatteryState,
        condition: OperatingCondition,
        dt: Float = 1f/60f, // 1 minute time step
        currentStep: Int = 0
    ): BatteryState {
        // power demand
        val busWeight = 18000f + condition.passengers * 70f // kg
        val power = if (condition.acceleration >= 0) {
            powerModel.calculatePowerDemand(
                condition.speed / 3.6f, // to m/s
                condition.acceleration,
                condition.gradient / 100f,
                busWeight
            )
        } else {
            -powerModel.calculateRegenPower(
                condition.speed / 3.6f,
                -condition.acceleration,
                busWeight
            )
        }

        // Battery parameters
        val ocv = calculateOCV(state.soc)
        val batteryVoltage = if (abs(power) > 0.1f) {
            ocv - (power / ocv) * 0.05f
        } else {
            ocv
        }

        val current = if (abs(batteryVoltage) > 50f) {
            (power * 1000f) / batteryVoltage
        } else {
            0f
        }
        val limitedCurrent = current.coerceIn(-400f, 400f)

        // Energy change
        val energyChange = power * dt // kWh
        val socChange = energyChange / bus.batteryCapacity

        // New SoC with charging logic
        var newSoC = state.soc - socChange

        // Overhead line charging
        if (condition.isCharging && newSoC < 0.9f) {
            val chargeRate = 150f // kW overhead charging
            val chargeSoC = (chargeRate * dt) / bus.batteryCapacity
            newSoC += chargeSoC
        }

        // Regenerative braking protection
        if (condition.isRegenerating && newSoC > 0.95f) {
            // Dump excess energy or limit regen.
            newSoC = minOf(newSoC, 0.98f)
        }

        newSoC = newSoC.coerceIn(0.05f, 1f) // allowing SOC go down to 5%

        // Temperature model (simplified)
        val heatGenerated = abs(limitedCurrent).pow(2) * 0.0001f * dt
        val cooling = (state.temperature - condition.ambientTemp) * 0.1f * dt
        val newTemp = (state.temperature + heatGenerated - cooling).coerceIn(-20f, 80f)

        // Cycle counting
        val socDelta = abs(socChange) * 100f // percentage change
        val cycleIncrement = socDelta / 200f // 200% total change -> 1 cycle
        val newCycles = state.cycleCount + cycleIncrement

        val newAvgDoD = if (abs(socChange) > 0.001f) { // updating only for significant changes
            val currentDoD = abs(socChange) * 100f
            if (state.avgDoD > 0f) {
                // Exponential moving average
                state.avgDoD * 0.95f + currentDoD * 0.05f
            } else {
                currentDoD
            }
        } else {
            state.avgDoD
        }.coerceIn(0.1f, 100f)

        val newAhThroughput = state.totalAhThroughput + abs(limitedCurrent * dt)

        // C-rate calculation (current relative to capacity)
        val nominalCapacityAh = bus.batteryCapacity * 1000f / 3.7f // Assume 3.7V nominal
        val cRate = abs(limitedCurrent) / nominalCapacityAh

        // trying more realistic aging calculation
        val timeInDays = currentStep / (60f * 24f) // Convert minutes to days

        // Aging calculations with progressive factor
        val calendarLoss = agingModel.calculateCalendarAging(
            timeInDays,
            newTemp + 273.15f,
            newSoC
        )

        val cyclicLoss = agingModel.calculateCyclicAging(
            newCycles,
            newTemp + 273.15f,
            newAvgDoD,
            cRate
        )

        // Total aging (capacity loss percentage)
        // simpler with realistic bounds
        val totalLoss = (calendarLoss + cyclicLoss).coerceIn(0f, 80f)

        val newCapacity = bus.batteryCapacity * (1f - totalLoss / 100f)
        val newSoH = ((1f - totalLoss / 100f) * 100f).coerceIn(20f, 100f)

        // logging every hour
        if (currentStep % 60 == 0 && currentStep > 0) {
            println("Step ${currentStep}: SOH=${newSoH}%, Temp=${newTemp}°C, TotalLoss=${totalLoss}%")
        }

        // Minimal smoothing to avoid shaking in progress bars
        val smoothedTemp = state.temperature * 0.8f + newTemp * 0.2f
        val smoothedCurrent = state.current * 0.7f + limitedCurrent * 0.3f

        return BatteryState(
            soc = newSoC.coerceIn(0f, 1f),
            voltage = batteryVoltage.coerceIn(200f, 450f),
            current = smoothedCurrent,
            temperature = smoothedTemp.coerceIn(-40f, 80f),
            cycleCount = maxOf(0f, newCycles),
            totalAhThroughput = maxOf(0f, newAhThroughput),
            calendarAge = maxOf(0f, timeInDays),
            capacity = newCapacity.coerceIn(bus.batteryCapacity * 0.2f, bus.batteryCapacity),
            soh = newSoH,
            avgDoD = newAvgDoD
        )
    }

    // OCV curve (simplified LFP)
    private fun calculateOCV(soc: Float): Float {
        return when {
            soc < 0.1f -> 280f + soc * 200f
            soc < 0.9f -> 320f + (soc - 0.1f) * 50f
            else -> 360f + (soc - 0.9f) * 200f
        }
    }
}