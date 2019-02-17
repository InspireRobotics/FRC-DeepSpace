package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drivetrain;

public class DriveCommand extends Command {
	
	private final Drivetrain drivetrain;
	private final long startTime;
	private final double speed;
	private final long duration;
	
	public DriveCommand(Drivetrain drivetrain, double speed, long duration){
		this.drivetrain = drivetrain;
		this.speed = speed;
		this.duration = duration;
		startTime = System.currentTimeMillis();
		
		requires(drivetrain);
	}
	
	@Override
	public synchronized void start() {
		drivetrain.drive(speed, speed);
	}
	
	@Override
	protected boolean isFinished() {
		Boolean finished = startTime + duration > System.currentTimeMillis();
		if (finished){
			drivetrain.Stop();
		}
		return finished;
	}
}
