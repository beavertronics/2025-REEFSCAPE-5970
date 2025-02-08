package frc.robot.subsystems

import com.revrobotics.RelativeEncoder
import com.revrobotics.spark.SparkBase
import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import com.revrobotics.spark.config.SparkMaxConfig
import edu.wpi.first.math.controller.ArmFeedforward
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.struct.DifferentialDriveWheelVoltagesStruct
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.units.Units.*
import edu.wpi.first.units.measure.Voltage
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.RobotController
import edu.wpi.first.wpilibj.sysid.SysIdRoutineLog
import edu.wpi.first.wpilibj2.command.SubsystemBase
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine
import frc.robot.RobotInfo
import frc.robot.commands.Arm.ArmTherapy

object ArmConstants {
    // trapezoidal profile things (assume m/s)
    const val maxVelocity = 1.0 // todo
    const val maxAcceleration = 1.0 // todo
    // pid things
    const val KP = 0.0
    const val KI = 0.0
    const val KD = 0.0
    // arm feed forward things?
    const val KS = 0.0 // sin
    const val KG = 0.0 // minimum voltage to move (K static)
    const val KV = 0.0 // to multiply to maintain velocity
    const val KA = 0.0 // to multiply desired acceleration
    // limit switch things
    const val FrontLimitSwitchAngle = 0.0 // todo (degrees or radians?)
    const val BackLimitSwitchAngle = 0.0 // todo (degrees or radians?)
    // other things
    const val chainBackslash = 0.0 // todo, is the amount of slack in the chain

    // enums for position states
    enum class PositionState(val direction : Int, val encoderValue : Double) {
        kFrontPosition(1, FrontLimitSwitchAngle),
        kBackPosition(-1, BackLimitSwitchAngle)
    }
}

object Arm : SubsystemBase() {
    val armMotor = SparkMax(RobotInfo.ArmMotorID, SparkLowLevel.MotorType.kBrushless)
    val encoder : RelativeEncoder = armMotor.encoder
    val pid : PIDController = PIDController(ArmConstants.KP, ArmConstants.KV, ArmConstants.KD)
    val frontLimitSwitch = DigitalInput(RobotInfo.ArmStartLimitSwitchDIO) // intake position
    val backLimitSwitch = DigitalInput(RobotInfo.ArmEndLimitSwitchDIO) // deposit position
    var goal = TrapezoidProfile.State(encoder.position, 0.0)

    init {
        // do custom config instead of using initMotorControllers from Beaverlib
        // to add closed loop PID
        val config = SparkMaxConfig()
        config.idleMode(SparkBaseConfig.IdleMode.kCoast)
        config.smartCurrentLimit(RobotInfo.ArmAmpLimit)
        /*config.closedLoop.pid(
            ArmConstants.KP,
            ArmConstants.KI,
            ArmConstants.KD
        )*/

        // Don't persist parameters since it takes time and this change is temporary
        armMotor.configure(config, SparkBase.ResetMode.kNoResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters)
        defaultCommand = ArmTherapy()
        //armMotor.setPositionConversionFactor todo
    }
    fun resetEncoder(limitSwitchHit : ArmConstants.PositionState){
        encoder.position = limitSwitchHit.encoderValue
    }


    // in a perfect world, how to go from point a to b
    val feedforward = ArmFeedforward(
        ArmConstants.KS,
        ArmConstants.KG,
        ArmConstants.KV,
        ArmConstants.KA
    )
    // imagine a trapezoid on a graph. This helps with speeding up
    // and slowing down to move to where you want to go
    val profile = TrapezoidProfile(TrapezoidProfile.Constraints(ArmConstants.maxVelocity, ArmConstants.maxAcceleration))

    /**
     * Applies PID values to trying moving to the set point
     * @param goalVelocity the velocity you want to be at
     */
    fun applyPIDF(goalVelocity : Double) {
        // finding out how to get to goal
        // finding out where am I and where I want to go
        // starts paying taxes, getting a job, filing for divorce
        // the whole deal
        var voltage = pid.calculate(armMotor.encoder.position) + feedforward.calculate(armMotor.encoder.position, goalVelocity)
        if(frontLimitSwitch.get()) {
            voltage = voltage.coerceAtMost(0.0)
            resetEncoder(ArmConstants.PositionState.kFrontPosition) }
        else if(backLimitSwitch.get()) {
            voltage = voltage.coerceAtLeast(0.0)
            resetEncoder(ArmConstants.PositionState.kBackPosition) }

        armMotor.setVoltage(voltage)
    }
    val voltageDrive : (Voltage) -> Unit = { armMotor.setVoltage(it.`in`(Volts)) }
    val log  : (SysIdRoutineLog) -> Unit = { log : SysIdRoutineLog ->
        // Record a frame for the shooter motor.
        log.motor("arm-motor")
            .voltage(
                m_appliedVoltage.mut_replace(
                    armMotor.get() * RobotController., Volts))
            .angularPosition(m_angle.mut_replace(encoder.position, Rotations))
            .angularVelocity(
                m_velocity.mut_replace(encoder.velocity, RotationsPerSecond));
    },
    // Tell SysId to ma
}