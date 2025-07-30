import utils.celsiusToKelvin
import utils.hoursToDays
import utils.yearsToHours
import kotlin.math.exp
import kotlin.math.pow

class CalendarAging{
    private val preExpFactor = 2f * 10f.pow(-5)
    private val activationEnergy = 36360f
    private val gasConstant = 8.314f
    private val temperature = celsiusToKelvin(25f)
    private val time = yearsToHours(1f)
    private val timeExponent = 0.789f // from A.Krupp table 3.4
    private val storageSOC = 0.5f

    private val expTerm = exp(-activationEnergy / (gasConstant * temperature))
    private val timeTerm = time.pow(timeExponent)

    private fun calculatePreExponentialFactor(): Float{
        // assuming Q_LOSS is ~3% for A(50%, 25°C)
        val qLoss = 0.03f // 3%
        val expFactor = qLoss / timeTerm
        println("Pre Exp.Factor for A(50%_SOC, 25°C) is ~$expFactor")
        return expFactor
    }

    fun calculateCapacityLoss(){
        val qLoss = calculatePreExponentialFactor() * timeTerm
        val qLossPercent = qLoss * 100f

        println("Q_LOSS: $qLoss")
        println("Capacity loss over ${hoursToDays(time)} days is $qLossPercent %")
    }

    fun calculateSimpleCapacityLoss(){
        val expTerm = exp(x = -activationEnergy / (gasConstant * temperature))
        val time_term = time.pow(timeExponent)

        val qLoss = preExpFactor * expTerm * time_term // Q_LOSS = 11.82% over 1 year

        println("Capacity loss over ${hoursToDays(time)} days is $qLoss %. Not taking SOC into account")
    }

}