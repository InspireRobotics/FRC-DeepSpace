package frc.robot.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.HardwareMap;

import static frc.robot.HardwareMap.Measurements.WHEEL_DIAMETER;

public class NEODrivetrain extends Subsystem {

        private CANSparkMax leftFront;
        private CANSparkMax leftBack;

        private CANSparkMax rightFront;
        private CANSparkMax rightBack;

        private CANEncoder left;
        private CANEncoder right;

        private PowerDistributionPanel pdp = new PowerDistributionPanel();

        public void NEODrivetrain() {
                leftFront = new CANSparkMax(HardwareMap.CAN.LEFT_FRONT_DRIVE, CANSparkMaxLowLevel.MotorType.kBrushless);
                leftBack = new CANSparkMax(HardwareMap.CAN.LEFT_BACK_DRIVE, CANSparkMaxLowLevel.MotorType.kBrushless);
                leftBack.follow(leftFront);

                rightFront = new CANSparkMax(HardwareMap.CAN.RIGHT_FRONT_DRIVE, CANSparkMaxLowLevel.MotorType.kBrushless);
                rightBack = new CANSparkMax(HardwareMap.CAN.RIGHT_BACK_DRIVE, CANSparkMaxLowLevel.MotorType.kBrushless);
                rightBack.follow(rightFront);

                left = new CANEncoder(leftFront);
                right = new CANEncoder(rightFront);
        }

        public void Drive(double left, double right){
                if (left < 0.2) {
                        left = 0;
                }
                if (right < 0.2) {
                        right = 0;
                }
                double diff = 12 / Math.min(12, pdp.getVoltage());
                left *= diff;
                right *= diff;
                leftFront.set(left);
                rightFront.set(right);
        }
        public void Stop(){
                leftFront.stopMotor();
                rightFront.stopMotor();
        }
        public void DriveDistance(FeetInches distance){
                double leftStart = left.getPosition() * WHEEL_DIAMETER.getInchesWhole() * Math.PI;
                double rightStart = right.getPosition() * WHEEL_DIAMETER.getInchesWhole() * Math.PI;

                double leftTarget = leftStart + distance.getInchesWhole();
                double rightTarget = rightStart + distance.getInchesWhole();

                double leftDiff = leftTarget - leftStart;
                double rightDiff = rightTarget - rightStart;

                while (Math.abs(leftStart - leftTarget) > 1 && Math.abs(rightStart - rightTarget) > 1){
                        leftDiff = leftTarget - left.getPosition() * WHEEL_DIAMETER.getInchesWhole() * Math.PI;
                        rightDiff = rightTarget - right.getPosition() * WHEEL_DIAMETER.getInchesWhole() * Math.PI;

                        double diff = (leftDiff - rightDiff)/10;

                        leftFront.set(0.75 - diff);
                        rightFront.set(0.75 + diff);
                }
        }
        //TODO: Properly setup "Defualt Command"
        @Override
        protected void initDefaultCommand() {

        }
}
