package frc.robot

import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import frc.robot.subsystems.Drivetrain
import frc.robot.subsystems.Lights

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
    //val autos: Map<String,Command> = mapOf(
        //TODO: Autos go here!
        //ie
        //"Description of auto" to TaxiAuto
    //)
    val commandScheduler = CommandScheduler.getInstance()

    init {
        Drivetrain.defaultCommand = TeleOp.teleOpDrive
    }
    /**
     * runs when robot turns on, should be used for any initialization of robot
     */
    override fun robotInit() {
        Lights.init()
    }

    /**
     * runs when the robot is on, regardless of enabled or not
     * used for telemetry, command scheduler, etc
     */
    override fun robotPeriodic() {
        commandScheduler.run()
    }

    override fun autonomousInit() {}
    override fun autonomousPeriodic() {} //TODO: Unnecesary with command-based programming?

    /**
     * runs when teleop is ready
     */
    override fun teleopInit() {}

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

    override fun testInit() {
        commandScheduler.cancelAll()
    }
    override fun testPeriodic() {}
}