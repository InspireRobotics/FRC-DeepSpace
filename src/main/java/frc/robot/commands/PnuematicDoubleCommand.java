package frc.robot.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Pneumatics;

public class PnuematicDoubleCommand extends Command {
	
	private int[] toExtend;
	private DoubleSolenoid.Value state;
	private Pneumatics pneumatics;
	
	public PnuematicDoubleCommand(Pneumatics pneumatics, DoubleSolenoid.Value state, int[] toExtend){
		this.pneumatics = pneumatics;
		this.state = state;
		this.toExtend = toExtend;
	}
	
	@Override
	public synchronized void start() {
		pneumatics.doubleSet(toExtend[0], toExtend[1], state);
	}
	
	@Override
	protected boolean isFinished() {
		return true;
	}
}