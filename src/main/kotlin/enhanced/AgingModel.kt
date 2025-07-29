package enhanced

import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

class AgingModel {

    // Calendar aging based on SEI growth (Eyring-Arrhenius)
    fun calculateCalendarAging(
        timeInDays: Float,
        tempK: Float,
        soc: Float
    ): Float {
        if (timeInDays <= 0f) return 0f

        // fitted from literature
        val A_cal = 0.012f    // Pre-exponential factor
        val Ea_cal = 24000f    // Activation energy [J/mol]
        val R = 8.314f         // Gas constant [J/mol·K]
        val alpha_soc = 0.8f   // SoC stress factor

        // SoC stress factor (high SoC accelerates aging)
        val socStress = 1f + alpha_soc * (soc - 0.5f).pow(2)

        // Temperature stress (Arrhenius)
        val tempStress = exp(-Ea_cal / (R * tempK)) // * 10000000f // scale

        // Time dependence (square root for SEI growth)
        val timeStress = sqrt(timeInDays)

        val calendarLoss = A_cal * tempStress * socStress * timeStress
        if (timeInDays > 1f) {
            println("Calendar: time=${timeInDays}d, temp=${tempK-273}°C, soc=${soc*100}%, loss=${calendarLoss}%")
        }
        return calendarLoss.coerceIn(0f, 50f) // Reasonable bounds
    }

    // Cyclic aging model (power law with stress factors)
    fun calculateCyclicAging(
        cycleCount: Float,
        tempK: Float,
        avgDoD: Float,
        cRate: Float
    ): Float {
        if (cycleCount <= 0f) return 0f

        val A_cyc = 0.008f    // Pre-exponential factor
        val Ea_cyc = 12000f    // Activation energy [J/mol]
        val R = 8.314f         // Gas constant [J/mol·K]
        val beta_dod = 1.8f    // DoD exponent
        val beta_crate = 0.3f  // C-rate exponent

        // DoD stress factor (power law)
        val dodStress = (avgDoD / 100f).pow(beta_dod)

        // C-rate stress factor
        val crateStress = (1f + cRate).pow(beta_crate)

        // Temperature stress (Arrhenius)
        val tempStress = exp(-Ea_cyc / (R * tempK)) // *50000f // scale

        // Cycle count (linear in log scale)
        val cycleStress = cycleCount / 100f // per 100 cycles

        val cyclicLoss = A_cyc * tempStress * dodStress * crateStress * cycleStress
        if (cycleCount > 1f) {
            println("Cyclic: cycles=${cycleCount}, DoD=${avgDoD}%, C-rate=${cRate}, loss=${cyclicLoss}%")
        }
        return cyclicLoss.coerceIn(0f, 50f)
    }
}