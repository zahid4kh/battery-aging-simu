@file:JvmName("BatteryAgingSimu")

fun main(){
    val agingModel = CalendarAging()
    agingModel.calculateSimpleCapacityLoss()
}