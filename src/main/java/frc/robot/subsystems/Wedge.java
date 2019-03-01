package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Wedge extends Subsystem {
	
	private final Spark wedge;
	
	public Wedge(int port){
		wedge = new Spark(port);
	}
	
	public void setWedgeSpeed(double speed){
		wedge.set(speed);
	}
	
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(null);
	}
}
