package frc.robot.commands.Arm

import edu.wpi.first.units.Units.Volts
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine
import frc.robot.subsystems.Arm

val ArmRoutine = SysIdRoutine(
    SysIdRoutine.Config(
        null,
        Volts.of(1.0),
        null,
    ),
    SysIdRoutine.Mechanism(Arm.voltageDrive, Arm.logMotors, Arm)
)
fun ArmSysIdQuasistatic(direction: SysIdRoutine.Direction?): Command? {
    return ArmRoutine.quasistatic(direction)
}

fun ArmSysIdDynamic(direction: SysIdRoutine.Direction?): Command? {
    return ArmRoutine.dynamic(direction)
}
