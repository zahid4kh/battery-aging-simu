@file:JvmName("BatteryAgingSimu")

import utils.daysToHours
import utils.weeksToHours
import utils.yearsToHours


fun main() {
    val runner = SimulationRunner()

    println("=== Battery Aging Comparison Simulation ===")
    println()

    println("1. Full Comparison (1 year simulation):")
    runner.runComparison(yearsToHours(1f))

    println()
    println("2. Quick Test (1 week simulation):")
    runner.runComparison(weeksToHours(1f))

    println()
    println("3. Single Scenario Example (30 days):")
    runner.runSingleScenario(daysToHours(30f), 25f, Pair(0.3f, 0.9f))
}