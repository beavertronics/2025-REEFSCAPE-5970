package frc.robot.subsystems

import edu.wpi.first.wpilibj.AddressableLED
import edu.wpi.first.wpilibj.AddressableLEDBuffer
import edu.wpi.first.wpilibj.LEDPattern
import edu.wpi.first.wpilibj2.command.SubsystemBase
import edu.wpi.first.units.Units

object Lights : SubsystemBase() {

    val length = 5 * 60; // 2 meters times 60 LEDs per meter
    val density = Units.Meters.of(1.0/60);

    val lights = AddressableLED(9); // PWM port 0
    var buffer = AddressableLEDBuffer(length);

    // Patterns:
    val scrollingRainbow = LEDPattern.rainbow(255, 128).scrollAtAbsoluteSpeed(Units.MetersPerSecond.of(1.0),density); //255 = max saturation, 128 = half brightness, scroll at 1 m/s

    fun init() {
        lights.setLength(length); // Length in meters times 60
        lights.start();
        scrollingRainbow.applyTo(buffer);
        lights.setData(buffer);
    }

    override fun periodic() {
        scrollingRainbow.applyTo(buffer);
        lights.setData(buffer);
    }
}