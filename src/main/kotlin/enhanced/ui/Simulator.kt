package enhanced.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import enhanced.*
import kotlinx.coroutines.delay

@Composable
fun EnhancedBatterySimulator() {
    var selectedBusIndex by remember { mutableStateOf(0) }
    var simulationTime by remember { mutableStateOf(24f) } // hours
    var isRunning by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(0) }

    val fleet = remember { createMockFleet() }
    val simulation = remember { BusFleetSimulation() }
    val profileGen = remember { RouteProfile() }

    val initialStates = remember {
        fleet.map { bus ->
            BatteryState(
                soc = bus.initialSoC,
                voltage = 350f,
                current = 0f,
                temperature = 25f,
                cycleCount = 0f,
                totalAhThroughput = 0f,
                calendarAge = 0f,
                capacity = bus.batteryCapacity,
                soh = 100f,
                avgDoD = 0f
            )
        }
    }

    var simulationResults by remember {
        mutableStateOf(
            fleet.mapIndexed { index, bus ->
                SimulationResult(
                    bus = bus,
                    history = listOf(initialStates[index]),
                    conditions = profileGen.generateDrivingProfile(bus, simulationTime)
                )
            }
        )
    }

    // Simulation effect
    LaunchedEffect(isRunning, currentStep) {
        if (isRunning && currentStep < simulationResults[0].conditions.size) {
            // one step for all buses
            val newResults = simulationResults.mapIndexed { busIndex, result ->
                if (currentStep < result.conditions.size) {
                    val currentState = result.history.last()
                    val condition = result.conditions[currentStep]
                    val newState = simulation.simulateTimeStep(
                        bus = result.bus,
                        state = currentState,
                        condition = condition,
                        currentStep = currentStep
                    )
                    result.copy(
                        history = result.history + newState
                    )
                } else result
            }
            simulationResults = newResults
            currentStep++

            delay(50) // Faster simulation
        } else if (currentStep >= simulationResults[0].conditions.size) {
            isRunning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Enhanced Bus Fleet Battery Simulation",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Control Panel
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Simulation controls
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                if (!isRunning) {
                                    isRunning = true
                                }
                            },
                            enabled = !isRunning && currentStep < simulationResults[0].conditions.size
                        ) {
                            Text(if (currentStep == 0) "Start Simulation" else "Resume")
                        }

                        Button(
                            onClick = { isRunning = false },
                            enabled = isRunning
                        ) {
                            Text("Pause")
                        }

                        Button(
                            onClick = {
                                isRunning = false
                                currentStep = 0
                                simulationResults = fleet.mapIndexed { index, bus ->
                                    SimulationResult(
                                        bus = bus,
                                        history = listOf(initialStates[index]),
                                        conditions = profileGen.generateDrivingProfile(bus, simulationTime)
                                    )
                                }
                            }
                        ) {
                            Text("Reset")
                        }

                        Button(
                            onClick = {
                                val exporter = DataExporter()
                                exporter.exportSimulationResults(simulationResults)
                            }
                        ) {
                            Text("Export Data")
                        }
                    }

                    // Time display
                    Text(
                        "Time: ${String.format("%.1f", currentStep / 60f)} / $simulationTime hours",
                        fontWeight = FontWeight.Medium
                    )
                }

                // Simulation time slider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Simulation Duration:", modifier = Modifier.width(150.dp))
                    Slider(
                        value = simulationTime,
                        onValueChange = {
                            if (!isRunning) {
                                simulationTime = it
                            }
                        },
                        valueRange = 1f..168f, // Up to 1 week
                        modifier = Modifier.weight(1f),
                        enabled = !isRunning && currentStep == 0
                    )
                    Text("${simulationTime.toInt()} hrs", modifier = Modifier.width(60.dp))
                }
            }
        }

        // Fleet Overview
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F4F8))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Fleet Overview",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(simulationResults.size) { index ->
                        BusCard(
                            busResult = simulationResults[index],
                            isSelected = selectedBusIndex == index,
                            onClick = { selectedBusIndex = index }
                        )
                    }
                }
            }
        }

        if (simulationResults.isNotEmpty()) {
            val selectedResult = simulationResults[selectedBusIndex]

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(0.4f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RealTimeMetricsCard(
                        busResult = selectedResult,
                        currentStep = minOf(currentStep, selectedResult.history.size - 1)
                    )
                }

                Column(
                    modifier = Modifier.weight(0.6f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AgingAnalysisCard(
                        busResult = selectedResult
                    )
                }
            }
        }
    }
}