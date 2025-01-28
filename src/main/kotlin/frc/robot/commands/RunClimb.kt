package frc.robot.commands

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.Climb

///////////////////////////////////////////////////
/*
Responsible for running the climb ONLY
 */
///////////////////////////////////////////////////

class RunClimb(
    val unspool: Boolean = false
) : Command() {

    // each subsystem adds itself as a requirement
    init { addRequirements(Climb) }

    /** @suppress */
    override fun execute() { Climb.spoolClimb(unspool) }
}