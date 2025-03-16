// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package frc.robot.commands

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.Drivetrain
import java.util.function.BooleanSupplier
import java.util.function.DoubleSupplier

///////////////////////////////////////////////////
/*
Responsible for running the drivetrain ONLY
 */
///////////////////////////////////////////////////

/**
 * A command that controls the swerve drive using controller inputs.
 * @param velocityLeftSupplier The percentage to drive the left side of the robot at.
 * @param velocityRightSupplier The percentage to drive the irght side of the robot at.
 * @param slowModeSupplier Boolean supplier that returns true if the robot should drive in slow mode.
 * @see Drivetrain
 */
class ChildModeTeleOpDriveCommand(
    private val childLeftVelocitySupplier : DoubleSupplier,
    private val childRightVelocitySupplier : DoubleSupplier,
    private val adultLeftVelocitySupplier : DoubleSupplier,
    private val adultRightVelocitySupplier : DoubleSupplier,
    private val toggleChildModeSupplier : BooleanSupplier,
    private val maxChildModeSpeed : Double = 3.0,
    private val maxAdultModeSpeed : Double = 9.0
) : Command() {

    init { addRequirements(Drivetrain) }

    /** @suppress */
    override fun execute() {
        var leftVelocity = 0.0
        var rightVelocity = 0.0
        if (toggleChildModeSupplier.asBoolean) {
            leftVelocity = childLeftVelocitySupplier.asDouble * maxChildModeSpeed
            rightVelocity = childRightVelocitySupplier.asDouble * maxChildModeSpeed
        }
        else {
            leftVelocity = adultLeftVelocitySupplier.asDouble * maxAdultModeSpeed
            rightVelocity = adultRightVelocitySupplier.asDouble * maxAdultModeSpeed
        }

        // Drive using raw values
        Drivetrain.rawDrive(
            leftVelocity,
            rightVelocity
        )
    }

    /** @suppress */
    override fun end(interrupted: Boolean) {}

    /** @suppress */
    override fun isFinished(): Boolean { return false }
}