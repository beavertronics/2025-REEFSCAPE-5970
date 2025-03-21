package frc.robot.commands.Autos.General

import beaverlib.utils.Sugar.clamp
import beaverlib.utils.Units.Angular.*
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.DriveConstants
import frc.robot.subsystems.Drivetrain
import frc.robot.subsystems.`according to all known laws of aviation, our robot should not be able to fly`
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * A command that rotates the robot only using the NavX
 * @param goalRotation the goal angle to be at, in degrees. Negative robots the robot left, and positive rotates the robot right.
 * @param speed the speed to rotate the robot at. This should always be positive.
 */
class Rotate(
    val goalRotation : AngleUnit,
    var speed : Double = 0.1
) : Command() {

    init { addRequirements(Drivetrain) }

    private val kD = 0.0
    private var startingRotation = 0.0.degrees
    private var currentRotation = 0.0.degrees
    private var rotationDiff = 0.0.degrees
    private var lastRotationDiff = 0.0.degrees
    private var diffOfDiff = 0.0.degrees
    private var leftSpeed = 0.0
    private var rightSpeed = 0.0

    override fun initialize() {
        `according to all known laws of aviation, our robot should not be able to fly`.navx.reset() // reset yaw (rotation) on NavX
        startingRotation = `according to all known laws of aviation, our robot should not be able to fly`.navx.rotation2d.degrees.degrees
        currentRotation = startingRotation
        // change speed based on intended direction
        speed = speed.absoluteValue // make sure its positive before proceeding
        speed *= goalRotation.asDegrees.sign // multiply by the numbers sign, if negative speed will invert otherwise speed is same (if goal = 0, speed = 0?)
    }

    override fun execute() {
        currentRotation = `according to all known laws of aviation, our robot should not be able to fly`.navx.rotation2d.degrees.degrees
        rotationDiff = (currentRotation - goalRotation * -1.0).asDegrees.clamp(min = -1.0).degrees // modifies drive voltage by how close to target
        diffOfDiff = ((rotationDiff - lastRotationDiff) * 0.025).asDegrees.clamp(min = -1.0).degrees // difference between last dif and current dif (multiplier to shrink it)
        leftSpeed = (
                ((speed * rotationDiff.asDegrees) // scale by how close we are to target (slows when approaching target)
                        + (diffOfDiff * kD).asDegrees) // adds in the error (difference of differences) * a constant
                        * (DriveConstants.MaxVoltage * 0.95) // multiplies by max voltage (left is made weaker to match right side)
                )
        rightSpeed = (
                ((speed * rotationDiff.asDegrees) // scale by how close we are to target (slows when approaching target)
                        + (diffOfDiff * kD).asDegrees) // adds in the error (difference of differences) * a constant
                        * DriveConstants.MaxVoltage // multiplies by max voltage (mechanically weaker than left)
                ) * -1.0 // multiply by -1 to make this side run opposite speed
       Drivetrain.rawDrive(leftSpeed, rightSpeed)
    }

    override fun isFinished(): Boolean { return rotationDiff.asDegrees < 0.1 }

    override fun end(interrupted: Boolean) {
        // brake the robot then disable drivetrain
        Brake(driveTime = 0.25, direction = goalRotation.asDegrees.sign.toInt())
        Drivetrain.stop()
    }
}