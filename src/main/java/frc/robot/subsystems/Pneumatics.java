package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.ArrayList;

public class Pneumatics extends Subsystem {
	
	//Storage of Pneumatic objects
	private final ArrayList<Solenoid> cylinders = new ArrayList<>();
	private final ArrayList<DoubleSolenoid> dualCylinders = new ArrayList<>();
	//Note with compressor object: It cannot be forced to enable...it only can be 'armed', and it will activate based on pressure status
	private final Compressor compressor;
	
	//Storage of Pneumatic ids
	private int[] idList;
	private int[][] doubleIdList;
	
	//Creation of Pneumatic object
	public Pneumatics(int[] idList, int[][] doubleIdList, int compressorId){
		this.idList = idList;
		this.doubleIdList = doubleIdList;
		compressor = new Compressor(compressorId);
		for (var i = 0; i < idList.length; i++){
			cylinders.add(new Solenoid(idList[i]));
		}
		for (var i = 0; i < doubleIdList.length; i++){
			dualCylinders.add(new DoubleSolenoid(doubleIdList[i][0],doubleIdList[i][1]));
		}
	}
	
	//Starts automatic compressor
	public void startCompressor(){
		compressor.start();
	}
	
	//Stops automatic compressor
	public void stopCompressor(){
		compressor.stop();
	}
	
	//Return pressure
	public Boolean systemPressurized(){
		return compressor.getPressureSwitchValue();
	}
	
	//Setting state of single solenoid
	public void set(int id, boolean state){
		for (var i = 0; i < idList.length; i++){
			if (idList[i] == id){
				unsafeSet(i, state);
			}
		}
	}
	//Private setter
	private void unsafeSet(int id, boolean state){
		cylinders.get(id).set(state);
	}
	
	//Setting state of double solenoid
	public void doubleSet(int id1, int id2, DoubleSolenoid.Value channel){
		for (var i = 0; i < doubleIdList.length; i++){
			if (doubleIdList[i][0] == id1 && doubleIdList[i][1] == id2 || doubleIdList[i][1] == id1 && doubleIdList[i][0] == id2){
				unsafeDoubleSet(i, channel);
			}
		}
	}
	
	//Private setter
	private void unsafeDoubleSet(int id, DoubleSolenoid.Value channel){
		dualCylinders.get(id).set(channel);
	}
	
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(null);
	}
}
