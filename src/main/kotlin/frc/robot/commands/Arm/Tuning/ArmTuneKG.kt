package frc.robot.commands.Arm.Tuning

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.Arm
import java.util.function.DoubleSupplier

class ArmTuneKG(
    val voltage : DoubleSupplier
)
: Command() {
    init { addRequirements(Arm) }

    override fun execute() { Arm.armMotor.setVoltage(voltage.asDouble) }
}