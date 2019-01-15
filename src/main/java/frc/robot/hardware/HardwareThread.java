package frc.robot.hardware;

import frc.robot.Robot;

public class HardwareThread implements Runnable {

    private final PixyCam cam;

    private HardwareThread(Robot robot){
        this.cam = robot.getPixyCam();
    }

    public static Thread create(Robot robot){
        Thread thread = new Thread(new HardwareThread(robot), "Hardware Thread");
        thread.setDaemon(true);
        thread.start();

        return thread;
    }

    @Override
    public void run() {
        while(!Thread.interrupted()){
            cam.updateFrame();
        }
    }

}
