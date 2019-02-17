package frc.robot.hardware;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import org.opencv.core.Mat;

public class Camera implements Runnable {
	
	//A ton of processing variables
	private UsbCamera camera; //Camera object
	private CvSink cvSink; //Data sink
	private CvSource outputStream; //Output port
	private Mat source; //Data holder
	private Mat output; //Data holder
	
	//Constructor
	public Camera(){
		//Gets camera object
		camera = CameraServer.getInstance().startAutomaticCapture();
		
		//Link data sink and output stream with appropriate references
		cvSink = CameraServer.getInstance().getVideo();
		outputStream = CameraServer.getInstance().putVideo("Blur", 320, 240);
		outputStream.setFPS(50);
		
		//Setup two data-holding variables
		source = new Mat();
		output = new Mat();
	}
	
	//Basic thread stuff
	public static Thread create(){
		Thread thread = new Thread(new Camera(), "Stream Camera Thread");
		thread.start();
		
		return thread;
	}
	
	//Main run loop
	@Override
	public void run() {
		//TODO: FIX IMAGE PROCESSING
	}
}
