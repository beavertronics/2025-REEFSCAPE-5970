//package frc.robot.subsystems
import beaverlib.utils.Units.Angular.degrees
import beaverlib.utils.Units.Linear.inches
import com.studica.frc.AHRS
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.kinematics.SwerveDriveOdometry
import edu.wpi.first.math.kinematics.SwerveModulePosition
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.robot.subsystems.IGNORE_SwerveModule.SwerveModule

/**
 * constants for drivetrain
 */
object DriveConstants {
    const val MotorLFrontID = 0 // todo
    const val MotorLBackID = 0 // todo
    const val MotorRFrontID = 0 // todo
    const val MotorRBackID = 0 // todo
    const val CurrentLimit = 40 // todo
}

/**
 * main drivetrain class for swerve
 */
class Drivetrain : SubsystemBase() {

    // all attributes
    var kinematics: SwerveDriveKinematics
    var odometry: SwerveDriveOdometry
    var swerveModules: Array<SwerveModule>
    lateinit var gyro: AHRS // Nav X gyroscope

    /**
     * runs on initialization of Drivetrain class
     */
    init {
        // create the swerve modules
        // todo set all motor IDs
        swerveModules = arrayOf(
            SwerveModule(0, 0, 0), // front left
            SwerveModule(0, 0, 0), // front right
            SwerveModule(0, 0, 0), // back left
            SwerveModule(0, 0, 0), // back right
        )

        // Create the kinematics object
        kinematics = SwerveDriveKinematics(
            // todo change these distance values to distance of center of swerve wheel
            // todo from middle of robot
            Translation2d(12.5.inches.asMeters, 12.5.inches.asMeters),  // front left
            Translation2d(12.5.inches.asMeters, -12.5.inches.asMeters),  // front right
            Translation2d(-12.5.inches.asMeters, 12.5.inches.asMeters),  // back left
            Translation2d(-12.5.inches.asMeters, -12.5.inches.asMeters) // back right
        )

        // Create the SwerveDrive Odometry given the current angle, the robot is at x=0, r=0, and heading=0
        odometry = SwerveDriveOdometry(
            kinematics,
            gyro.rotation2d, // todo idek if this works
            arrayOf(
                SwerveModulePosition(),
                SwerveModulePosition(),
                SwerveModulePosition(),
                SwerveModulePosition()
            ),
            // Front-Left, Front-Right, Back-Left, Back-Right
            Pose2d(0.0, 0.0, Rotation2d())
        )
    }

    /**
     * main driving function
     */
    fun drive() {
        // Create test ChassisSpeeds going X = 14in, Y=4in, and spins at 30deg per second.
        val testSpeeds: ChassisSpeeds =
            ChassisSpeeds(14.inches.asMeters, 4.inches.asMeters, 30.degrees.asRadians) // todo

        // Get the SwerveModuleStates for each module given the desired speeds.
        val swerveModuleStates = kinematics.toSwerveModuleStates(testSpeeds)

        // Output order is Front-Left, Front-Right, Back-Left, Back-Right
        swerveModules[0].setState(swerveModuleStates[0]) // todo
        swerveModules[1].setState(swerveModuleStates[1]) // todo
        swerveModules[2].setState(swerveModuleStates[2]) // todo
        swerveModules[3].setState(swerveModuleStates[3]) // todo
    }

    /**
     * gets current position of all swerve modules
     */
    // todo
    fun getCurrentSwerveModulePositions(): Array<SwerveModulePosition> {
        return arrayOf<SwerveModulePosition>(
            SwerveModulePosition(swerveModules[0].getDistance(), swerveModules[0].getAngle()),  // front left
            SwerveModulePosition(swerveModules[1].getDistance(), swerveModules[1].getAngle()),  // front right
            SwerveModulePosition(swerveModules[2].getDistance(), swerveModules[2].getAngle()),  // back left
            SwerveModulePosition(swerveModules[3].getDistance(), swerveModules[3].getAngle()) // back right
        )
    }

    /**
     * runs every frame
     */
    override fun periodic() {
        // Update the odometry
        odometry.update(gyro.rotation2d, getCurrentSwerveModulePositions())
    }
}