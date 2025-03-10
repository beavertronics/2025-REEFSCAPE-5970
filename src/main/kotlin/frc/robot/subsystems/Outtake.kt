package frc.robot.subsystems

import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.engine.utils.initMotorControllers

object OuttakeConstants {
    val OuttakeMotorID = 14
    val OuttakeCurrentLimit = 10 // todo
}
object Outtake : SubsystemBase() {
    val outtakeMotor = SparkMax(OuttakeConstants.OuttakeMotorID, SparkLowLevel.MotorType.kBrushed)

    init { initMotorControllers(OuttakeConstants.OuttakeCurrentLimit, SparkBaseConfig.IdleMode.kCoast, outtakeMotor) }
}