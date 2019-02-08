package frc.robot.hardware;

import frc.robot.Robot;

public class HardwareThread implements Runnable {

    //Pixycam ref storage
    private final PixyCam cam;

    //Get pixycam ref.
    private HardwareThread(Robot robot){
        this.cam = robot.getPixyCam();
    }

    //Creates thread
    public static Thread create(Robot robot){
        Thread thread = new Thread(new HardwareThread(robot), "Hardware Thread");
        thread.start();

        return thread;
    }

    //Do stuff in thread
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        int fps = 0;
        
        while(!Thread.interrupted()){
            //Attempet to do Pixy stuff
            try {
                cam.updateFrame();
            } catch (Exception e){
                System.out.println("Pixy Update Failure");
            }
            //FPS counter
            fps++;
            if((startTime + 1000) < System.currentTimeMillis()){
                System.out.println("FPS: " + fps + "\t Time: " + (System.currentTimeMillis() - startTime));
                fps = 0;
                startTime = System.currentTimeMillis();
            }
        }
    }

}
