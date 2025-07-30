import utils.celsiusToKelvin
import utils.hoursToDays
import utils.yearsToHours
import kotlin.math.exp
import kotlin.math.pow

class CalendarAging{
    private val preExpFactor = 2.15f * 10f.pow(4)
    private val activationEnergy = 36360f
    private val gasConstant = 8.314f
    private val temperature = celsiusToKelvin(25f)
    private val time = yearsToHours(1f)
    private val timeExponent = 0.789f // from A.Krupp table 3.4
    private val storageSOC = 0.5f


    fun calculateSimpleCapacityLoss(){
        val expTerm = exp(x = -activationEnergy / (gasConstant * temperature))
        val time_term = time.pow(timeExponent)

        val qLoss = preExpFactor * expTerm * time_term // Q_LOSS = 11.82% over 1 year

        println("Capacity loss over ${hoursToDays(time)} days is $qLoss %. Not taking SOC into account")
    }

    fun calculateCapacityLoss(){
        val expTerm = exp(x = -activationEnergy / (gasConstant * temperature))
        val time_term = time.pow(timeExponent)

        val qLoss = preExpFactor * expTerm * time_term * storageSOC.pow(5)
        val qLossPercent = qLoss * 100f

        println("Capacity loss over ${hoursToDays(time)} days is $qLossPercent %")
    }

}