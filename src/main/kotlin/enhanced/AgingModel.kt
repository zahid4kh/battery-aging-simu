package enhanced

import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

class AgingModel {

    private val k_sei_cal = 1.0f // MUCH higher calendar aging base rate
    private val Ea_cal = 5000f // VERY low activation energy (almost no temp dependence)
    private val k_sei_cyc = 2.0f // MUCH higher cyclic aging base rate
    private val Ea_cyc = 3000f // VERY low activation energy
    private val R = 8.314f // Gas constant
    private val alpha_soc = 1.0f // High SOC stress factor
    private val n_dod = 0.5f // Lower DOD exponent so small DODs still cause aging
    private val min_aging_per_day = 0.1f // Minimum 0.1% aging per day

    // calendar aging based on Krupp model
    fun calculateCalendarAging(
        days: Float,
        tempK: Float,
        soc: Float
    ): Float {
        if (days <= 0f) return 0f

        // SoC stress function (quadratic, worst at extremes)
        val socStress = 1f + alpha_soc * (soc - 0.5f).pow(2)

        val tempFactor = exp(-Ea_cal / (R * tempK))

        // SEI growth (square root of time) - capacity loss percentage
        val calendarLoss = k_sei_cal * tempFactor * socStress * sqrt(days)

        // minimum aging to ensure visible degradation
        val minAging = min_aging_per_day * days

        val totalCalendarLoss = maxOf(calendarLoss, minAging)

        return totalCalendarLoss.coerceIn(0f, 50f)
    }

    // cyclic aging based on stress factors
    fun calculateCyclicAging(
        cycles: Float,
        tempK: Float,
        avgDoD: Float,
        avgCRate: Float
    ): Float {
        if (cycles <= 0f) return 0f

        val tempFactor = exp(-Ea_cyc / (R * tempK))

        // DoD stress with lower exponent (so small DODs still matter)
        val dodFraction = (avgDoD / 100f).coerceIn(0.01f, 1f) // Min 1% to avoid zero
        val dodStress = dodFraction.pow(n_dod)

        // C-rate stress
        val cRateStress = 1f + 0.5f * avgCRate.coerceIn(0f, 5f)

        // Cyclic capacity loss percentage
        val cyclicLoss = k_sei_cyc * tempFactor * dodStress * cRateStress * cycles

        // minimum cyclic aging based on cycles
        val minCyclicAging = cycles * 0.05f // 0.05% per cycle minimum

        val totalCyclicLoss = maxOf(cyclicLoss, minCyclicAging)

        return totalCyclicLoss.coerceIn(0f, 50f)
    }
}