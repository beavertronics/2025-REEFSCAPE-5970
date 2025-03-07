package frc.robot.commands.Arm

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.Arm

class JankArm(val voltage: Double = 1.0)
: Command() {

    init { addRequirements(Arm) }

    override fun execute() { Arm.armMotor.setVoltage(voltage) }

    override fun end(interrupted: Boolean) { Arm.armMotor.stopMotor() }

    override fun isFinished(): Boolean {
        if (voltage > 0) { return Arm.frontLimitSwitch.get() }
        else return Arm.backLimitSwitch.get()
    }
}