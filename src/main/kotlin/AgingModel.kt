
import utils.celsiusToKelvin
import utils.hoursToDays
import kotlin.math.exp
import kotlin.math.pow

class AgingModel(private val params: AgingParameters = AgingParameters()) {

    fun calculateCalendarAging(timeHours: Float, tempCelsius: Float, avgSoC: Float): Float {
        val tempKelvin = celsiusToKelvin(tempCelsius)
        val timeDays = hoursToDays(timeHours)

        val tempTerm = exp(-params.activationEnergyCalendar / (params.gasConstant * tempKelvin))
        val socTerm = 1.19e-4f * avgSoC + 0.01f
        val timeTerm = timeDays.pow(params.timeExponent)

        return params.preExpFactorCalendar * tempTerm * socTerm * timeTerm
    }

    fun calculateCyclicAging(efc: Float, tempCelsius: Float, avgSoC: Float, doD: Float): Float {
        val tempKelvin = celsiusToKelvin(tempCelsius)
        val tempTerm = exp(-params.activationEnergyCyclic / (params.gasConstant * tempKelvin))
        val stressTerm = calculateStressAmplitude(avgSoC, doD)
        val efcTerm = efc.pow(params.efcExponent)

        return params.cyclicFactor * stressTerm * tempTerm * efcTerm
    }

    // Equation 4.7
    private fun calculateStressAmplitude(avgSoC: Float, doD: Float): Float {
        val socMin = avgSoC - doD/2f
        val socMax = avgSoC + doD/2f
        return calculatePolynomial(socMax) - calculatePolynomial(socMin)
    }

    // from Table 4.2
    private fun calculatePolynomial(soc: Float): Float {
        val coeffs = floatArrayOf(2.74e-13f, -8.39e-11f, 8.38e-9f, -2.39e-7f, -5.05e-6f, 9.70e-5f, 0.02f, -6.19e-3f)
        var result = 0f
        for (i in coeffs.indices) {
            result += coeffs[i] * soc.pow(7 - i)
        }
        return maxOf(0f, result)
    }
}