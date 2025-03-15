// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package frc.robot.commands

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.TeleOp
import frc.robot.subsystems.DriveConstants
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
) : Command() {

    init {
        addRequirements(Drivetrain)
    }

    /** @suppress */
    override fun execute() {
        // initially set drive inputs to child mode
        var leftVelocity = TeleOp.OI.leftJoystick
        var rightVelocity = TeleOp.OI.rightJoystick
        var speedMult = 3
        // if child mode is disabled, use child overwatcher inputs instead
        if (TeleOp.OI.toggleChildMode.asBoolean == false) {
            leftVelocity = TeleOp.OI.controllerDrive - TeleOp.OI.controllerStrafe
            rightVelocity = TeleOp.OI.controllerDrive + TeleOp.OI.controllerStrafe
            speedMult = 9
        }

        // Drive using raw values
        Drivetrain.rawDrive(
            leftVelocity * speedMult,
            rightVelocity * speedMult
        )
    }

    /** @suppress */
    override fun end(interrupted: Boolean) {}

    /** @suppress */
    override fun isFinished(): Boolean { return false }
}