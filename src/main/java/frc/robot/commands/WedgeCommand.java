package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Wedge;

public class WedgeCommand extends Command {
	private final long startTime;
	private final int climbTime;
	private final Wedge wedge;
	private double speed;
	
	public WedgeCommand(Wedge wedge, double speed, int climbTime){
		this.startTime = System.currentTimeMillis();
		this.climbTime = climbTime;
		this.wedge = wedge;
		this.speed = speed;
	}
	
	@Override
	public synchronized void start() {
		wedge.setWedgeSpeed(speed);
	}
	
	@Override
	protected boolean isFinished() {
		Boolean finished = startTime + climbTime < System.currentTimeMillis();
		if (finished){
			wedge.setWedgeSpeed(0);
		}
		return finished;
	}
}
