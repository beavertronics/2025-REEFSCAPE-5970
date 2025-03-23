package frc.robot

import beaverlib.utils.Units.Angular.asRotations
import beaverlib.utils.Units.Angular.degrees
import beaverlib.utils.Units.Angular.rotations
import beaverlib.utils.Units.Linear.inches
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
import frc.robot.commands.OuttakeCoral
import frc.robot.commands.Autos.General.Drive
import frc.robot.commands.Autos.General.Rotate
import frc.robot.commands.Autos.ReefAdjacentDepositPreload
import frc.robot.subsystems.Drivetrain

//import frc.robot.subsystems.Phatplanner

//import frc.robot.subsystems.Lights

/*
 Main code for controlling the robot. Mainly just links everything together.

 Driver control is defined in TeleOp.kt.
*/

/**
 * main object for controlling robot, based off
 * of the timed robot class
 */
object RobotController : TimedRobot() {
    val commandScheduler = CommandScheduler.getInstance()
    // true if running manual autos when enabled
    // false if running pathplanner autos when enabled
    var manualAutos = true
    // NOTE: robot is 3ft^2
    // NOTE: reef is 52 inches from starting line
    val manualAutoCommands: Map<String,Command> = mapOf(
        /**
         * Drives the robot forwards and deposits the preloaded coral
         */
        Pair(
            "deposit preload",
            SequentialCommandGroup(
                ParallelCommandGroup(
                    Drive(speed = -0.25, driveTime = Drivetrain.calcDistanceTime(60.0.inches)),
                    JankArm(-3.0), // reset arm to back / outtake of robot
                ),
                OuttakeCoral(3.0, -3.5),
                JankArm(3.0) // move arm to front of robot
            )
        ),
        Pair(
            "drive backwards",
            SequentialCommandGroup(Drive(speed = -0.25, Drivetrain.calcDistanceTime(52.0.inches)))
        ),
        Pair(
            "test - calibrate time calculator",
            SequentialCommandGroup(
                Drive(speed = -0.25, driveTime = 2.0)
            )
        ),
        Pair(
            "test - drive distance",
            SequentialCommandGroup(
                Drive(speed = -0.25, driveTime = Drivetrain.calcDistanceTime(52.0.inches)) // drives distance to reef
            )
        ),
        Pair(
            "test - turn right",
            SequentialCommandGroup(Rotate(60.0.degrees, speed = 0.25))
        ),
        Pair(
            "test - turn left",
            SequentialCommandGroup(Rotate(300.0.degrees, 0.25))
        )
    )
    var selectedManualAuto: Command? = null
    val ManualAutoChooser = SendableChooser<Command>()
    var selectedPathAuto: Command? = null
    val ReefAdjacentSideChooser = SendableChooser<Double>()

    /**
     * runs when robot turns on, should be used for any initialization of robot or subsystems
     */
    override fun robotInit() {
//        Lights
        TeleOp
        CameraServer.startAutomaticCapture(0) // todo 0 or 1? no drive cam :c
        // load manual autos
        ManualAutoChooser.setDefaultOption("no auto", Commands.none())
        ManualAutoChooser.addOption("drive forwards", manualAutoCommands["drive backwards"])
        ManualAutoChooser.addOption("deposit preload", manualAutoCommands["deposit preload"])
        ManualAutoChooser.addOption("reef adjacent deposit preload", ReefAdjacentDepositPreload())
        ManualAutoChooser.addOption("calibrate time finder", manualAutoCommands["calibrate time finder"])
        ManualAutoChooser.addOption("test distance", manualAutoCommands["test distance"])
        ManualAutoChooser.addOption("test - turn left", manualAutoCommands["test - turn left"])
        ManualAutoChooser.addOption("test - turn right", manualAutoCommands["test - turn right"])
        SmartDashboard.putData("Manual auto choices", ManualAutoChooser)
        // create side chooser for reef adjacent
        ReefAdjacentSideChooser.setDefaultOption("Side - none", 0.0)
        ReefAdjacentSideChooser.addOption("Side - alliance cage", 5.0)
        ReefAdjacentSideChooser.addOption("Side - opponent cage", 1.0)
        SmartDashboard.putData("Field side", ReefAdjacentSideChooser)
        // put data for reef adjacent preload auto onto dashboard
        SmartDashboard.putNumber("distance from wall (inches)", 0.0)
        // load pathplanner autos
//        Phatplanner.autoChooser.setDefaultOption("no auto", Commands.none())
//        Phatplanner.autoChooser.addOption("3 piece center auto (backwards)", PathPlannerAuto("comp - 3 coral auto"))
//        SmartDashboard.putData("Pathplanner auto choices", Phatplanner.autoChooser)

    }

    /**
     * runs when the robot is on, regardless of enabled or not
     * used for telemetry, command scheduler, etc
     */
    override fun robotPeriodic() { commandScheduler.run() }

    override fun autonomousInit() {
        if (manualAutos) {
            println("Using manual auto")
            selectedManualAuto = ManualAutoChooser.selected
            selectedManualAuto?.schedule()
            println("Auto selected: " + selectedManualAuto)
        }
        else {
            println("using pathplanner auto")
//            selectedPathAuto = Phatplanner.getAutonomousCommand()
            selectedPathAuto?.schedule()
            println("Auto selected: " + selectedPathAuto)
        }
    }
    override fun autonomousPeriodic() {} //TODO: Unnecesary with command-based programming?

    /**
     * runs when teleop is ready
     */
    override fun teleopInit() {
        TeleOp.configureBindings()
        if (manualAutos && selectedManualAuto != null) { selectedManualAuto?.cancel() }
        else if (!manualAutos && selectedPathAuto != null) { selectedPathAuto?.cancel() }
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