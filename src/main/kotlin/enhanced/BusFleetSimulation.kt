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
        val energySocChange = energyChange / bus.batteryCapacity

        // New SoC with charging logic
        var newSoC = state.soc - energySocChange

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

        newSoC = newSoC.coerceIn(0.02f, 1f) // allowing 2% discharge floor for SOC

        // Temperature model (simplified)
        val targetTemp = 25f
        val heatGenerated = abs(limitedCurrent).pow(2) * 0.0001f * dt
        val cooling = (state.temperature - targetTemp) * 0.3f * dt // stronger cooling
        val newTemp = (state.temperature + heatGenerated - cooling).coerceIn(-20f, 80f)

        //for frequent charging scenarios
        var newAvgDoD = state.avgDoD
        var newCycleStartSoC = state.cycleStartSoC
        var newIsInDischarge = state.isInDischarge
        var newCompletedCycles = state.completedCycles

        val socDifference = newSoC - state.soc

        // Micro-cycle detection (for city buses)
        if (!condition.isCharging) { // Only count when not charging
            if (socDifference < -0.001f && !state.isInDischarge) {
                // Start new discharge phase
                newIsInDischarge = true
                newCycleStartSoC = state.soc
                println("Bus ${bus.id} Step $currentStep: STARTING micro-discharge from ${(state.soc * 100).toInt()}%")
            }
        } else {
            if (state.isInDischarge) {
                newIsInDischarge = false
                val cycleDoD = (state.cycleStartSoC - state.soc) * 100f

                if (cycleDoD > 0.5f) {
                    newCompletedCycles++
                    newAvgDoD = if (state.completedCycles > 0) {
                        (state.avgDoD * state.completedCycles + cycleDoD) / (state.completedCycles + 1)
                    } else {
                        cycleDoD
                    }
                    println("Bus ${bus.id} Step $currentStep: COMPLETED micro-cycle $newCompletedCycles: ${(state.cycleStartSoC * 100).toInt()}% → ${(state.soc * 100).toInt()}% = ${cycleDoD.toInt()}% DoD, Avg=${newAvgDoD.toInt()}%")
                }
            }
        }

        // Calculating DoD from recent SoC range every hour
        if (currentStep > 0 && currentStep % 360 == 0) { // Every 6 hours
            val socRange = when (bus.routeType) {
                RouteType.CITY_CENTER -> 8f // City bus: 8% DoD per cycle
                RouteType.EXPRESS -> 35f // Express: 35% DoD per cycle
                RouteType.SUBURBAN -> 18f // Suburban: 18% DoD per cycle
                else -> 20f
            }

            newAvgDoD = (if (newAvgDoD < 5f) { // If cycle detection failed
                println("Bus ${bus.id} Step $currentStep: Using route-based DoD estimate = ${socRange.toInt()}%")
                socRange
            } else {
                newAvgDoD
            })
        }

        // Ensure minimum DoD for aging calculations
        newAvgDoD = maxOf(newAvgDoD, 2f) // Minimum 2% instead of 1%

        val socDelta = abs(energySocChange) * 100f // percentage change
        val cycleIncrement = socDelta / 100f // one full cycle = 100% SOC change
        val newCycles = state.cycleCount + cycleIncrement

        val newAhThroughput = state.totalAhThroughput + abs(limitedCurrent * dt)

        // C-rate calculation (current relative to capacity)
        val nominalCapacityAh = bus.batteryCapacity * 1000f / 3.7f // Assume 3.7V nominal
        val cRate = abs(limitedCurrent) / nominalCapacityAh

        // progressive aging factor that increases over time
        val progressiveAgingFactor = 1.0f + (currentStep / 500f) // Increasing aging over simulation

        // Aging calculations with progressive factor
        val calendarLoss = agingModel.calculateCalendarAging(
            state.calendarAge,
            newTemp + 273.15f,
            newSoC
        ) * progressiveAgingFactor

        val cyclicLoss = agingModel.calculateCyclicAging(
            newCycles,
            newTemp + 273.15f,
            newAvgDoD,
            cRate
        ) * progressiveAgingFactor

        // Total aging (capacity loss percentage)
        val totalLoss = (calendarLoss + cyclicLoss).coerceIn(
            minOf(0.1f + currentStep * 0.001f, 20f), // Minimum loss increases with time
            80f
        )

        if (currentStep % 60 == 0) {
            println("Step $currentStep: CalLoss=$calendarLoss%, CycLoss=$cyclicLoss%, TotalLoss=$totalLoss%, SOH=${(1f - totalLoss/100f)*100f}%, DoD=${newAvgDoD.toInt()}%")
        }

        val newCapacity = bus.batteryCapacity * (1f - totalLoss / 100f)
        val newSoH = (newCapacity / bus.batteryCapacity * 100f).coerceIn(20f, 100f)

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
            calendarAge = maxOf(0f, state.calendarAge + dt / 24f),
            capacity = newCapacity.coerceIn(bus.batteryCapacity * 0.2f, bus.batteryCapacity),
            soh = newSoH,
            avgDoD = newAvgDoD.coerceIn(2f, 100f), // Minimum 2%
            cycleStartSoC = newCycleStartSoC,
            isInDischarge = newIsInDischarge,
            completedCycles = newCompletedCycles
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