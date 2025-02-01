package frc.robot.subsystems

import beaverlib.utils.Units.Angular.degrees
import beaverlib.utils.Units.Linear.metersPerSecond
import com.revrobotics.spark.ClosedLoopSlot
import com.revrobotics.spark.SparkBase
import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import com.revrobotics.spark.config.SparkMaxConfig
import edu.wpi.first.math.controller.ArmFeedforward
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.robot.RobotInfo
import kotlin.math.absoluteValue

object Arm : SubsystemBase() {
    private val armMotor = SparkMax(RobotInfo.ArmMotorID, SparkLowLevel.MotorType.kBrushless)

    init {
        val config = SparkMaxConfig()
        config.idleMode(SparkBaseConfig.IdleMode.kCoast)
        config.smartCurrentLimit(RobotInfo.ArmAmpLimit)
        config.closedLoop.pid(
            0.0,
            0.0,
            0.0,
        )

        // Don't persist parameters since it takes time and this change is temporary
        armMotor.configure(config, SparkBase.ResetMode.kNoResetSafeParameters, SparkBase.PersistMode.kNoPersistParameters)
    }

    // point to go to basically
    var setPoint = 0.degrees // todo
    val feedforward = ArmFeedforward(/* sin */ 0.0, /* k static*/ 0.0, 0.0,0.0)
    val profile = TrapezoidProfile(TrapezoidProfile.Constraints(1.0, 1.0))

    fun applyPIDF(time : Double, goalPosition : Double, goal : TrapezoidProfile.State) {
        val current : TrapezoidProfile.State = TrapezoidProfile.State(
            armMotor.encoder.position,
            armMotor.encoder.velocity
        )
        val m_setpoint = profile.calculate(time,current, goal)
        armMotor.closedLoopController.setReference(goalPosition,
            SparkBase.ControlType.kPosition,
            ClosedLoopSlot.kSlot0, //fixme no idea what this does
            feedforward.calculate(
                armMotor.encoder.position,
                m_setpoint.velocity))
    }
    class MoveArm(position : Double) : Command() {
        val timer = Timer()
        val goal : TrapezoidProfile.State = TrapezoidProfile.State(
            position,
            0.0
    }
}