@file:JvmName("BatteryAgingSimu")

fun main(){
    println(daysToHours(45f))

    println(hoursToDays(72f))
}

fun daysToHours(days: Float): Float{
    return days * 24f
}

fun hoursToDays(hours: Float): Float{
    return hours / 24f
}