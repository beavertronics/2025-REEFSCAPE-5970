package frc.robot.commands
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj2.command.Command
import kotlin.math.*

import beaverlib.utils.Sugar.within
import frc.robot.subsystems.Drivetrain
import frc.robot.subsystems.Intake
import frc.robot.subsystems.Shooter

/*
Controls the robot based off of inputs from the humans operating the driving station.
 */


/**
 * class for managing systems and inputs
 */
object TeleOp : Command() {

    /**
     * Makes sure that everything intializes together,
     * and that there isn't a time gap between things being called.
     */
    override fun initialize() {
        addRequirements(Drivetrain) // todo add systems
    }

    /**
     * The main executing loops for driving
     * the robot and whatnot.
     * Executed very frame
     */
    override fun execute() {
        //===== CHANGING STUFF =====//
        var inverted = 1.0
        if (OI.invertDirection) { inverted = -1.0 }
        //===== DRIVETRAIN =====//
        if (OI.toggleChildMode) {
            Drivetrain.rawDrive(OI.childModeDriveLeft*3, OI.childModeDriveRight*3)
        }
        else {
            Drivetrain.rawDrive(
                (OI.parentModeDrive - OI.parentModeStraife) * 9,
                (OI.parentModeDrive + OI.parentModeStraife) * 9)
        }

        //===== SUBSYSTEMS =====//
        Intake.runIntake(OI.runIntake*(1.0*inverted))
        Shooter.runOpenLoop(OI.runShooter*(1.0*inverted))
    }

    /**
     * Class for the operator interface
     * getting inputs from controllers and whatnot.
     */
    object OI {
        private val operatorController = XboxController(2)
        private val rightDrive = Joystick(0)
        private val leftDrive = Joystick(1)


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

        val childModeDriveLeft get() = leftDrive.y.processInput()
        val childModeDriveRight get() = rightDrive.y.processInput()
        val parentModeDrive get() = operatorController.leftY.processInput()
        val parentModeStraife get() = operatorController.leftX.processInput()
        val runIntake get() = operatorController.leftTriggerAxis
        val runShooter get() = operatorController.rightTriggerAxis * -1.0 // invert direction
        val invertDirection get() = operatorController.rightBumperButton
        val toggleChildMode get() = operatorController.leftBumperButton


        /**
         * Values for inputs go here
         */
        // todo
//        val driveFieldOrientedForwards get() = drivingController.leftY.processInput()
//        val driveFieldOrientedSideways get() = drivingController.leftX.processInput()
//        val rotateRobot get() = drivingController.rightX.processInput()
//        val toggleFieldOriented get() = drivingController.rightTriggerAxis
    }
}






































































































// uwu