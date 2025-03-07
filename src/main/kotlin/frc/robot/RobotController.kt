package frc.robot

//import com.pathplanner.lib.auto.AutoBuilder
import edu.wpi.first.cameraserver.CameraServer
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import frc.robot.commands.Arm.JankArm
import frc.robot.commands.Arm.MoveArm
import frc.robot.commands.Arm.OuttakeCoral
import frc.robot.commands.Autos.driveForward
import frc.robot.subsystems.ArmConstants
//import frc.robot.subsystems.Lights

/*
 Main code for controlling the robot. Mainly just links everything together.

 Driver control is defined in TeleOp.kt.

 The hardware of the robot (what motor controllers, etc) is defined in "robot info.kt"
*/

/**
 * main object for controlling robot, based off
 * of the timed robot class
 */
object RobotController : TimedRobot() {
    // general things
    val commandScheduler = CommandScheduler.getInstance()
    // anything relating to autos
    val manualAutoCommands: Map<String,Command> = mapOf(
        /**
         * Drives the robot forwards and deposits the preloaded coral
         */
        Pair(
            "deposit preload",
            SequentialCommandGroup(
                ParallelCommandGroup(
                    driveForward(speed = 0.25, driveTime = 5.5), // todo
                    JankArm(-3.0), // reset arm to back / outtake of robot
                ),
                OuttakeCoral(3.0),
                JankArm(3.0) // move arm to front of robot
            )
        )
    )
//    val autoChooser = AutoBuilder.buildAutoChooser();
    var selectedManualAuto: Command? = null
    val ManualAutoChooser = SendableChooser<Command>()

    /**
     * runs when robot turns on, should be used for any initialization of robot or subsystems
     */
    override fun robotInit() {
//        Lights
        TeleOp
        CameraServer.startAutomaticCapture()
//        SmartDashboard.putData("Auto Chooser", autoChooser);

        ManualAutoChooser.setDefaultOption("No Auto", Commands.none());
        ManualAutoChooser.addOption("Operation bear minimum", driveForward(speed = 0.25, driveTime = 5.5))
        ManualAutoChooser.addOption("deposit preload", manualAutoCommands["deposit preload"])
        SmartDashboard.putData("Auto choices", ManualAutoChooser);

    }

    /**
     * runs when the robot is on, regardless of enabled or not
     * used for telemetry, command scheduler, etc
     */
    override fun robotPeriodic() { commandScheduler.run() }

    override fun autonomousInit() {
        selectedManualAuto = ManualAutoChooser.selected
        selectedManualAuto?.schedule()
        println("Auto selected: " + selectedManualAuto)
    }
    override fun autonomousPeriodic() {} //TODO: Unnecesary with command-based programming?

    /**
     * runs when teleop is ready
     */
    override fun teleopInit() {
        TeleOp.configureBindings()
        if (selectedManualAuto != null) { selectedManualAuto?.cancel() }
    }

    /**
     * runs on every frame of teleop
     */
    override fun teleopPeriodic() {} //TODO: Unnecessary with command-based programming?

    /**
     * runs only in simulation mode,
     * other functions will run regardless of whether the robot is
     * simulated or not
     */
    override fun simulationInit() {}

    /**
     * runs immediately when the robot is disabled, helpful for safe
     * deactivation of robot and whatnot
     */
    override fun disabledInit() {}

    /**
     * runs while robot is disabled, used to hold motors
     * in place.
     * try not to put code here, is often unsafe
     */
    override fun disabledPeriodic() {}

    override fun testInit() { commandScheduler.cancelAll() }

    override fun testPeriodic() {}
}