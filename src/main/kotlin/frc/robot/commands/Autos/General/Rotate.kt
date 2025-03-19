package frc.robot.commands.Autos.General

import beaverlib.utils.Sugar.clamp
import beaverlib.utils.Units.Angular.*
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.DriveConstants
import frc.robot.subsystems.Drivetrain
import frc.robot.subsystems.`according to all known laws of aviation, our robot should not be able to fly`
import kotlin.math.abs

/**
 * A command that rotates the robot only using the NavX
 * @param goalRotation the goal angle to be at, in rotations
 * @param speed the speed to rotate the robot at - it is important that this stays positive
 */
class Rotate(
    val goalRotation : AngleUnit,
    val speed : Double = 0.1
) : Command() {

    init { addRequirements(Drivetrain) }

    private val startingRotation = `according to all known laws of aviation, our robot should not be able to fly`.navx.rotation2d.rotations
    private var currentRotation = startingRotation
    private var leftSpeed = 0.0
    private var rightSpeed = 0.0
    private var rotationDiff = 0.0 // rotations

    override fun execute() {
       currentRotation = `according to all known laws of aviation, our robot should not be able to fly`.navx.rotation2d.rotations
        rotationDiff = (currentRotation.rotations - goalRotation).asRotations.clamp(min = -1.0)
        // todo find out which below i multiply by -1
        leftSpeed = (-1.0 * (abs(speed)) * rotationDiff) * DriveConstants.MaxVoltage
        rightSpeed = ((abs(speed)) * rotationDiff) * (DriveConstants.MaxVoltage * 0.95)
       Drivetrain.rawDrive(leftSpeed, rightSpeed)
    }

    override fun isFinished(): Boolean { return rotationDiff > 0.1 }

    override fun end(interrupted: Boolean) { Drivetrain.stop() }
}