package frc.robot.subsystems

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import edu.wpi.first.wpilibj.motorcontrol.Spark
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.engine.utils.initMotorControllers

object Intake : SubsystemBase() {

    private val TopMotor = SparkMax(14, SparkLowLevel.MotorType.kBrushed)
    private val bottomMotor = TalonSRX(27)

    init {
        initMotorControllers(10, bottomMotor)
        initMotorControllers(10, SparkBaseConfig.IdleMode.kCoast, false, TopMotor)

        /*TopMotor.setSmartCurrentLimit(C.CurrentLimit)
        TopMotor.restoreFactoryDefaults()
        bottomMotor.configContinuousCurrentLimit(C.CurrentLimit)
        bottomMotor.configFactoryDefault()*/
        bottomMotor.inverted = true
    }

    /** Runs the intake motor at the give voltage
     * @param voltage The voltage to run the motor at. Positive is intake, Negative is outake
     */
    fun runIntake(percent:Double) {
        //bottomMotor.set(ControlMode.PercentOutput, voltage/12)
        TopMotor.set(percent)
        bottomMotor.set(ControlMode.PercentOutput,percent)
    }
}