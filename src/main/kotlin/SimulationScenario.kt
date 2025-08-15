data class SimulationScenario(
    val name: String,
    val temperature: Float,
    val socWindow: Pair<Float, Float>,
    val targetDoD: Float,
    val cRate: Float
)