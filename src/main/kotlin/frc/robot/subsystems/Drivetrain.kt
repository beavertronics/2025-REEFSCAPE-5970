package frc.robot.subsystems

import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj.Filesystem
import edu.wpi.first.wpilibj2.command.SubsystemBase
import swervelib.SwerveDrive
import swervelib.parser.SwerveParser
import swervelib.telemetry.SwerveDriveTelemetry
import swervelib.telemetry.SwerveDriveTelemetry.*
import java.io.File

/**
 * class for all constants for drivetrain
 */
object DriveConstants {
    // for YAGSL to find swerve directory
    val DriveConfig = File(Filesystem.getDeployDirectory(), "swerve")
    val MaxSpeed = 10.0 // in m/s
}

/**
 * the main class for the drivetrain, containing everything
 */
object Drivetrain : SubsystemBase() {

    // create anything that is set later (late init)
    var swerveDrive: SwerveDrive

    // variables created now
    private var fieldOriented: Boolean = true

    // anything to get values
    val isFieldOriented get() = fieldOriented

    /**
     * init file that runs on intialization of drivetrain class
     */
    init {
        // set up swerve drive :D
        SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH
        swerveDrive = SwerveParser(DriveConstants.DriveConfig).createSwerveDrive(DriveConstants.MaxSpeed)
    }

    fun makeChassisSpeed(xSpeedMPS: Double, ySpeedMPS: Double, rotationOmegaRPS: Double): ChassisSpeeds {
        return ChassisSpeeds(xSpeedMPS, ySpeedMPS, rotationOmegaRPS)
    }

    /**
     * sets field oriented to inputted state
     */
    fun setFieldOriented(state: Boolean) { fieldOriented = state }

    /**
     * drives the robot field oriented.
     * this means that no matter the robots rotation, driving "forwards" on the joysticks
     * makes the robot go forwards on the field
     */
    fun driveFieldOriented(speed: ChassisSpeeds) { swerveDrive.driveFieldOriented(speed) }

    /**
     * drives the robot regardless of field orientation.
     * this means that driving forwards just makes the robot go in the direction
     * of the front of the robot
     */
    fun drive(speed: ChassisSpeeds) { swerveDrive.drive(speed) }
}