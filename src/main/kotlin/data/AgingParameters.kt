package data

data class AgingParameters(
    val activationEnergyCalendar: Float = 36360f,
    val activationEnergyCyclic: Float = 36360f,
    val gasConstant: Float = 8.314f,
    val timeExponent: Float = 0.789f,
    val efcExponent: Float = 0.98f,
    val preExpFactorCalendar: Float = 2.15e-4f,
    val cyclicFactor: Float = 9.31e-4f
)
