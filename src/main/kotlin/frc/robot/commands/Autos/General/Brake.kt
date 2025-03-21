package frc.robot.commands.Autos.General

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.DriveConstants
import frc.robot.subsystems.Drivetrain
import kotlin.math.sign

/**
 * A command that brakes the robot by driving in the inputted direction.
 * @param speed the speed in which to drive at
 * @param driveTime the duration of time to counter-turn the robot
 * @param direction the direction to rotate the robot, negative is left and positive is right
 */
class Brake(val speed: Double = 0.1, val driveTime : Double = 0.1, val direction : Int = 1)
    : Command() {
    val timer = Timer()

    init { addRequirements(Drivetrain) }

    override fun initialize() { timer.restart() }

    override fun execute() {
        Drivetrain.rawDrive(
            direction * speed * DriveConstants.MaxVoltage,
            (direction * -1) * speed * (DriveConstants.MaxVoltage * 0.95))
    }

    override fun isFinished(): Boolean { return timer.hasElapsed(driveTime) }

    override fun end(interrupted: Boolean) { Drivetrain.stop() }
}