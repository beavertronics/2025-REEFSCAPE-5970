package frc.robot.subsystems

import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase

object ClimbConstants {
    const val climbWinchMotorID = 10
    const val climbLimitSwitchID = 0
}
object Climb : SubsystemBase() {
    val climbMotor = SparkMax(ClimbConstants.climbWinchMotorID, SparkLowLevel.MotorType.kBrushless)
    val climbLimitSwitch = DigitalInput(ClimbConstants.climbLimitSwitchID)

    /**
     * runs the motor to spool up the string, retracting climb
     * @param unspool whether to release string instead of pulling it in
     */
    fun spoolClimb(unspool: Boolean = false) {
        var motorSpeed = 12.0
        if (unspool) {motorSpeed * -1.0}
        if (climbLimitSwitch.get()) {motorSpeed = 0.0} // override motor and stop if switch pressed
        climbMotor.set(motorSpeed)
    }

    override fun periodic() {
        if (climbLimitSwitch.get()) {climbMotor.set(0.0)}
    }
}