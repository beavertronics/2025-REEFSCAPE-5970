package frc.robot.commands.Arm

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.Arm
import frc.robot.subsystems.Arm.armMotor
import frc.robot.subsystems.Arm.backLimitSwitch
import frc.robot.subsystems.Arm.frontLimitSwitch
import frc.robot.subsystems.ArmConstants
import frc.robot.subsystems.Climb

/**
 * resets the arms to the choice position
 * @param endGoal whether you want the arm to be at the front or the back of the robot
 * @param speed the value to move the arm during configuring
 */
class ResetArm(
    val endGoal : ArmConstants.PositionState,
    val speed: Double = 2.0
)
: Command() {

    init { addRequirements(Arm) }

    override fun end(interrupted: Boolean) {
        if (frontLimitSwitch.get()) { armMotor.encoder.setPosition(ArmConstants.FrontLimitSwitchAngle) }
        if (backLimitSwitch.get()) { armMotor.encoder.setPosition(ArmConstants.BackLimitSwitchAngle) }
    }

    override fun execute() { armMotor.set(speed * endGoal.direction) }

    override fun isFinished(): Boolean { return frontLimitSwitch.get() || backLimitSwitch.get() }
}
