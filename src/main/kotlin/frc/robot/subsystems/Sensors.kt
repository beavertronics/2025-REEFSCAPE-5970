package frc.robot.subsystems

import beaverlib.utils.Sugar.clamp
import beaverlib.utils.Units.Angular.*
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
    var offset : AngleUnit = 0.0.rotations,
    var lowerLimit : AngleUnit = 0.0.radians,
    var upperLimit : AngleUnit = 360.degrees
) : DutyCycleEncoder(channel, 2* PI, 0.0) {
    val rotations : AngleUnit get() =
        ((this.get().radians + (offset/ratio)) * ratio)//.asRadians
            //.clamp(lowerLimit.asRadians, upperLimit.asRadians).radians
    fun reset(newValue : AngleUnit) {
        offset = (offset - rotations) + newValue
    }

        //(this.get().radians * ratio) + offset
}

inline val AngleUnit.standardPosition get() =
    if(this.asRadians < 0.0) { ( AngleUnit((2*PI) + (this.asRadians % (2 * PI)))) }
else { AngleUnit(this.asRadians % (2 * PI)) }
inline val AngleUnit.cook get() = AngleUnit(this.asRadians % (2* PI))

object Sensors : SubsystemBase() {
    // motor to encoder: 30:48
    // encoder to arm: 3:1

    val encoder = BeaverDutyCycle(
        RobotInfo.ArmThroughboreEncoderDIO,
        1.0
    )
    init {

    }
    override fun periodic() {
        SmartDashboard.putNumber("encoder", encoder.rotations.asDegrees)
        if(Climb.climbLimitSwitch.get()) {
            encoder.reset(0.0.radians)
            //encoder.offset -= encoder.rotations
        }

    }
}