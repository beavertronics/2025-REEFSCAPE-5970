package frc.robot.subsystems

import  com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase

object ClimbConstants {
    const val climbWinchMotorID = 6
    const val climbLimitSwitchID = 0 // DIO
}
object Climb : SubsystemBase() {
    val climbMotor = SparkMax(ClimbConstants.climbWinchMotorID, SparkLowLevel.MotorType.kBrushless)
    val climbLimitSwitch = DigitalInput(ClimbConstants.climbLimitSwitchID)

    /**
     * runs the motor to spool up the string, retracting climb
     * @param unspool whether to release string instead of pulling it in
     */
    fun spoolClimb(unspool: Boolean = false) {
        var motorSpeed = -0.1
        if (unspool) {motorSpeed *= -1.0}
        if (climbLimitSwitch.get()) {motorSpeed = 0.0} // override motor and stop if switch pressed
        climbMotor.set(motorSpeed)
    }

    /**
     * runs every frame, makes sure that if at any point the
     * limit switch is pressed, the motor can not spin
     */
    override fun periodic() {
        if (climbLimitSwitch.get()) {climbMotor.set(0.0)}
    }
}