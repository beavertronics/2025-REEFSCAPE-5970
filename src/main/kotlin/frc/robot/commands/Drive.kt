package frc.robot.commands

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.DriveConstants
import frc.robot.subsystems.Drivetrain

class Drive(val speed: Double = 0.1, val driveTime : Double = 1.5) // todo use encoders
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