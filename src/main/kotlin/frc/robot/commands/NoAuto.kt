package frc.robot.commands

import edu.wpi.first.wpilibj2.command.Command

class NoAuto : Command() {
    override fun isFinished(): Boolean {
        return true
    }
}