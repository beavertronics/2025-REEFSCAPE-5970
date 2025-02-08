package frc.robot.subsystems

import com.revrobotics.spark.SparkBase
import  com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.engine.utils.initMotorControllers

object ClimbConstants {
    const val climbWinchMotorID = 6
    const val climbLimitSwitchID = 0 // DIO
}
object Climb : SubsystemBase() {
    val climbMotor = SparkMax(ClimbConstants.climbWinchMotorID, SparkLowLevel.MotorType.kBrushless)
    val climbLimitSwitch = DigitalInput(ClimbConstants.climbLimitSwitchID)
    init {
        initMotorControllers(10, SparkBaseConfig.IdleMode.kBrake, climbMotor)
        defaultCommand = run { runClimb(0.0) }.repeatedly().withName("stop")
    }

    /**
     * runs the motor to spool up the string, retracting climb
     * @param speed what percent speed to run [climbMotor] at
     */
    fun runClimb(speed: Double ) {
        if (climbLimitSwitch.get()) { climbMotor.set(0.0); return} // override motor and stop if switch pressed
        climbMotor.set(speed)
    }
}