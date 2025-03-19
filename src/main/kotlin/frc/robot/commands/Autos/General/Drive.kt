package frc.robot.commands.Autos.General

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.DriveConstants
import frc.robot.subsystems.Drivetrain

/**
 * A command that drives the robot at a speed for an amount of time, without using encoders.
 * @param speed the speed in which to drive at
 * @param driveTime the duration of how long to drive forwards
 */
class Drive(val speed: Double = 0.1, val driveTime : Double = 2.0) // todo use encoders
    : Command() {
    val timer = Timer()

    init { addRequirements(Drivetrain) }

    override fun initialize() { timer.restart() }

    override fun execute() {
        Drivetrain.rawDrive(speed * DriveConstants.MaxVoltage, speed * DriveConstants.MaxVoltage * 0.95)
    }

    override fun isFinished(): Boolean { return timer.hasElapsed(driveTime) }

    override fun end(interrupted: Boolean) { Drivetrain.stop() }
}