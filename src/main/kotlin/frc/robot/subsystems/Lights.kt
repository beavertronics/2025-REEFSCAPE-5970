package frc.robot.subsystems

import edu.wpi.first.wpilibj.AddressableLED
import edu.wpi.first.wpilibj.AddressableLEDBuffer
import edu.wpi.first.wpilibj.LEDPattern
import edu.wpi.first.wpilibj2.command.SubsystemBase
import edu.wpi.first.units.Units
import edu.wpi.first.units.Units.Percent
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj.util.Color
import edu.wpi.first.wpilibj2.command.Command
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
        defaultCommand = //runPatternAtBrightness(scrollingRainbow, { TeleOp.OI.driveFieldOrientedForwards.absoluteValue })
            fromShuffleBoard(
                mapOf(
                    Pair("Off", LEDPattern.solid(Color.kBlack)),
                    Pair("Rainbow", scrollingRainbow),
                    Pair("TransFlag", Transflag),
                    Pair("AceFlag", AceFlag),
                    Pair("BiFlag", BiFlag),
                )
            )
        // randomCyclePatterns(10.0, scrollingRainbow, Transflag, AceFlag, BiFlag).withName("Trans").ignoringDisable(true)
        lights.setLength(length) // Length in meters times 60
        lights.start()
        lights.setData(buffer)
    }

    override fun periodic() {
        lights.setData(buffer);
    }

    fun runPattern(pattern: LEDPattern) : Command {
        val command : Command = run { pattern.applyTo(buffer) }.repeatedly().ignoringDisable(true)
        return command
    }
    fun runPatternAtBrightness(pattern: LEDPattern, brightness : ()->Double) : Command {
        val command : Command = run { pattern.atBrightness(Percent.of(100 * brightness())).applyTo(buffer); }.repeatedly().ignoringDisable(true)
        return command
    }


    /**
     * Iterates through each patter in vararg patterns in order, looping
     * @param patterns List of paterns to iterate through
     * @param cycleTime to between each cycle
     */
    class cyclePatterns(val cycleTime : Double, vararg val patterns: LEDPattern) : Command() {
        var iterator : Int = 0
        val timer = Timer()
        init { addRequirements(Lights) }
        override fun runsWhenDisabled(): Boolean { return true }
        override fun initialize() { timer.restart() }
        override fun execute() {
            println(iterator)
            patterns[iterator].applyTo(buffer)
            if(timer.hasElapsed(cycleTime)) {
                timer.restart()
                iterator = (iterator + 1) % patterns.size
            }
        }
    }
    /**
     * Iterates through each patter in vararg patterns in order, looping
     * @param patterns List of paterns to iterate through
     * @param cycleTime to between each cycle
     */
    class randomCyclePatterns(val cycleTime : Double, vararg val patterns: LEDPattern) : Command() {
        var iterator : Int = 0
        val timer = Timer()
        init { addRequirements(Lights) }
        override fun runsWhenDisabled(): Boolean { return true }

        override fun initialize() { timer.restart(); iterator = (0..patterns.size).random() }
        override fun execute() {
            patterns[iterator].applyTo(buffer)
            if(timer.hasElapsed(cycleTime)) { timer.restart(); iterator = (0..patterns.size).random() }
        }
    }
    /**
     * Iterates through each patter in vararg patterns in order, looping
     * @param patterns List of paterns to iterate through
     * @param cycleTime to between each cycle
     */
    class fromShuffleBoard(val patterns: Map<String, LEDPattern>) : Command() {
        val patternChooser = SendableChooser<LEDPattern>()
        init { addRequirements(Lights) }
        override fun runsWhenDisabled(): Boolean { return true }

        override fun initialize() {
            var i = 0
            patterns.forEach {
                name: String, pattern : LEDPattern ->
                if(i == 0) patternChooser.setDefaultOption(name, pattern)
                    else patternChooser.addOption(name, pattern)
                i++

            }
            SmartDashboard.putData("LEDPatterns", patternChooser)
        }
        override fun execute() {
            if(patternChooser.selected != null) patternChooser.selected.applyTo(buffer)
        }
    }
}