package frc.robot.commands

import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.TeleOp
import frc.robot.subsystems.Climb

///////////////////////////////////////////////////
/*
Responsible for running the climb ONLY
 */
///////////////////////////////////////////////////

class RampClimb(
    val startSpeed: Double = 0.1,
    val endSpeed : Double = 0.1,
    val holdTime: Double = 0.25,
    val rampTime : Double = 2.0

) : Command() {
    val timer = Timer()
    // each subsystem adds itself as a requirement
    init { addRequirements(Climb) }

    override fun initialize() {
        timer.restart()
    }

    /** @suppress */
    override fun execute() {
        println(timer.get())
        if(timer.hasElapsed(holdTime)) {
            TeleOp.OI.Rumble(TeleOp.OI.drivingController, 0.1, 0.5).schedule()
            var speed = startSpeed
            speed += (endSpeed - startSpeed) * ((timer.get() - holdTime) / (rampTime)).coerceAtMost(1.0)
            Climb.runClimb(speed)
        }
    }

    override fun end(interrupted: Boolean) {
        Climb.runClimb(0.0)
    }

    override fun isFinished(): Boolean {
    return false //fixme
    // return Climb.climbLimitSwitch.get()
    }
}