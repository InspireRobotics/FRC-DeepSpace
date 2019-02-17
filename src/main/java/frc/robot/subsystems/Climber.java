package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Climber extends Subsystem {
	
	private Spark climbMotor;
	
	public Climber(int channel){
		climbMotor = new Spark(channel);
	}
	
	public void setSpeed(double speed){
		climbMotor.set(speed);
	}
	
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(null);
	}
}
