package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.HardwareMap;
import frc.robot.commands.DefaultDriveCommand;

import static frc.robot.HardwareMap.Measurements.WHEEL_DIAMETER;

public class Drivetrain extends Subsystem {
        
        boolean fallback = false; //Are we using old motors?

        //Motor object storage
        private CANSparkMax leftFront;
        private CANSparkMax leftBack;

        private CANSparkMax rightFront;
        private CANSparkMax rightBack;
        
        private CANEncoder left;
        private CANEncoder right;

        //private PowerDistributionPanel pdp = new PowerDistributionPanel();
        
        //f for fallback, old motor object storage
        private Spark fLeftFront;
        private Spark fLeftBack;
        
        private Spark fRightFront;
        private Spark fRightBack;

        
        public Drivetrain() {
                
                //Initializes based on if it is in fallback mode or not
                if (!fallback) {
                        leftFront = new CANSparkMax(HardwareMap.CAN.LEFT_FRONT_DRIVE, CANSparkMaxLowLevel.MotorType.kBrushless);
                        leftBack = new CANSparkMax(HardwareMap.CAN.LEFT_BACK_DRIVE, CANSparkMaxLowLevel.MotorType.kBrushless);
        
                        leftFront.setRampRate(1);
        
                        leftBack.setInverted(true);
        
                        leftBack.follow(leftFront);
        
                        rightFront = new CANSparkMax(HardwareMap.CAN.RIGHT_FRONT_DRIVE, CANSparkMaxLowLevel.MotorType.kBrushless);
                        rightBack = new CANSparkMax(HardwareMap.CAN.RIGHT_BACK_DRIVE, CANSparkMaxLowLevel.MotorType.kBrushless);
        
                        rightFront.setRampRate(1);
        
                        rightFront.setInverted(true);
        
                        rightBack.follow(rightFront);
        
                        left = new CANEncoder(leftFront);
                        right = new CANEncoder(rightFront);
                        
                } else {
                        
                        fLeftFront = new Spark(HardwareMap.PWM.LEFT_FRONT_DRIVE);
                        fLeftBack = new Spark(HardwareMap.PWM.LEFT_BACK_DRIVE);
        
                        fRightFront = new Spark(HardwareMap.PWM.RIGHT_FRONT_DRIVE);
                        fRightBack = new Spark(HardwareMap.PWM.RIGHT_BACK_DRIVE);
                        
                }
        }

        //Drives drivetrain, by sending commands to individual motors
        public void drive(double left, double right){
                //Limits deadzones to prevent burning out the motors
                if (Math.abs(left) < 0.2) {
                        left = 0;
                }
                if (Math.abs(right) < 0.2) {
                        right = 0;
                }
                
                //A possible solution to a weak battery (for consistency)
                //double diff = 12 / Math.min(12, pdp.getVoltage());
                
                //If not in fallback mode...else...
                if (!fallback) {
                        //...drive new motors
                        leftFront.set(left);
                        rightFront.set(right);
                } else {
                        //...drive old motors (right side manually inverted)
                        fLeftFront.set(left);
                        fLeftBack.set(left);
                        
                        fRightFront.set(-right);
                        fRightBack.set(-right);
                }

                //System.out.println(String.format("Left: %f, Right: %f", left, right));
        }
        
        //Stops all motors in the drivetrain
        public void Stop() {
                //If not in fallback mode...else...
                if (!fallback) {
                        //...stop new motors
                        leftFront.stopMotor();
                        rightFront.stopMotor();
                } else {
                        //...stop old motors
                        fLeftFront.stopMotor();
                        fLeftBack.stopMotor();
        
                        fRightFront.stopMotor();
                        fRightBack.stopMotor();
                }
        }
        public void DriveDistance(FeetInches distance) {
                if (!fallback) {
                        double leftStart = left.getPosition() * WHEEL_DIAMETER.getInchesWhole() * Math.PI;
                        double rightStart = right.getPosition() * WHEEL_DIAMETER.getInchesWhole() * Math.PI;
        
                        double leftTarget = leftStart + distance.getInchesWhole();
                        double rightTarget = rightStart + distance.getInchesWhole();
        
                        double leftDiff = leftTarget - leftStart;
                        double rightDiff = rightTarget - rightStart;
        
                        while (Math.abs(leftStart - leftTarget) > 1 && Math.abs(rightStart - rightTarget) > 1) {
                                leftDiff = leftTarget - left.getPosition() * WHEEL_DIAMETER.getInchesWhole() * Math.PI;
                                rightDiff = rightTarget - right.getPosition() * WHEEL_DIAMETER.getInchesWhole() * Math.PI;
                
                                double diff = (leftDiff - rightDiff) / 10;
                
                                leftFront.set(0.75 - diff);
                                rightFront.set(0.75 + diff);
                        }
                } else {
                        throw new Error("This method is unavailable on older/incompatible drivetrains");
                }
        }
        //Sets default drive command
        @Override
        protected void initDefaultCommand() {
                setDefaultCommand(new DefaultDriveCommand(this));
        }
}