package frc.robot.commands.Arm

import beaverlib.utils.Sugar.within
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.Arm
import frc.robot.subsystems.Arm.applyPIDF
import frc.robot.subsystems.Arm.armMotor
import frc.robot.subsystems.Arm.profile
import frc.robot.subsystems.Arm.m_setpoint

/**
 * Moves the arm to the target position
 * @position the position we want to move the arm to
 */
class MoveArm(
    val position : Double)
: Command() {

    val timer = Timer()
    val goal: TrapezoidProfile.State = TrapezoidProfile.State(
        position,
        0.0
    )

    init { addRequirements(Arm) }

    override fun initialize() {
        timer.restart()
        Arm.set_setpoint(position)
    }

    override fun execute() {
        val current: TrapezoidProfile.State = TrapezoidProfile.State(
            armMotor.encoder.position,
            armMotor.encoder.velocity
        )
        val mSetpoint = profile.calculate(timer.get(), current, goal)
        applyPIDF(mSetpoint.velocity)
    }

    override fun isFinished(): Boolean {
        return armMotor.encoder.position.within(1.0, position)
    }
}