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
    const val FrontLeftDrive = 2
    const val FrontRightDrive = 3
    const val BackLeftDrive = 4
    const val BackRightDrive = 5
    const val FrontLeftSteer = 6
    const val FrontRightSteer = 7
    const val BackLeftSteer = 8
    const val BackrightSteer = 9

    // Subsystem - Climb
    const val ClimbMotorWinchID = 10
    const val ClimbAmpLimit = 30
    const val ClimbVoltLimit = 12
    const val ClimbLimitSwitchDIO = 0 // todo

    // Subystem - Arm
    const val ArmMotorID = 0 // todo
    const val ArmAmpLimit = 0 // todo
    const val ArmVoltLimit = 0 // todo

    // Subsystem - LED
    const val LEDPWM = 9
}