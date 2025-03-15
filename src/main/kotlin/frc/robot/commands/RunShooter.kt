package frc.robot.commands

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.Shooter

class RunShooter(
    val rawVoltage : Double = 1.0
) : Command() {

    init { addRequirements(Shooter) }

    override fun execute() {
        Shooter.leftFlywheel.setVoltage(rawVoltage)
        Shooter.rightFlywheel.setVoltage(rawVoltage)
    }

    override fun end(interrupted: Boolean) {
        Shooter.leftFlywheel.setVoltage(0.0)
        Shooter.rightFlywheel.setVoltage(0.0)
    }
}