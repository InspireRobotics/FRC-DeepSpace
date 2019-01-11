package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class HardwareMap {

    public static class PWM{
        public static final int LEFT_FRONT_DRIVE = 0;
        public static final int LEFT_BACK_DRIVE = 1;
        public static final int RIGHT_FRONT_DRIVE = 2;
        public static final int RIGHT_BACK_DRIVE = 3;
    }

    public static class Joysticks{
        public static final Joystick drive = new Joystick(0);
        public static final Joystick aux = new Joystick(1);

        public static final int LEFT_Y_AXIS = 1;
        public static final int RIGHT_Y_AXIS = 5;
    }


}
