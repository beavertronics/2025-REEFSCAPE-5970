package frc.robot.commands

import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj2.command.Command
import kotlin.math.*

import beaverlib.utils.Sugar.within
import edu.wpi.first.math.geometry.Translation2d
import frc.robot.subsystems.Drivetrain

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
        //===== DRIVETRAIN =====//
        val driveTranslation = Translation2d(
            OI.driveFieldOrientedSideways * 9, // todo maybe dont multiply here???
            OI.driveFieldOrientedForwards * 9 // todo maybe dont multiply here???
        )
        Drivetrain.drive(
            translation = driveTranslation,
            rotation = OI.rotateRobot,
            fieldOriented = OI.toggleFieldOriented
        )
        //===== SUBSYSTEMS =====//
        // todo
    }

    /**
     * Class for the operator interface
     * getting inputs from controllers and whatnot.
     */
    object OI {
        private val drivingController = XboxController(0) // todo fix port ID
        private val operatorController = XboxController(0) // todo fix port ID

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
         * Values for inputs go here
         */
        // todo
        val driveFieldOrientedForwards get() = drivingController.leftY.processInput()
        val driveFieldOrientedSideways get() = drivingController.leftX.processInput()
        val rotateRobot get() = drivingController.rightX.processInput()
        val toggleFieldOriented get() = drivingController.rightBumperButtonPressed
    }
}






































































































// uwu