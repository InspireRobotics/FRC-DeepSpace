package frc.robot.subsystems;

public class FeetInches {

    private int feet;
    private double inchesWhole;
    private double inchesPart;

    public FeetInches(int feet, double inches){
        this.feet = (int) feet;
        this.inchesPart = inches;
        this.inchesWhole = inches + feet * 12;
    }
    public FeetInches(double inches){
        this.inchesWhole = inches;
        this.feet = (int) (inches - inches % 12);
        this.inchesPart = inches % 12;
    }

    public int getFeet() {
        return feet;
    }

    public double getInchesPart() {
        return inchesPart;
    }

    public double getInchesWhole() {
        return inchesWhole;
    }
}
