package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Pneumatics;

public class PnuematicCommand extends Command {
	
	private Pneumatics pneumatics;
	private int toExtend;
	private Boolean state;
	
	public PnuematicCommand(Pneumatics pneumatics, Boolean state, int toExtend){
		this.toExtend = toExtend;
		this.state = state;
		this.pneumatics = pneumatics;
		
		requires(pneumatics);
	}
	
	@Override
	public synchronized void start() {
		System.out.println("Firing Pneumatic...");
		pneumatics.set(toExtend, state);
		System.out.println("Fired!");
	}
	
	@Override
	protected boolean isFinished() {
		return true;
	}
}
