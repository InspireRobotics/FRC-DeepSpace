package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Climber extends Subsystem {
	
	private Relay climbMotor;
	
	public Climber(int channel){
		climbMotor = new Relay(channel);
	}
	
	public void extend(){
		climbMotor.set(Relay.Value.kForward);
	}
	
	public void retract(){
		climbMotor.set(Relay.Value.kReverse);
	}
	
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(null);
	}
}
