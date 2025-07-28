package enhanced

import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class PowerDemand {

    // power demand based on driving conditions
    fun calculatePowerDemand(
        speed: Float, // m/s
        acceleration: Float, // m/s²
        gradient: Float, // fraction
        mass: Float // kg
    ): Float {
        val g = 9.81f
        val rho = 1.225f // air density
        val Cd = 0.7f // drag coefficient (bus)
        val A = 8f // frontal area m²
        val Crr = 0.01f // rolling resistance

        // Forces
        val F_drag = 0.5f * rho * Cd * A * speed.pow(2)
        val F_roll = Crr * mass * g * cos(atan(gradient))
        val F_grade = mass * g * sin(atan(gradient))
        val F_accel = mass * acceleration

        val F_total = F_drag + F_roll + F_grade + F_accel
        val power = F_total * speed / 1000f // kW

        return power
    }

    fun calculateRegenPower(
        speed: Float,
        deceleration: Float,
        mass: Float,
        efficiency: Float = 0.7f
    ): Float {
        val kinetic = 0.5f * mass * speed.pow(2)
        val powerRegen = kinetic * deceleration / speed / 1000f * efficiency
        return minOf(powerRegen, 150f)
    }
}