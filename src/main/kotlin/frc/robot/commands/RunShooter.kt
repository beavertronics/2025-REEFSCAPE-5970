package frc.robot.commands

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.Shooter

class RunShooter : Command() {
    init {
        addRequirements(Shooter)
    }

    override fun execute() {
        Shooter.leftFlywheel.setVoltage(-9.0)
        Shooter.rightFlywheel.setVoltage(-9.0)
    }

    override fun end(interrupted: Boolean) {
        Shooter.leftFlywheel.setVoltage(0.0)
        Shooter.rightFlywheel.setVoltage(0.0)
    }
}