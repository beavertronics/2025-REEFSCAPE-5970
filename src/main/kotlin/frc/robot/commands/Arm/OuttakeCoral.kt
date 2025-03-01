package frc.robot.commands.Arm

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.Arm

class OuttakeCoral
: Command() {

    init { addRequirements(Arm) }
}