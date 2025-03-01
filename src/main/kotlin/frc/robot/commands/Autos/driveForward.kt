package frc.robot.commands.Autos

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.DriveConstants
import frc.robot.subsystems.Drivetrain

class driveForward : Command() {
    val timer = Timer()

    init { addRequirements(Drivetrain) }

    override fun initialize() { timer.restart() }

    override fun execute() {
        Drivetrain.rawDrive(0.1 * DriveConstants.MaxVoltage, 0.1 * DriveConstants.MaxVoltage)
    }

    override fun isFinished(): Boolean { return timer.hasElapsed(1.5) } // todo tune time

    override fun end(interrupted: Boolean) { Drivetrain.stop() }
}