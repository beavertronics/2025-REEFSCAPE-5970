package frc.robot.subsystems

import beaverlib.utils.Units.Angular.AngleUnit
import beaverlib.utils.Units.Angular.asDegrees
import beaverlib.utils.Units.Angular.rotations
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.DutyCycleEncoder
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.robot.RobotInfo
import kotlin.math.PI

/**
 * @param channel [DutyCycleEncoder]
 * @param ratio multiply the rotations of the encoder by this value to get the rotations of the arm
 * @param offset the offset to add to the rotations of the encoder to get the rotations of the arm (Applied after ratio)
 */
class BeaverDutyCycle(
    channel: Int,
    var ratio : Double = 1.0,
    var offset : AngleUnit = 0.0.rotations
) : DutyCycleEncoder(channel) {
    val rotations : AngleUnit get() = (this.get().rotations * ratio) + offset
}

object Sensors : SubsystemBase() {
    // motor to encoder: 30:48
    // encoder to arm: 3:1
    val encoder = BeaverDutyCycle(
        RobotInfo.ArmThroughboreEncoderDIO,
        ratio = (1.0/3.0)
    )
    val climbLimitSwitch = DigitalInput(RobotInfo.ClimbLimitSwitchDIO)

    override fun periodic() {
        SmartDashboard.putNumber("encoder", encoder.rotations.asDegrees)
        if(climbLimitSwitch.get()) {
            encoder.offset -= encoder.rotations
        }

    }
}