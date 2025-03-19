package frc.robot.commands.Autos

import beaverlib.utils.Sugar.clamp
import beaverlib.utils.Units.Angular.asRotations
import beaverlib.utils.Units.Angular.degrees
import beaverlib.utils.Units.Angular.rotations
import beaverlib.utils.Units.Linear.asInches
import beaverlib.utils.Units.Linear.feet
import beaverlib.utils.Units.Linear.inches
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import frc.robot.commands.Arm.JankArm
import frc.robot.commands.Autos.General.Drive
import frc.robot.commands.Autos.General.Rotate
import frc.robot.commands.OuttakeCoral
import frc.robot.subsystems.Drivetrain

/**
 * A command that finds out how far to drive to align with a diagonal line cast by the angled faces
 * of the reef - once we align with this line, we turn to face it then drive into the reef
 */
class ReefAdjacentDepositPreload
: Command() {
    val reefDistance = 52.0.inches
    val fieldWidth = 24.0.feet // guessed based off fact that reef is 12 feet from wall (center? edge?) // todo
    val reefWidth = 5.0.feet // todo
    val minWallSpacing = 3.0.feet // the default distance we should be away from wall // todo
    // divide field length into 2, then subtract the robots width from it
    // then add in the safety margin on the wall-side and the reef-side (so we don't clip the reef corner)
    val upperDistanceLimit = ((fieldWidth / 2) - 3.0.feet) - (minWallSpacing * 2.0)
    var finished = false
    var reefRight = false
    var wallDistance = 0.0.inches
    var distanceMultiplier = 0.0

    override fun initialize() {
        // load distance from wall from dashboard
        wallDistance = SmartDashboard.getNumber("distance from wall (inches)", 0.0).inches
        reefRight = SmartDashboard.getBoolean("right of reef (drivers left)", false)
        wallDistance -= minWallSpacing.asInches.inches // adjust wall distance for 3 foot margin
        wallDistance = wallDistance.asInches.clamp(max = upperDistanceLimit.asInches).inches // put wall distance within accepted limits
        distanceMultiplier = wallDistance.asInches.clamp() // how far to drive to get to reef (0 = no distance, 1 = full distance) // todo set lower limit to minimum needed
    }

    override fun execute() {
        println("driving forwards and setting arm to outtake")
        ParallelCommandGroup(
            JankArm(-3.0),
            Drive(-0.25, Drivetrain.calcDistanceTime(reefDistance * distanceMultiplier))
        ).schedule()
        if (reefRight) {
            println("rotating robot left 60 degrees")
            SequentialCommandGroup(Rotate(-60.0.degrees.asRotations.rotations, 0.25)).schedule()
        }
        else {
            println("rotating robot right 60 degrees")
            SequentialCommandGroup(Rotate(60.0.degrees.asRotations.rotations, 0.25)).schedule()
        }
        println("driving towards reef and outtaking coral")
        SequentialCommandGroup(
            Drive(-0.25, Drivetrain.calcDistanceTime((fieldWidth / 2) - reefWidth)),
            OuttakeCoral(3.0, -3.5)
        ).schedule()
        finished = true
    }

    override fun isFinished(): Boolean { return finished }
}