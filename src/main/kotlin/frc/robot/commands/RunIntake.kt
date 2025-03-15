package frc.robot.commands

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.Intake

class RunIntake(
    val percent : Double = 0.1
) : Command() {

    init {
        addRequirements(Intake)
    }

    override fun execute() {
        Intake.runIntake(percent)
    }

    override fun end(interrupted: Boolean) {
        Intake.runIntake(0.0)
    }
}