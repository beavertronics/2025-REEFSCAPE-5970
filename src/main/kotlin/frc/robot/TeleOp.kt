package frc.robot

import kotlin.math.*
import beaverlib.utils.Sugar.within
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.SubsystemBase
import edu.wpi.first.wpilibj2.command.button.CommandJoystick
import edu.wpi.first.wpilibj2.command.button.CommandXboxController
import frc.robot.commands.Arm.MoveArm
import frc.robot.commands.Arm.OuttakeCoral
import frc.robot.commands.RunClimb
import frc.robot.commands.swerve.TeleopDriveCommand
import frc.robot.subsystems.ArmConstants
import frc.robot.subsystems.Climb
import frc.robot.subsystems.Drivetrain

/*
Sets up the operator interface (controller inputs), as well as
setting up the commands for running the drivetrain and the subsystems
 */

/**
 * class for managing systems and inputs
 */
object TeleOp {
    val teleOpDrive: TeleopDriveCommand =
        TeleopDriveCommand(
            { OI.rightDrive * 1.0 }, // todo artifically lower right to match left (which is weaker)
            { OI.leftDrive },
            { OI.slowMode },
        )

    init {
        Climb
        Drivetrain.defaultCommand = teleOpDrive // sets what function is called every frame (somewhere?)
    }

    /**
     * configures things to run on specific inputs
     */
    fun configureBindings() {
        OI.spoolClimb.whileTrue(RunClimb())
        OI.ejectCoral.whileTrue(OuttakeCoral(null))
        OI.moveArmForward.whileTrue(MoveArm(ArmConstants.FrontLimitSwitchAngle))
        OI.moveArmBackward.whileTrue(MoveArm(ArmConstants.BackLimitSwitchAngle))
    }

    /**
     * Class for the operator interface
     * getting inputs from controllers and whatnot.
     */
    object OI : SubsystemBase() {
        val leftDriveController = CommandJoystick(0) // todo change to xbox controller
        val rightDriveController = CommandJoystick(1) // todo change to xbox controller
        private val operatorController = CommandXboxController(2)

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

        /**
         * Allows the inputted controller to rumble
         */
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
        val leftDrive get() = leftDriveController.y.processInput() * -1
        val rightDrive get() = rightDriveController.y.processInput() * -1
        val slowMode get() = rightDriveController.trigger().asBoolean
        //===== SUBSYSTEMS =====//
        val spoolClimb get() = operatorController.b()
        val ejectCoral get() = operatorController.x()
        val moveArmForward get() = operatorController.y()
        val moveArmBackward get() = operatorController.a()
    }
}






































































































// uwu