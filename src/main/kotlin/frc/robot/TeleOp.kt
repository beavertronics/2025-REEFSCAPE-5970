package frc.robot

import edu.wpi.first.wpilibj.XboxController
import kotlin.math.*

import beaverlib.utils.Sugar.within
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.Subsystem
import edu.wpi.first.wpilibj2.command.SubsystemBase
import edu.wpi.first.wpilibj2.command.button.CommandJoystick
import edu.wpi.first.wpilibj2.command.button.CommandXboxController
import frc.robot.commands.RunClimb
import frc.robot.commands.swerve.TeleopDriveCommand
import frc.robot.subsystems.Climb

//import frc.robot.subsystems.Drivetrain

/*
Sets up the operator interface (controller inputs), as well as
setting up the commands for running the drivetrain and the subsystems
 */

object TeleOpConstants {
    const val spoolClimbButton = 11
    const val unspoolClimbButton = 12
}

/**
 * class for managing systems and inputs
 */
object TeleOp {

    val teleOpDrive: TeleopDriveCommand =
        TeleopDriveCommand(
            { OI.driveForwards },
            { OI.driveStrafe },
            { OI.rotateRobot },
            { OI.toggleFieldOriented },
            { false },
        )

    init {
        Climb
//        Drivetrain.defaultCommand = teleOpDrive // sets what function is called every frame (somewhere?)
    }

    /**
     * configures things to run on specific inputs
     */
    fun configureBindings() {
        OI.spoolClimb.whileTrue(RunClimb())
        OI.unpsoolClimb.whileTrue(RunClimb())
    }

    /**
     * Class for the operator interface
     * getting inputs from controllers and whatnot.
     */
    object OI : SubsystemBase() {
        val drivingController = CommandXboxController(2) // todo fix port ID
        private val operatorController = CommandJoystick(0) // todo fix port ID

        /**
         * Allows you to tweak controller inputs (ie get rid of deadzone, make input more sensitive by squaring or cubing it, etc).
         */
        private fun Double.processInput(deadzone : Double = 0.1, squared : Boolean = false, cubed : Boolean = false, readjust : Boolean = true) : Double{
            var processed = this
            if(readjust) processed = ((this.absoluteValue - deadzone)/(1 - deadzone))*this.sign
            return when {
                this.within(deadzone) ->    0.0
                squared ->                  processed.pow(2) * this.sign
                cubed   ->                  processed.pow(3)
                else    ->                  processed
            }
        }
        private fun Double.abs_GreaterThan(target: Double): Boolean{
            return this.absoluteValue > target
        }
        class Rumble(val controller : CommandXboxController, val time: Double = 1.0, val rumblePower : Double = 1.0, val rumbleSide : GenericHID.RumbleType = GenericHID.RumbleType.kRightRumble ) : Command() {
            val timer = Timer()
            init { addRequirements(OI) }
            override fun initialize() { timer.restart(); controller.setRumble(rumbleSide, rumblePower) }
            override fun execute() { controller.setRumble(rumbleSide, rumblePower) }

            override fun end(interrupted: Boolean) { controller.setRumble(rumbleSide, 0.0) }

            override fun isFinished(): Boolean { return timer.hasElapsed(time) }
        }

        /**
         * Values for inputs go here
         */
        //===== DRIVETRAIN =====//
        val driveForwards get() = drivingController.leftY.processInput()
        val driveStrafe get() = drivingController.leftX.processInput()
        val rotateRobot get() = drivingController.rightX.processInput()
        val toggleFieldOriented get() = true // todo
        //===== SUBSYSTEMS =====//
        val spoolClimb = drivingController.a()
        val unpsoolClimb = drivingController.y()
    }
}






































































































// uwu