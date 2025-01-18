package frc.robot.subsystems

import Engine.setMotorFollow
import beaverlib.controls.Controller
import com.revrobotics.spark.SparkBase
import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig

import com.revrobotics.RelativeEncoder
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.engine.utils.initMotorControllers
import frc.engine.utils.*
//import frc.robot.subsystems.Odometry.chassisSpeeds


object Drivetrain : SubsystemBase() {
    private val       leftMain = SparkMax(15, SparkLowLevel.MotorType.kBrushed)
    private val  leftSecondary = SparkMax(16,  SparkLowLevel.MotorType.kBrushed)
    private val      rightMain = SparkMax(12, SparkLowLevel.MotorType.kBrushed)
    private val rightSecondary = SparkMax(13,  SparkLowLevel.MotorType.kBrushed)

    val    leftEncoder: RelativeEncoder = leftMain.encoder
    val   rightEncoder: RelativeEncoder = rightMain.encoder

    private val drive = DifferentialDrive(leftMain, rightMain)

    private val leftPid = Controller.PID(0.0, 0.0)
    private val rightPid = Controller.PID(0.0, 0.0)
    private val leftFeedForward = SimpleMotorFeedforward(0.0, 0.0, 0.0)
    private val rightFeedForward = SimpleMotorFeedforward(0.0, 0.0, 0.0)

    private fun allMotors(code: SparkMax.() -> Unit) { //Run a piece of code for each drive motor controller.
        for (motor in listOf(leftMain, rightMain, leftSecondary, rightSecondary)) {
            motor.apply(code)
        }
    }

    init {
        Engine.initMotorControllers(20, SparkBaseConfig.IdleMode.kCoast, true, leftMain)
        Engine.initMotorControllers(20, SparkBaseConfig.IdleMode.kCoast, true, rightMain)
        setMotorFollow(20,SparkBaseConfig.IdleMode.kCoast, true, leftSecondary, leftMain)
        setMotorFollow(20,SparkBaseConfig.IdleMode.kCoast, false, rightSecondary, rightMain)

        drive.setDeadband(0.0)
    }
    /** Drive by setting left and right power (-1 to 1).
     * @param left Power for left motors [-1.0.. 1.0]. Forward is positive.
     * @param right Voltage for right motors [-1.0.. 1.0]. Forward is positive.
     * */
    fun tankDrive(left: Double, right: Double) {
        drive.tankDrive(left, right, false)
    }
    /** Drive by setting left and right voltage (-12v to 12v)
     * @param left Voltage for left motors
     * @param right Voltage for right motors
     * */
    fun rawDrive(left: Double, right: Double) {
        //TODO: Prevent voltages higher than 12v or less than -12v? Or not neccesary?
        leftMain.setVoltage(left)
        rightMain.setVoltage(right)
        drive.feed()
    }

    fun stop(){
        rawDrive(0.0,0.0)
    }
    /** Drive by setting left and right speed, in M/s, using PID and FeedForward to correct for errors.
     * @param left Desired speed for the left motors, in M/s
     * @param right Desired speed for the right motors, in M/s
     */
    fun closedLoopDrive(left: Double, right: Double) {
        leftPid.setpoint = left
        rightPid.setpoint = right

        val lPidCalculated = leftPid.calculate(leftEncoder.velocity)
        val rPidCalculated = rightPid.calculate(rightEncoder.velocity)

        val lFFCalculated = leftFeedForward.calculate(leftPid.setpoint)
        val rFFCalculated = rightFeedForward.calculate(rightPid.setpoint)

        rawDrive(lPidCalculated+lFFCalculated, rPidCalculated + rFFCalculated )
    }



    private fun rawDrive(left: Double) {

    }

}