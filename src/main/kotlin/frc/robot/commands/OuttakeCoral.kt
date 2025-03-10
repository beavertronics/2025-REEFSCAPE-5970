package frc.robot.commands

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj.Timer
import frc.robot.subsystems.Outtake

/**
 * Runs the motor to outtake the coral
 * @param runtime how long to run the outtake motor for, if set to null will run until stopped
 */
class OuttakeCoral(
    val runtime: Double?,
    val speed : Double = 1.0
)
: Command() {
    val timer = Timer()

    init { addRequirements(Outtake) }

    override fun initialize() { timer.restart() }

    override fun execute() { Outtake.outtakeMotor.setVoltage(speed) } // todo change speed

    override fun isFinished(): Boolean {
        if (runtime == null) { return false }
        else { return timer.hasElapsed(runtime) }
    }

    override fun end(interrupted: Boolean) { Outtake.outtakeMotor.setVoltage(0.0) }
}