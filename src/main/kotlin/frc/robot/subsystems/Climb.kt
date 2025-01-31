package frc.robot.subsystems

import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.engine.utils.initMotorControllers
import frc.robot.RobotInfo

object Climb : SubsystemBase() {
    private val climbMotor = SparkMax(RobotInfo.ClimbMotorWinchID, SparkLowLevel.MotorType.kBrushless)
    private val climbLimitSwitch = DigitalInput(RobotInfo.ClimbLimitSwitchDIO)

    init {
        initMotorControllers(RobotInfo.ClimbAmpLimit, SparkBaseConfig.IdleMode.kCoast, climbMotor)
    }

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

    /**
     * runs every frame, makes sure that if at any point the
     * limit switch is pressed, the motor can not spin
     */
    override fun periodic() {
        if (climbLimitSwitch.get()) {climbMotor.set(0.0)}
    }
}