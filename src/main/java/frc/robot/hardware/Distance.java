package frc.robot.hardware;

import edu.wpi.first.wpilibj.AnalogInput;

public class Distance {
	
	private AnalogInput input;
	
	public Distance(int port) {
		input = new AnalogInput(port);
	}
	
	public double GetDist(){
		return input.getAverageVoltage() * 2;
	}
	
}
