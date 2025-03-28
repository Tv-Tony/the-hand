package tv.toner.dto;

public class TestStruct {

    private double angleX;

    private double angleY;

    private double angleZ;

    public TestStruct(double angleX, double angleY, double angleZ) {
        this.angleX = angleX;
        this.angleY = angleY;
        this.angleZ = angleZ;
    }

    public TestStruct() {
        // Empty Constructor For Json Serialization
    }

    public double getAngleX() {
        return angleX;
    }

    public void setAngleX(double angleX) {
        this.angleX = angleX;
    }

    public double getAngleY() {
        return angleY;
    }

    public void setAngleY(double angleY) {
        this.angleY = angleY;
    }

    public double getAngleZ() {
        return angleZ;
    }

    public void setAngleZ(double angleZ) {
        this.angleZ = angleZ;
    }

    @Override
    public String toString() {
        return "TestStruct{" +
                "angleX=" + angleX +
                ", angleY=" + angleY +
                ", angleZ=" + angleZ +
                '}';
    }
}
