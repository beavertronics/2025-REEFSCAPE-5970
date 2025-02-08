package frc.robot.subsystems

import  com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.engine.utils.initMotorControllers
import frc.robot.RobotInfo

object Climb : SubsystemBase() {
    val climbMotor = SparkMax(RobotInfo.ClimbMotorWinchID, SparkLowLevel.MotorType.kBrushless)
    val climbLimitSwitch = DigitalInput(RobotInfo.ClimbLimitSwitchDIO)
    init {
        initMotorControllers(RobotInfo.ClimbCurrentLimit, SparkBaseConfig.IdleMode.kCoast, climbMotor)
        defaultCommand = run { runClimb(0.0) }.repeatedly().withName("stop climb")
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