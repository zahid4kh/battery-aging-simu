package enhanced

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DataExporter {

    fun exportSimulationResults(
        results: List<SimulationResult>,
        filename: String = "sim.csv"
    ) {
        try {
            val file = File(filename)
            val content = buildString {
                // CSV Header
                appendLine("BusID,RouteType,TimeStep,SoC,Voltage,Current,Temperature,CycleCount,AhThroughput,CalendarAge,Capacity,SoH,AvgDoD,Speed,Passengers,IsCharging,IsRegenerating")

                // Data rows
                results.forEach { result ->
                    result.history.forEachIndexed { index, state ->
                        val condition = if (index < result.conditions.size)
                            result.conditions[index]
                        else result.conditions.last()

                        appendLine("${result.bus.id},${result.bus.routeType},$index,${state.soc},${state.voltage},${state.current},${state.temperature},${state.cycleCount},${state.totalAhThroughput},${state.calendarAge},${state.capacity},${state.soh},${state.avgDoD},${condition.speed},${condition.passengers},${condition.isCharging},${condition.isRegenerating}")
                    }
                }
            }

            file.writeText(content)
            println("Simulation data exported to: $filename")

        } catch (e: Exception) {
            println("Export failed: ${e.message}")
        }
    }

    private fun getCurrentTimestamp(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
    }
}