```kotlin
# Main.kt

@file:JvmName("BatteryAgingSimu")

import utils.celsiusToKelvin
import utils.hoursToDays
import utils.yearsToHours
import kotlin.math.exp
import kotlin.math.pow


fun main(){
    val agingModel = CalendarAging()
    agingModel.calculateSimpleCapacityLoss()
}

class CalendarAging{
    // params
    private val preExpFactor = 2.15f * 10f.pow(4)
    private val activationEnergy = 36360f                           // J/mol
    private val gasConstant = 8.314f                                 // J/mol
    private val temperature = celsiusToKelvin(25f)
    private val time = yearsToHours(1f)
    private val timeExponent = 0.789f                               // from A.Krupp table 3.4
    private val storageSOC = 0.5f


    fun calculateSimpleCapacityLoss(){
        val expTerm = exp(x = -activationEnergy / (gasConstant * temperature))
        val time_term = time.pow(timeExponent)

        val qLoss = preExpFactor * expTerm * time_term // Q_LOSS = 11.82% over 1 year

        println("Capacity loss over ${hoursToDays(time)} days is $qLoss %. Not taking SOC into account")
    }

}
```

```kotlin
# Utils.kt
package utils


fun daysToHours(days: Float): Float{
    return days * 24f
}

fun hoursToDays(hours: Float): Float{
    return hours / 24f
}

fun yearsToHours(years: Float): Float{
    val hoursInOneYear = 24f * 365f
    val hours = years * hoursInOneYear
    return hours
}

fun weeksToHours(weeks: Float): Float{
    val hoursInOneWeek = 24f * 7f
    val hours = weeks * hoursInOneWeek
    return hours
}

fun celsiusToKelvin(celsius: Float): Float{
    return celsius + 273.15f
}

fun kelvinToCelsius(kelvin: Float): Float{
    return kelvin - 273.15f
}


```
