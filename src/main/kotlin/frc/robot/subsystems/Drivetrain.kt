package frc.robot.subsystems

import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.wpilibj.Filesystem
import edu.wpi.first.wpilibj2.command.SubsystemBase
import swervelib.SwerveDrive
import swervelib.parser.SwerveParser
import swervelib.telemetry.SwerveDriveTelemetry
import swervelib.telemetry.SwerveDriveTelemetry.TelemetryVerbosity
import java.io.File

/**
 * class for all constants for drivetrain
 */
object DriveConstants {
    // for YAGSL to find swerve directory
    val DriveConfig = File(Filesystem.getDeployDirectory(), "swerve")
    val MaxSpeed = 10.0
}

/**
 * the main class for the drivetrain, containing everything
 */
class Drivetrain : SubsystemBase() {

    // initially create variables
    lateinit var swerveDrive: SwerveDrive

    /**
     * init file that runs on intialization of drivetrain class
     */
    init {
        // set up swerve drive :D
        SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH
        swerveDrive = SwerveParser(DriveConstants.DriveConfig).createSwerveDrive(DriveConstants.MaxSpeed)
    }
}