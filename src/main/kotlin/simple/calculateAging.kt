package simple

import kotlin.math.pow

fun calculateAging(
    temp: Float,
    soc: Float,
    dailyCycles: Float,
    dod: Float
): Quadruple<Float, Float, Float, Float> {
    // Calendar aging (storage degradation)
    val k1 = 2.0f
    val tempFactorCal = temp / 25f
    val socFactor = 1 + ((soc - 50) / 50).pow(2)
    val calendarLoss = k1 * tempFactorCal * socFactor

    // Cyclic aging (constant usage degradation)
    val k2 = 0.01f
    val yearlyCycles = dailyCycles * 300 // operating days
    val tempFactorCyc = temp / 25f
    val dodFactor = (dod / 100).pow(1.5f)
    val cyclicLoss = k2 * yearlyCycles * dodFactor * tempFactorCyc

    val totalLoss = calendarLoss + cyclicLoss
    val remainingCapacity = (100 - totalLoss).coerceAtLeast(0f)

    val capacityLossTo80 = 20f // Need to lose 20% to reach 80%
    val lifetime = if (totalLoss > 0) capacityLossTo80 / totalLoss else Float.POSITIVE_INFINITY

    return Quadruple(calendarLoss, cyclicLoss, remainingCapacity, lifetime)
}

data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)