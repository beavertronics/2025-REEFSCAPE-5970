package frc.robot.subsystems

import beaverlib.utils.Units.Angular.*
import beaverlib.utils.Units.Linear.Acceleration
import beaverlib.utils.Units.Linear.VelocityUnit
import beaverlib.utils.Units.Linear.metersPerSecond
import beaverlib.utils.Units.Linear.metersPerSecondSquared
import com.pathplanner.lib.auto.AutoBuilder
import com.pathplanner.lib.auto.NamedCommands
import com.pathplanner.lib.commands.PathPlannerAuto
import com.pathplanner.lib.config.PIDConstants
import com.pathplanner.lib.config.RobotConfig
import com.pathplanner.lib.controllers.PPHolonomicDriveController
import com.pathplanner.lib.path.GoalEndState
import com.pathplanner.lib.path.PathConstraints
import com.pathplanner.lib.path.PathPlannerPath
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.commands.Arm.MoveArm
import frc.robot.commands.Arm.ReefLevel
import frc.robot.commands.Manipulator.DumpL1
import frc.robot.commands.NoAuto
import java.nio.file.Path
import kotlin.math.PI

object PathPlanner {
    // All other subsystem initialization
    // ...

    // Load the RobotConfig from the GUI settings. You should probably
    // store this in your Constants file
    lateinit var config : RobotConfig;
    init {
        try{
            config = RobotConfig.fromGUISettings();
        } catch (e: Exception) {
            // Handle exception as needed
            e.printStackTrace();
        }
        // Configure AutoBuilder last
        AutoBuilder.configure(
            {Drivetrain.swerveDrive.pose}, // Robot pose supplier
            {pose : Pose2d -> Drivetrain.swerveDrive.resetOdometry(pose)}, // Method to reset odometry (will be called if your auto has a starting pose)
            {Drivetrain.swerveDrive.robotVelocity}, // ChassisSpeeds supplier. MUST BE ROBOT RELATIVE
            {speeds : ChassisSpeeds -> Drivetrain.drive(speeds)}, // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds. Also optionally outputs individual module feedforwards
            PPHolonomicDriveController( // PPHolonomicController is the built in path following controller for holonomic drive trains
                PIDConstants(5.0, 0.0, 0.0), // Translation PID constants
                PIDConstants(5.0, 0.0, 0.0) // Rotation PID constants
            ),
            config, // The robot configuration
            { getInvert() },
            Drivetrain // Reference to this subsystem to set requirements
        );
    }
    val autoChooser: SendableChooser<Command> = AutoBuilder.buildAutoChooser("noAuto")

    fun getInvert() : Boolean{
        val alliance = DriverStation.getAlliance();
        if (alliance.isPresent) {
            return alliance.get() == DriverStation.Alliance.Red;
        }
        return false
    }

    fun generatePath(vararg pose2dWaypoints: Pose2d,
                     endRotation : AngleUnit = (-90).degrees,
                     endVelocity: VelocityUnit = 0.0.metersPerSecond,
                     maxVelocity : VelocityUnit = 3.0.metersPerSecond,
                     maxAcceleration : Acceleration = 3.0.metersPerSecondSquared,
                     maxAngularVelocity : AngularVelocity = (2* PI).radiansPerSecond,
                     maxAngularAcceleration: AngularAcceleration = (4* PI).radiansPerSecondSquared ) : PathPlannerPath {
        // Create a list of waypoints from poses. Each pose represents one waypoint.
        // The rotation component of the pose should be the direction of travel. Do not use holonomic rotation.
        val waypoints = PathPlannerPath.waypointsFromPoses( pose2dWaypoints.asList() )

        val constraints = PathConstraints(maxVelocity.asMetersPerSecond, maxAcceleration.asMetersPerSecondSquared, maxAngularVelocity.asRadiansPerSecond, maxAngularAcceleration.asRadiansPerSecondSquared) // The constraints for this path.
        // PathConstraints constraints = PathConstraints.unlimitedConstraints(12.0); // You can also use unlimited constraints, only limited by motor torque and nominal battery voltage

        // Create the path using the waypoints created above
        val path = PathPlannerPath(
            waypoints,
            constraints,
            null,  // The ideal starting state, this is only relevant for pre-planned paths, so can be null for on-the-fly paths.
            GoalEndState(
                endVelocity.asMetersPerSecond,
                Rotation2d(endRotation.asRadians)
            ) // Goal end state. You can set a holonomic rotation here. If using a differential drivetrain, the rotation will have no effect.
        )
        // Prevent the path from being flipped if the coordinates are already correct
        path.preventFlipping = true
        return path
    }
    fun getAutonomousCommand(autoName : String): Command {
        // This method loads the auto when it is called, however, it is recommended
        // to first load your paths/autos when code starts, then return the
        // pre-loaded auto/path
        return PathPlannerAuto(autoName)
    }

    /**
     * Adds auto chooser to
     */
    fun initPathplanner(){
        NamedCommands.registerCommand("ArmToL1", MoveArm(ReefLevel.L1));
        NamedCommands.registerCommand("DumpL1", DumpL1());

        SmartDashboard.putData("Auto Chooser", autoChooser);
    }

    /**
     * Returns the selected auto command, null if none is selected
     */
    fun getAutonomousCommand(): Command? {
        return autoChooser.selected
    }


}