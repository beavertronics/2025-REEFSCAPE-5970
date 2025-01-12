package frc.robot.subsystems
import com.ctre.phoenix6.configs.CANcoderConfiguration
import com.ctre.phoenix6.configs.CANcoderConfigurator
import com.ctre.phoenix6.configs.MagnetSensorConfigs
import com.ctre.phoenix6.hardware.CANcoder
import com.ctre.phoenix6.signals.SensorDirectionValue
import com.revrobotics.AbsoluteEncoder
import com.revrobotics.spark.SparkBase.ControlType
import com.revrobotics.spark.SparkClosedLoopController
import com.revrobotics.spark.SparkLowLevel
import com.revrobotics.spark.SparkMax
import com.revrobotics.spark.config.SparkBaseConfig
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.SwerveModuleState
import frc.engine.utils.initMotorControllers


/**
 * Represnts a full swerve module class
 */
class IGNORE_SwerveModule {
    class SwerveModule(driveMotorCANID: Int, steerMotorCANID: Int, cancoderCANID: Int) {
        // set up motors and encoders
        private val driveMotor: SparkMax
        private val steerMotor: SparkMax
        private val absoluteEncoder: CANcoder

        // set up PID controllers
        private val drivingPIDController: SparkClosedLoopController
        private val turningPIDController: SparkClosedLoopController

        // set up encoders
        private val driveEncoder: AbsoluteEncoder
        private val steerEncoder: AbsoluteEncoder

        /**
         * runs on initialization of the
         * Swerve Module class
         */
        init {
            // set up drive motor, steer motor, encoder
            driveMotor = SparkMax(driveMotorCANID, SparkLowLevel.MotorType.kBrushless)
            steerMotor = SparkMax(steerMotorCANID, SparkLowLevel.MotorType.kBrushless)
            absoluteEncoder = CANcoder(cancoderCANID)

            // set PID controllers
            drivingPIDController = driveMotor.closedLoopController
            turningPIDController = steerMotor.closedLoopController

            // set encoders
            driveEncoder = driveMotor.absoluteEncoder
            steerEncoder = steerMotor.absoluteEncoder

            // sets up motor controllers / current limits
            // sets to factory defaults automatically
            initMotorControllers(10, SparkBaseConfig.IdleMode.kCoast, steerMotor)
            initMotorControllers(40, SparkBaseConfig.IdleMode.kCoast, driveMotor)

            // Reset everything to factory default
            absoluteEncoder.configurator.apply(CANcoderConfiguration())

            ////////////
            // TODO BASICALLY FIX HERE DOWN OH NO
            ////////////

            // CANcoder Configuration
            val cfg: CANcoderConfigurator = absoluteEncoder.getConfigurator()
            cfg.apply(CANcoderConfiguration())
            val magnetSensorConfiguration = MagnetSensorConfigs()
            cfg.refresh(magnetSensorConfiguration)
            cfg.apply(
                magnetSensorConfiguration
                    .withAbsoluteSensorRange(AbsoluteSensorRangeValue.Unsigned_0To1) // todo
                    .withSensorDirection(SensorDirectionValue.CounterClockwise_Positive)
            )

            // Steering Motor Configuration
            steerMotor.inverted = false
            turningPIDController.setFeedbackDevice(steerEncoder);
            // Apply position and velocity conversion factors for the turning encoder. We
            // want these in radians and radians per second to use with WPILib's swerve
            // APIs.
            steerEncoder.setPositionConversionFactor(ModuleConstants.kTurningEncoderPositionFactor);
            steerEncoder.setVelocityConversionFactor(ModuleConstants.kTurningEncoderVelocityFactor);
            // Enable PID wrap around for the turning motor. This will allow the PID
            // controller to go through 0 to get to the setpoint i.e. going from 350 degrees
            // to 10 degrees will go through 0 rather than the other direction which is a
            // longer route.
            turningPIDController.setPositionPIDWrappingEnabled(true);
            turningPIDController.setPositionPIDWrappingMinInput(0);
            turningPIDController.setPositionPIDWrappingMaxInput(90);
            // Set the PID gains for the turning motor. Note these are example gains, and you
            // may need to tune them for your own robot!
            turningPIDController.setP(ModuleConstants.kTurningP);
            turningPIDController.setI(ModuleConstants.kTurningI);
            turningPIDController.setD(ModuleConstants.kTurningD);
            turningPIDController.setFF(ModuleConstants.kTurningFF);

            // Drive Motor Configuration
            driveMotor.inverted = false
            drivingPIDController.setFeedbackDevice(driveEncoder);
            // Apply position and velocity conversion factors for the driving encoder. The
            // native units for position and velocity are rotations and RPM, respectively,
            // but we want meters and meters per second to use with WPILib's swerve APIs.
            driveEncoder.setPositionConversionFactor(ModuleConstants.kDrivingEncoderPositionFactor);
            driveEncoder.setVelocityConversionFactor(ModuleConstants.kDrivingEncoderVelocityFactor);
            // Set the PID gains for the driving motor. Note these are example gains, and you
            // may need to tune them for your own robot!
            drivingPIDController.setP(ModuleConstants.kDrivingP);
            drivingPIDController.setI(ModuleConstants.kDrivingI);
            drivingPIDController.setD(ModuleConstants.kDrivingD);
            drivingPIDController.setFF(ModuleConstants.kDrivingFF);

            // Save the SPARK MAX configurations. If a SPARK MAX browns out during
            // operation, it will maintain the above configurations.
            driveMotor.burnFlash();
            steerMotor.burnFlash();

            driveEncoder.setPosition(0);
            steerEncoder.setPosition(absoluteEncoder.position.refresh().value * 360);
        }

        fun setState(state: SwerveModuleState) {
            turningPIDController.setReference(state.angle.degrees, ControlType.kPosition)
            drivingPIDController.setReference(state.speedMetersPerSecond, ControlType.kVelocity)
        }

        fun getDistance(): Double { return driveEncoder.position }

        fun getAngle(): Rotation2d { return Rotation2d.fromDegrees(steerEncoder.position) }
    }
}