package frc.robot.commands.Arm

import beaverlib.utils.Sugar.within
import beaverlib.utils.Units.Angular.rotations
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.Arm
import frc.robot.subsystems.Arm.applyPIDF
import frc.robot.subsystems.Arm.armMotor
import frc.robot.subsystems.Arm.profile

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
    var current: TrapezoidProfile.State = TrapezoidProfile.State(
        Arm.encoder.position.rotations.asRadians,
        0.0
    )

    init { addRequirements(Arm) }

    override fun initialize() {
        timer.restart()
        Arm.goal = goal
        Arm.pid.setpoint = goal.position
    }

    override fun execute() {
        val current = profile.calculate(timer.get(), current, goal)
        applyPIDF(current.velocity)
    }

    override fun isFinished(): Boolean {
        return armMotor.encoder.position.within(1.0, position)
    }
}