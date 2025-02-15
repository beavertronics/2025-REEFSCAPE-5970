package frc.robot.subsystems

import beaverlib.controls.Controller
import com.revrobotics.RelativeEncoder
import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.units.Units.*
import edu.wpi.first.units.measure.Voltage
import edu.wpi.first.wpilibj.RobotController
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import edu.wpi.first.wpilibj.sysid.SysIdRoutineLog
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.SubsystemBase
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Mechanism
import frc.robot.RobotInfo


object Drivetrain : SubsystemBase() {
    private val       leftMain = SparkMax(RobotInfo.FrontLeftDrive, SparkLowLevel.MotorType.kBrushed) // todo
    private val  leftSecondary = SparkMax(RobotInfo.BackLeftDrive,  SparkLowLevel.MotorType.kBrushed) // todo
    private val      rightMain = SparkMax(RobotInfo.FrontRightDrive, SparkLowLevel.MotorType.kBrushed) // todo
    private val rightSecondary = SparkMax(RobotInfo.BackRightDrive,  SparkLowLevel.MotorType.kBrushed) // todo

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
        Engine.initMotorControllers(RobotInfo.DriveMotorCurrentLimit, SparkBaseConfig.IdleMode.kCoast, true, leftMain)
        Engine.initMotorControllers(RobotInfo.DriveMotorCurrentLimit, SparkBaseConfig.IdleMode.kCoast, true, rightMain)
        Engine.setMotorFollow(20,SparkBaseConfig.IdleMode.kCoast, true, leftSecondary, leftMain)
        Engine.setMotorFollow(20,SparkBaseConfig.IdleMode.kCoast, false, rightSecondary, rightMain)

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

    private val sysIdRoutine =
        SysIdRoutine( // Empty config defaults to 1 volt/second ramp rate and 7 volt step voltage.
            SysIdRoutine.Config(),
            Mechanism( // Tell SysId how to plumb the driving voltage to the motors.
                { voltage: Voltage? ->
                    leftMain.setVoltage(voltage)
                    rightMain.setVoltage(voltage)
                },  // Tell SysId how to record a frame of data for each motor on the mechanism being
                // characterized.*/
                { log: SysIdRoutineLog ->
                    // Record a frame for the left motors.  Since these share an encoder, we consider
                    // the entire group to be one motor.
                    log.motor("drive-left")
                        .voltage( Volts.mutable(leftMain.get() * RobotController.getBatteryVoltage()) )
                        .linearPosition(Meters.mutable(leftEncoder.position)) //todo should be distance
                        .linearVelocity( MetersPerSecond.mutable(leftEncoder.velocity) ) //todo should be velocity
                    // Record a frame for the right motors.  Since these share an encoder, we consider
                    // the entire group to be one motor.
                    log.motor("drive-right")
                        .voltage(Volts.mutable(rightMain.get() * RobotController.getBatteryVoltage() ) )
                        .linearPosition(Meters.mutable(rightEncoder.position)) //todo should be distance
                        .linearVelocity( MetersPerSecond.mutable(rightEncoder.velocity) ) //todo should be velocity
                },  // Tell SysId to make generated commands require this subsystem, suffix test state in
                // WPILog with this subsystem's name ("drive")
                this
            )
        )

    /**
     * Returns a command that will execute a quasistatic test in the given direction.
     *
     * @param direction The direction (forward or reverse) to run the test in
     */
    fun sysIdQuasistatic(direction: SysIdRoutine.Direction?): Command? {
        return sysIdRoutine.quasistatic(direction)
    }

    /**
     * Returns a command that will execute a dynamic test in the given direction.
     *
     * @param direction The direction (forward or reverse) to run the test in
     */
    fun sysIdDynamic(direction: SysIdRoutine.Direction?): Command? {
        return sysIdRoutine.dynamic(direction)
    }
}