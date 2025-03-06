package frc.robot.subsystems

import Engine.BeaverRelativeEncoder
import beaverlib.utils.Units.Angular.degrees
import beaverlib.utils.Units.Angular.radians
import com.revrobotics.spark.SparkBase
import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import com.revrobotics.spark.config.SparkMaxConfig
import edu.wpi.first.math.controller.ArmFeedforward
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.units.Units.*
import edu.wpi.first.units.measure.Voltage
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.RobotController
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj.sysid.SysIdRoutineLog
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.robot.commands.Arm.Tuning.ArmTune
import kotlin.math.cos

object ArmConstants {
    // motor IDs and whatnot
    const val ArmMotorID = 8
    const val outtakeMotorID = 14
    const val ArmAmpLimit = 20
    const val ArmStartLimitSwitchDIO = 3 // front
    const val ArmEndLimitSwitchDIO = 2 // back
    const val ArmBeamBreakDIO = 5
    // trapezoidal profile things (assume m/s)
    const val maxVelocity = 1.0 // todo
    const val maxAcceleration = 1.0 // todo
    // pid things
    var KP = 0.0 // todo
    var KI = 0.0 // todo
    const val KD = 0.0
    // arm feed forward things?
    var KS = 0.5 // minimum voltage to move (K static) // todo
    var KG = 0.05 // minimum voltage to stay in place // todo
    const val KV = 0.0 // to multiply to maintain velocity
    const val KA = 0.0 // to multiply desired acceleration
    // limit switch things
    val FrontLimitSwitchAngle = 0.0.degrees // todo
    val BackLimitSwitchAngle = 0.0.degrees // todo
    // other things
    const val chainBackslash = 0.0 // todo, is the amount of slack in the chain

    // enums for position states
    enum class PositionState(val direction : Int, val encoderValue : Double) {
        kFrontPosition(1, FrontLimitSwitchAngle.asRadians),
        kBackPosition(-1, BackLimitSwitchAngle.asRadians)
    }
}

object Arm : SubsystemBase() {
    val armMotor = SparkMax(ArmConstants.ArmMotorID, SparkLowLevel.MotorType.kBrushless)
    val outtakeMotor = SparkMax(ArmConstants.outtakeMotorID, SparkLowLevel.MotorType.kBrushed)
    val encoderRatio : Double = ((1.0/40.0) * (42.0 / 30.0) * (48.0 / 18.0))
    //val encoder : BeaverDutyCycleEncoder = BeaverDutyCycleEncoder(ArmConstants.ArmEncoderDIO, (1.0/3.0) ) // todo set armOffset
    val encoder : BeaverRelativeEncoder = BeaverRelativeEncoder(armMotor.encoder, positionConversionFactor = encoderRatio)
    val pid : PIDController = PIDController(ArmConstants.KP, ArmConstants.KV, ArmConstants.KD)
    val frontLimitSwitch = DigitalInput(ArmConstants.ArmStartLimitSwitchDIO) // intake position
    val backLimitSwitch = DigitalInput(ArmConstants.ArmEndLimitSwitchDIO) // deposit position
    val IntakeBeamBreak = DigitalInput(ArmConstants.ArmBeamBreakDIO)
    var goal = TrapezoidProfile.State(encoder.position.asRadians, 0.0)

    init {

        // dashboard tuning things
        SmartDashboard.putNumber("KP", ArmConstants.KP)
        SmartDashboard.putNumber("KI", ArmConstants.KI)
        SmartDashboard.putNumber("KG", ArmConstants.KG)
        SmartDashboard.putNumber("KS", ArmConstants.KS)

        // do custom config instead of using initMotorControllers from Beaverlib
        // to add closed loop PID
        val config = SparkMaxConfig()
        config.idleMode(SparkBaseConfig.IdleMode.kCoast)
        config.smartCurrentLimit(ArmConstants.ArmAmpLimit)
        /*config.closedLoop.pid(
            ArmConstants.KP,
            ArmConstants.KI,
            ArmConstants.KD
        )*/

        // Don't persist parameters since it takes time and this change is temporary
        armMotor.configure(config, SparkBase.ResetMode.kNoResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters)
//        defaultCommand = ArmTherapy()
        defaultCommand = ArmTune( {ArmConstants.KG * cos(encoder.position.asRadians) + ArmConstants.KS} ) // todo for tuning
    }

    override fun periodic() {
        if(backLimitSwitch.get()) {
            encoder.resetPosition(ArmConstants.BackLimitSwitchAngle)
        }
        if(frontLimitSwitch.get()) {
            encoder.resetPosition(ArmConstants.FrontLimitSwitchAngle)
        }
        SmartDashboard.putBoolean("Coral in?", IntakeBeamBreak.get())


        ArmConstants.KP = SmartDashboard.getNumber("KP", 0.0)
        ArmConstants.KI = SmartDashboard.getNumber("KI", 0.0)
        ArmConstants.KG = SmartDashboard.getNumber("KG", 0.0)
        ArmConstants.KS = SmartDashboard.getNumber("KS", 0.0)
        pid.p = ArmConstants.KP
        pid.i = ArmConstants.KI
        feedforward.kg = ArmConstants.KG
        feedforward.ks = ArmConstants.KS
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
        var voltage = pid.calculate(encoder.position.asRadians) + feedforward.calculate(encoder.position.asRadians, goalVelocity)
        if(frontLimitSwitch.get()) {
            voltage = voltage.coerceAtMost(0.0)
            }
        else if(backLimitSwitch.get()) {
            voltage = voltage.coerceAtLeast(0.0)
        }

        armMotor.setVoltage(voltage)
    }
    val voltageDrive : (Voltage) -> Unit = { armMotor.setVoltage(it.`in`(Volts)) }
    val logMotors  : (SysIdRoutineLog) -> Unit = { log : SysIdRoutineLog ->
        // Record a frame for the shooter motor.
        log.motor("arm-motor")
            .voltage(Volts.mutable(0.0).mut_replace(armMotor.get() * RobotController.getBatteryVoltage(), Volts))
            .angularPosition(Radians.mutable(0.0).mut_replace(encoder.position.asRadians, Radians))
            .angularVelocity(RadiansPerSecond.mutable(0.0).mut_replace(encoder.velocity.asRadiansPerSecond, RadiansPerSecond));
    }
}