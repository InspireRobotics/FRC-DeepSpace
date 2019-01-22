package frc.robot.hardware;

import frc.robot.Robot;

public class HardwareThread implements Runnable {

    private final PixyCam cam;

    private HardwareThread(Robot robot){
        this.cam = robot.getPixyCam();
    }

    public static Thread create(Robot robot){
        Thread thread = new Thread(new HardwareThread(robot), "Hardware Thread");
        thread.start();

        return thread;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        int fps = 0;

        while(!Thread.interrupted()){
            cam.updateFrame();
            fps++;
            if((startTime + 1000) < System.currentTimeMillis()){
                System.out.println("FPS: " + fps + "\t Time: " + (System.currentTimeMillis() - startTime));
                fps = 0;
                startTime = System.currentTimeMillis();
            }
        }
    }

}
