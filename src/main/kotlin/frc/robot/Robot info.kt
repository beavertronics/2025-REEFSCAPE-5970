package frc.robot

object `Robot info` {
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
    const val frontLeftDrive = 2
    const val frontRightDrive = 3
    const val backLeftDrive = 4
    const val backRightDrive = 5
    const val frontLeftSteer = 6
    const val frontRightSteer = 7
    const val backLeftSteer = 8
    const val backrightSteer = 9

    // Subsystem - Climb
    const val ClimbID = 10
    const val ampLimit = 30
    const val voltLimit = 12
    const val limitSwitchDIO = 0

    // Subsystem - LED
    const val LEDPWM = 9
}