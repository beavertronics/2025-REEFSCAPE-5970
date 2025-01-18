package frc.robot.subsystems

import com.revrobotics.RelativeEncoder
import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.units.*
import edu.wpi.first.wpilibj.RobotController
import edu.wpi.first.wpilibj.sysid.SysIdRoutineLog
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.engine.utils.initMotorControllers

object Shooter : SubsystemBase() {
    private val leftFlywheel = SparkMax(25, SparkLowLevel.MotorType.kBrushless)
    private val rightFlywheel = SparkMax(26, SparkLowLevel.MotorType.kBrushless)

    private val    leftEncoder: RelativeEncoder = leftFlywheel.encoder
    private val   rightEncoder: RelativeEncoder = rightFlywheel.encoder



    /* 
    private var rawShooterSpeed = 0.0;
    enum class ShooterMode {
        CLOSED_LOOP, OPEN_LOOP, STOP
    }
    var shooterMode = ShooterMode.OPEN_LOOP
    */

    init {
        initMotorControllers(20,SparkBaseConfig.IdleMode.kCoast, false, leftFlywheel)
        initMotorControllers(20,SparkBaseConfig.IdleMode.kCoast, true, rightFlywheel)
    }

    fun runOpenLoop(rawShooterSpeed : Double){
        leftFlywheel.set(rawShooterSpeed)
        rightFlywheel.set(rawShooterSpeed)
    }
}