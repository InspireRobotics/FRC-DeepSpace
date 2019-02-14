package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.ArrayList;

public class Pneumatics extends Subsystem {
	
	private final ArrayList<Solenoid> cylinders = new ArrayList<>();
	private final ArrayList<DoubleSolenoid> dualCylinders = new ArrayList<>();
	
	private int[] idList;
	private int[][] doubleIdList;
	
	public Pneumatics(int[] idList, int[][] doubleIdList){
		this.idList = idList;
		this.doubleIdList = doubleIdList;
		for (var i = 0; i < idList.length; i++){
			cylinders.add(new Solenoid(idList[i]));
		}
		for (var i = 0; i < doubleIdList.length; i++){
			dualCylinders.add(new DoubleSolenoid(doubleIdList[i][0],doubleIdList[i][1]));
		}
	}
	public void set(int id, boolean state){
		for (var i = 0; i < idList.length; i++){
			if (idList[i] == id){
				unsafeSet(i, state);
			}
		}
	}
	private void unsafeSet(int id, boolean state){
		cylinders.get(id).set(state);
	}
	public void doubleSet(int id1, int id2, DoubleSolenoid.Value channel){
		for (var i = 0; i < doubleIdList.length; i++){
			if (doubleIdList[i][0] == id1 && doubleIdList[i][1] == id2 || doubleIdList[i][1] == id1 && doubleIdList[i][0] == id2){
				unsafeDoubleSet(i, channel);
			}
		}
	}
	private void unsafeDoubleSet(int id, DoubleSolenoid.Value channel){
		dualCylinders.get(id).set(channel);
	}
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(null);
	}
}
