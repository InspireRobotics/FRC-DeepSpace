package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Climber extends Subsystem {
	
	private final Spark climbWinch;
	private final Spark[] climbAux;
	
	public Climber(int winchChannel, int[] auxChannels){
		climbWinch = new Spark(winchChannel);
		climbAux = new Spark[]{new Spark(auxChannels[0]), new Spark(auxChannels[1])};
	}
	
	public void setWinchSpeed(double speed){
		climbWinch.set(speed);
		
	}
	public void setClampSpeed(double speed){
		climbAux[0].set(speed);
		climbAux[1].set(-speed);
	}
	
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(null);
	}
}
