package frc.robot

import edu.wpi.first.wpilibj2.command.button.CommandXboxController
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine
import frc.robot.commands.Arm.ArmSysIdDynamic
import frc.robot.commands.Arm.ArmSysIdQuasistatic


class SysIDController {
    // The driver's controller
    var controller = CommandXboxController(2)

    fun configureBindings(){


        controller
            .a()
            .and(controller.leftBumper())
            .whileTrue(ArmSysIdQuasistatic(SysIdRoutine.Direction.kForward));
        controller
            .b()
            .and(controller.leftBumper())
            .whileTrue(ArmSysIdQuasistatic(SysIdRoutine.Direction.kReverse));
        controller
            .x()
            .and(controller.leftBumper())
            .whileTrue(ArmSysIdDynamic(SysIdRoutine.Direction.kForward));
        controller
            .y()
            .and(controller.leftBumper())
            .whileTrue(ArmSysIdDynamic(SysIdRoutine.Direction.kReverse));
    }
}