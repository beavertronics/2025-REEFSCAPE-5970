package frc.robot.subsystems

import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase

object InfraredSensor : SubsystemBase() {
    val InfraredSensor = DigitalInput(0)
    override fun periodic() {
        SmartDashboard.putBoolean("sensor data", InfraredSensor.get())
    }
}