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