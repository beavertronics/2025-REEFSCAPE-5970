package frc.robot.commands.Arm

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine
import frc.robot.subsystems.Arm

val ArmSysID = SysIdRoutine(
    SysIdRoutine.Config(),
    SysIdRoutine.Mechanism(Arm.voltageDrive,)
)