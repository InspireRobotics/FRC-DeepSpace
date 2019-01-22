package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.HardwareMap;

public class NEODrivetrain extends Subsystem {

        private CANSparkMax leftFront;
        private CANSparkMax leftBack;

        private CANSparkMax rightFront;
        private CANSparkMax rightBack;

        private PowerDistributionPanel pdp = new PowerDistributionPanel();

        public void NEODrivetrain() {
                leftFront = new CANSparkMax(HardwareMap.CAN.LEFT_FRONT_DRIVE, CANSparkMaxLowLevel.MotorType.kBrushless);
                leftBack = new CANSparkMax(HardwareMap.CAN.LEFT_BACK_DRIVE, CANSparkMaxLowLevel.MotorType.kBrushless);
                leftBack.follow(leftFront);

                rightFront = new CANSparkMax(HardwareMap.CAN.RIGHT_FRONT_DRIVE, CANSparkMaxLowLevel.MotorType.kBrushless);
                rightBack = new CANSparkMax(HardwareMap.CAN.RIGHT_BACK_DRIVE, CANSparkMaxLowLevel.MotorType.kBrushless);
                rightBack.follow(rightFront);
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
        //TODO: Properly setup "Defualt Command"
        @Override
        protected void initDefaultCommand() {

        }
}
