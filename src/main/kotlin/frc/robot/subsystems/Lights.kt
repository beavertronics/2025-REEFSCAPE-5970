package frc.robot.subsystems

import edu.wpi.first.units.DimensionlessUnit
import edu.wpi.first.wpilibj.AddressableLED
import edu.wpi.first.wpilibj.AddressableLEDBuffer
import edu.wpi.first.wpilibj.LEDPattern
import edu.wpi.first.wpilibj2.command.SubsystemBase
import edu.wpi.first.units.Units
import edu.wpi.first.units.Units.Percent
import edu.wpi.first.units.measure.Dimensionless
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.util.Color
import edu.wpi.first.wpilibj2.command.Command
import jdk.jfr.Percentage
import kotlin.math.round
import kotlin.math.roundToInt

fun beaverColor(red : Int, green : Int, blue : Int, alpha : Double = 1.0) : Color {
    return Color((green * alpha).roundToInt(), (red * alpha).roundToInt(), round(blue * alpha).roundToInt())
}
object Lights : SubsystemBase() {
    val timer = Timer()
    val length = 5 * 60; // 2 meters times 60 LEDs per meter
    val density = Units.Meters.of(1.0/60);

    val lights = AddressableLED(9); // PWM port 0
    var buffer = AddressableLEDBuffer(length);

    // Patterns:
    val off = LEDPattern.solid(Color.kBlack)
    val scrollingRainbow = LEDPattern.rainbow(255, 128).scrollAtAbsoluteSpeed(Units.MetersPerSecond.of(1.0),density); //255 = max saturation, 128 = half brightness, scroll at 1 m/s
    val Transflag = LEDPattern.steps(
        mutableMapOf(
            Pair(0, beaverColor(45, 102, 250)), //blue
            Pair(0.2, beaverColor(245, 85, 92)), //blue
            Pair(0.4, beaverColor(255,255,255)), //blue
            Pair(0.6, beaverColor(245, 85, 92)), //blue
            Pair(0.8, beaverColor(45, 102, 250))) //blue
    ).scrollAtAbsoluteSpeed(Units.MetersPerSecond.of(0.5),density).atBrightness(Percent.of(75.0))
    val AceFlag = LEDPattern.steps(
        mutableMapOf(
            Pair(0, beaverColor(0, 0, 0)), //blue
            Pair(0.25, beaverColor(50, 50, 50)), //blue
            Pair(0.5, beaverColor(255,255,255)), //blue
            Pair(0.75, beaverColor(128, 0, 128))) //blue
    ).scrollAtAbsoluteSpeed(Units.MetersPerSecond.of(0.5),density).atBrightness(Percent.of(75.0))
    val BiFlag = LEDPattern.steps(
        mutableMapOf(
            Pair(0.0, beaverColor(214, 2, 112)), //blue
            Pair(0.35, beaverColor(110, 20, 110)), //blue
            Pair(0.7, beaverColor(0, 56, 168)),
        )//blue
    ).scrollAtAbsoluteSpeed(Units.MetersPerSecond.of(0.5),density).atBrightness(Percent.of(75.0))
    init {
        defaultCommand = runPattern(off).withName("Trans")
    }
    fun init() {
        lights.setLength(length) // Length in meters times 60
        lights.start()
        lights.setData(buffer);
    }

    override fun periodic() {

        //Transflag.applyTo(buffer);
        lights.setData(buffer);
        //if(timer.hasElapsed(5.0))
    }

    fun runPattern(pattern: LEDPattern) : Command {
        val command : Command = run { pattern.applyTo(buffer) }.repeatedly().ignoringDisable(true)
        return command
    }
    class cyclePatterns(vararg pattern: LEDPattern, cycleTime : Double) : Command() {
        
    }
}