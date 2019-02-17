package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.subsystems.FeetInches;

public class HardwareMap {

    public static class PWM{
        public static final int LEFT_FRONT_DRIVE = 0;
        public static final int LEFT_BACK_DRIVE = 1;
        public static final int RIGHT_FRONT_DRIVE = 2;
        public static final int RIGHT_BACK_DRIVE = 3;
    }
    public static class CAN {
        public static final int LEFT_FRONT_DRIVE = 7;
        public static final int LEFT_BACK_DRIVE = 8;
        public static final int RIGHT_FRONT_DRIVE = 5;
        public static final int RIGHT_BACK_DRIVE = 6;
    }
    public static class Joysticks{
        public static final Joystick drive = new Joystick(0);
        public static final Joystick aux = new Joystick(1);

        public static final int LEFT_Y_AXIS = 1;
        public static final int RIGHT_Y_AXIS = 5;

        public static final int A_BUTTON = 1;
        public static final int B_BUTTON = 2;
        public static final int X_BUTTON = 3;
        public static final int Y_BUTTON = 4;
    }
    
    public static class Pnuematic{
        public static final int[] singleList = new int[]{};
        public static final int[][] doubleList = new int[][]{{0, 3}, {4, 7}};
        public static final int compresserId = 0;
    }

    public static class Sensors{
        public static final int PIXY_CAM = 54;
    }

    public static class Measurements{
        public static final FeetInches MARKER_DIST_TARGET = new FeetInches(11.25);
        public static final FeetInches WHEEL_DIAMETER = new FeetInches(8);
    }
    
    
}
