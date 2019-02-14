package frc.robot.hardware;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Camera implements Runnable {
	
	private CameraServer cameraServer;
	private UsbCamera camera;
	private CvSink cvSink;
	private CvSource outputStream;
	private Mat source;
	private Mat output;
	
	
	public Camera(){
		cameraServer = CameraServer.getInstance();
		camera = cameraServer.startAutomaticCapture();
		
		camera.setFPS(50);
		camera.setResolution(640, 480);
		
		cvSink = cameraServer.getVideo();
		outputStream = cameraServer.putVideo("blur", 640, 480);
		
		source = new Mat();
		output = new Mat();
	}
	
	public static Thread create(){
		Thread thread = new Thread(new Camera(), "Stream Camera Thread");
		thread.start();
		
		return thread;
	}
	
	@Override
	public void run() {
		long frameStart = System.currentTimeMillis();
		int FPS = 0;
		while (!Thread.interrupted()){
			cvSink.grabFrame(source);
			
			Imgproc.cvtColor(source, output, Imgproc.COLOR_BayerGR2RGB);
			
			outputStream.putFrame(output);
			FPS++;
			if (frameStart + 1000 < System.currentTimeMillis()){
				System.out.println("Microsoft FPS: " + FPS + " Time: " + (System.currentTimeMillis() - frameStart));
				FPS = 0;
				frameStart = System.currentTimeMillis();
			}
		}
	}
}
