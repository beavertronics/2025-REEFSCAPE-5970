package frc.robot.commands.Arm

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.Arm
import edu.wpi.first.wpilibj.Timer

/**
 * Runs the motor to outtake the coral
 * @param runtime how long to run the outtake motor for, if set to null will run until stopped
 */
class OuttakeCoralNoRequire(
    val runtime: Double?,
    val speed : Double = 1.0
)
: Command() {
    val timer = Timer()
    override fun initialize() { timer.restart() }

    override fun execute() { Arm.outtakeMotor.setVoltage(speed) } // todo change speed

    override fun isFinished(): Boolean {
        if (runtime == null) { return false }
        else { return timer.hasElapsed(runtime) }
    }

    override fun end(interrupted: Boolean) { Arm.outtakeMotor.setVoltage(0.0) }
}