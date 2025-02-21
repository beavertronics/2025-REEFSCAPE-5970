package frc.robot

object RobotInfo {
    /**
     * SPECS
     * - WCP X2S swerve modules
     * - Neo 550 steer motors
     * - kraken X60 drive motors
     * - NavX IMU/Gyroscope
     * - SRX Mag absolute encoder
     *
     * DATA
     * - Steering ratio 1:1 (For YAGSL: 360)
     * - Driving ratio: 5.625
     * - Wheel diameter: 3.5 inches
     *
     * POSITIONING (for YAGSL) (coordinates) (inches) (front of robot is X+)
     * - front left: 11, 11
     * - front right: 11, -11
     * - back left: -11, 11
     * - back right: -11, -11
     */

    // Drivetrain
    const val LeftMainDrive = 13 // todo
    const val LeftSubDrive = 7 // todo
    const val RightMainDrive = 9 // todo
    const val RightSubDrive = 10 // todo
    const val DriveMotorCurrentLimit = 20

    // Subsystem - Climb
    const val ClimbMotorWinchID = 6 // todo
    const val ClimbCurrentLimit = 10
    const val ClimbLimitSwitchDIO = 0

    // Subystem - Arm
    const val ArmMotorID = 0 // todo
    const val ArmAmpLimit = 0 // todo
    const val ArmVoltLimit = 0 // todo
    const val ArmStartLimitSwitchDIO = 0 // todo
    const val ArmEndLimitSwitchDIO = 0 // todo

    // Subsystem - LED
    const val LEDPWM = 9
}