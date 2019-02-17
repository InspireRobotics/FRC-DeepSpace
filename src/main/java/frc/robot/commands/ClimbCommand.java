package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Climber;

public class ClimbCommand extends Command {
	
	private final long startTime;
	private final int climbTime;
	private final Climber climber;
	private double speed;
	
	public ClimbCommand(Climber climber, double speed, int climbTime){
		this.startTime = System.currentTimeMillis();
		this.climbTime = climbTime;
		this.climber = climber;
		this.speed = speed;
	}
	
	@Override
	public synchronized void start() {
		climber.setSpeed(speed);
	}
	
	@Override
	protected boolean isFinished() {
		Boolean finished = startTime + climbTime < System.currentTimeMillis();
		if (finished){
			climber.setSpeed(0);
		}
		return finished;
	}
}
