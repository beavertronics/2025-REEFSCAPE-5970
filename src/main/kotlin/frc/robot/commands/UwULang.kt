package frc.robot.commands

import edu.wpi.first.wpilibj2.command.Command
import frc.robot.subsystems.Vision

object UwULang : Command() {
    init {
        addRequirements(Vision)
    }
    override fun initialize() {
        Vision.listeners.add(
            "Print vision results"
        ) {
            if (it.bestTarget != null) {
                println(it.bestTarget.bestCameraToTarget)
            }
        }
    }

    override fun runsWhenDisabled(): Boolean {
        return true
    }

    override fun execute() {
    }
}