package frc.robot.subsystems

import com.revrobotics.spark.ClosedLoopSlot
import com.revrobotics.spark.SparkBase
import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import com.revrobotics.spark.config.SparkMaxConfig
import edu.wpi.first.math.controller.ArmFeedforward
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase
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
    enum class PositionState(val direction : Int) {
        kFrontPosition(1),
        kBackPosition(-1)
    }
}

object Arm : SubsystemBase() {
    val armMotor = SparkMax(RobotInfo.ArmMotorID, SparkLowLevel.MotorType.kBrushless)
    val frontLimitSwitch = DigitalInput(RobotInfo.ArmStartLimitSwitchDIO) // intake position
    val backLimitSwitch = DigitalInput(RobotInfo.ArmEndLimitSwitchDIO) // deposit position

    init {
        // do custom config instead of using initMotorControllers from Beaverlib
        // to add closed loop PID
        val config = SparkMaxConfig()
        config.idleMode(SparkBaseConfig.IdleMode.kCoast)
        config.smartCurrentLimit(RobotInfo.ArmAmpLimit)
        config.closedLoop.pid(
            ArmConstants.KP,
            ArmConstants.KI,
            ArmConstants.KD
        )

        // Don't persist parameters since it takes time and this change is temporary
        armMotor.configure(config, SparkBase.ResetMode.kNoResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters)
        defaultCommand = ArmTherapy()
    }

    // point to go to basically
    var setPoint = 0.0 // todo
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
        armMotor.closedLoopController.setReference(
            setPoint,
            SparkBase.ControlType.kPosition,
            ClosedLoopSlot.kSlot0, //todo fixme no idea what this does
            feedforward.calculate(
                armMotor.encoder.position,
                goalVelocity))
    }
}