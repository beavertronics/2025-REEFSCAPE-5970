package frc.robot.commands.Arm

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.Arm
import frc.robot.subsystems.Arm.applyPIDF

/**
 * keeps the arm at the setpoint after arriving
 */
class ArmTherapy()
: Command() {

    init { addRequirements(Arm) }
    override fun execute() { applyPIDF(0.0) }
    override fun isFinished(): Boolean { return false }
}