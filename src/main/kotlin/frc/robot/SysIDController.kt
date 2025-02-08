package frc.robot

import edu.wpi.first.wpilibj2.command.button.CommandXboxController
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine
import frc.robot.commands.Arm.ArmSysIdDynamic
import frc.robot.commands.Arm.ArmSysIdQuasistatic


class SysIDController {
    // The driver's controller
    var m_driverController = CommandXboxController(2)
    fun configureBindings(){


        m_driverController
            .a()
            .and(m_driverController.leftBumper())
            .whileTrue(ArmSysIdQuasistatic(SysIdRoutine.Direction.kForward));
        m_driverController
            .b()
            .and(m_driverController.leftBumper())
            .whileTrue(ArmSysIdQuasistatic(SysIdRoutine.Direction.kReverse));
        m_driverController
            .x()
            .and(m_driverController.leftBumper())
            .whileTrue(ArmSysIdDynamic(SysIdRoutine.Direction.kForward));
        m_driverController
            .y()
            .and(m_driverController.leftBumper())
            .whileTrue(ArmSysIdDynamic(SysIdRoutine.Direction.kReverse));
    }
}