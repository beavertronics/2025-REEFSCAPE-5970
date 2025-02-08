package frc.robot.subsystems

import beaverlib.utils.Units.Angular.AngleUnit
import beaverlib.utils.Units.Angular.asDegrees
import beaverlib.utils.Units.Angular.rotations
import edu.wpi.first.wpilibj.DutyCycleEncoder
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.robot.RobotInfo
import kotlin.math.PI

/**
 * Returns an angle unit representing the encoders rotation
 */
inline val DutyCycleEncoder.rotations : AngleUnit get() = this.get().rotations

object Sensors : SubsystemBase() {
    // motor to encoder: 30:48
    // encoder to arm: 3:1
    val encoder = DutyCycleEncoder(
        RobotInfo.ArmThroughboreEncoderDIO
    )

    override fun periodic() {
        SmartDashboard.putNumber("encoder", encoder.rotations.asDegrees)
    }
}