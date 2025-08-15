
import utils.hoursToDays
import utils.yearsToHours
import kotlin.random.Random

class SimulationRunner {
    private val busSimulation = BusSimulation()
    private val operationGenerator = BusOperationGenerator()

    fun runComparison(durationHours: Float = yearsToHours(1f)) {
        val scenarios = listOf(
            SimulationScenario("Cold_Narrow", 10f, Pair(0.2f, 0.8f), 0.1f, 0.5f),
            SimulationScenario("Normal_Narrow", 25f, Pair(0.2f, 0.8f), 0.1f, 0.5f),
            SimulationScenario("Hot_Narrow", 40f, Pair(0.2f, 0.8f), 0.1f, 0.5f),

            SimulationScenario("Normal_Medium", 25f, Pair(0.3f, 0.9f), 0.5f, 1f),
            SimulationScenario("Normal_Wide", 25f, Pair(0.5f, 1.0f), 0.5f, 1f),

            SimulationScenario("High_DoD", 25f, Pair(0.2f, 0.8f), 0.8f, 2f),
            SimulationScenario("High_CRate", 25f, Pair(0.2f, 0.8f), 0.5f, 2f)
        )

        println("Scenario,FinalSoH,CalendarAge,CycleCount,CapacityLoss")

        scenarios.forEach { scenario ->
            val bus = Bus(
                id = "TestBus",
                routeType = RouteType.CITY,
                batteryCapacity = 300f,
                initialSoC = (scenario.socWindow.first + scenario.socWindow.second) / 2f,
                stopsPerRoute = Random.nextInt(15, 35),
                passengersAvg = Random.nextInt(25, 55)
            )

            val conditions = operationGenerator.generateOperatingConditions(
                durationHours = durationHours,
                tempCelsius = scenario.temperature
            )

            val result = busSimulation.simulateBattery(bus, conditions, scenario.socWindow)
            val finalState = result.history.last()
            val capacityLoss = ((bus.batteryCapacity - finalState.capacity) / bus.batteryCapacity) * 100f

            println("${scenario.name},${finalState.soh},${finalState.calendarAge},${finalState.cycleCount},$capacityLoss")
        }
    }

    fun runSingleScenario(
        durationHours: Float,
        temperature: Float = 25f,
        socWindow: Pair<Float, Float> = Pair(0.2f, 0.8f)
    ) {
        val bus = Bus(
            id = "SingleTestBus",
            routeType = RouteType.CITY,
            batteryCapacity = 300f,
            initialSoC = 0.8f,
            stopsPerRoute = Random.nextInt(20, 30),
            passengersAvg = Random.nextInt(30, 50)
        )

        val conditions = operationGenerator.generateOperatingConditions(
            durationHours = durationHours,
            tempCelsius = temperature
        )

        val result = busSimulation.simulateBattery(bus, conditions, socWindow)
        val finalState = result.history.last()

        println("Single Scenario Results:")
        println("Duration: ${hoursToDays(durationHours)} days")
        println("Temperature: ${temperature}°C")
        println("SoC Window: ${socWindow.first * 100}% - ${socWindow.second * 100}%")
        println("Final SoH: ${finalState.soh}%")
        println("Calendar Age: ${finalState.calendarAge} days")
        println("Cycle Count: ${finalState.cycleCount}")
        println("Capacity Loss: ${((bus.batteryCapacity - finalState.capacity) / bus.batteryCapacity) * 100f}%")
    }
}