// this is line 1
// this is line 2
// this is line 3
// this is line 4
package frc.robot.commands
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj2.command.Command
import kotlin.math.*

import beaverlib.utils.Sugar.within

import frc.robot.subsystems.Drivetrain


////////////////////////////////////////////////////////////////////////////////
// TeleOp Code - Controls the robot based off of inputs from the humans operating the Driver Station.

object TeleOp : Command() {

    /**
     * Makes sure that everything intializes together,
     * and that there isn't a time gap between things being called.
     */
    override fun initialize() {
        addRequirements(Drivetrain)
    }

    /**
     * The main executing loops for driving
     * the robot and whatnot.
     * Executed very frame
     */
    override fun execute() {

        //===== DRIVETRAIN =====//

    }

    /**
     * Class for the operator interface
     * getting inputs from controllers and whatnot.
     */
    object OI {
        private val leftJoystick = Joystick(0) //These numbers correspond to the USB order in the Driver Station App
        private val rightJoystick = Joystick(1)
        private val controller = XboxController(2)

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
         * Values for controller inputs go here
         */
        // val something get() = function()
    }
}






































































































// uwu