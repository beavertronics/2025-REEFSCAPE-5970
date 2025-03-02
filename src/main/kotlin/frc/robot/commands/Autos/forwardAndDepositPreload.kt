package frc.robot.commands.Autos

import beaverlib.utils.Units.Angular.asDegrees
import beaverlib.utils.Units.Angular.asRotations
import beaverlib.utils.Units.Angular.degrees
import beaverlib.utils.Units.Linear.asInches
import beaverlib.utils.Units.Linear.inches
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import frc.robot.commands.Arm.MoveArm
import frc.robot.commands.Arm.OuttakeCoral
import frc.robot.subsystems.ArmConstants
import frc.robot.subsystems.DriveConstants
import frc.robot.subsystems.Drivetrain
import kotlin.math.PI

class forwardAndDepositPreload
: Command() {
    /*
    - set robot in middle of starting line, vision camera facing reef
    - reset arm to intake position
    - move arm to outtake position
    - plan 1:
        - using vision, drive forwards until close enough to reef
        - move arm to outtake position
        - outtake coral
        - move arm to intake position
    - plan 2:
        - using right drive encoder, go 7ft 4in forwards
            - (7*12) + 4 = 88 inches to reef
            - 6 * PI = distance per wheel revolution
                - distance / 88 inches is 4.6685 wheel / encoder revolutions to travel said distance
        - move arm to outtake position
        - outtake coral
        - move arm to intake position
     */

    val bumperDistanceFromCenter = 0.0.inches // todo
    val distancePerRevolution = (DriveConstants.WheelDiameter * PI).asInches
    val targetDistance = 88.inches - bumperDistanceFromCenter
    val revolutions = (targetDistance / distancePerRevolution).asInches.degrees.asRotations
    val requiredDegreesRotation = (revolutions * 360).degrees
    var finished = false

    init { addRequirements( Drivetrain ) }

    override fun execute() {
        Drivetrain.rawDrive(0.1 * DriveConstants.MaxVoltage, 0.1 * DriveConstants.MaxVoltage) // todo
        if (Drivetrain.leftEncoder.distance.degrees.asDegrees >= requiredDegreesRotation.asDegrees) {
            Drivetrain.stop()
            SequentialCommandGroup(
                MoveArm(ArmConstants.BackLimitSwitchAngle),
                OuttakeCoral(3.0), // todo
                MoveArm(ArmConstants.FrontLimitSwitchAngle)
            ).schedule()
            finished = true
        }
    }

    override fun isFinished(): Boolean { return finished }

    override fun end(interrupted: Boolean) { Drivetrain.stop() }
}