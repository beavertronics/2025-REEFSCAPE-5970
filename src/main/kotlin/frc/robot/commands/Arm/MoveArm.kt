package frc.robot.commands.Arm

import edu.wpi.first.wpilibj2.command.Command

enum class ReefLevel {
    L1, L2, L3, L4
}

class MoveArm(armHeight : ReefLevel) : Command() {
    //TODO make arm move to level
    override fun isFinished(): Boolean {
        return true
    }
}