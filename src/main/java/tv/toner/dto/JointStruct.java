package tv.toner.dto;

import java.util.Objects;

public class JointStruct {

    private String name;  // To hold the name from JSON
    private Joint joint;  // This will be the nested joint object

    // Getter and setter for 'name'
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for 'joint'
    public Joint getJoint() {
        return joint;
    }

    public void setJoint(Joint joint) {
        this.joint = joint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JointStruct that = (JointStruct) o;
        return Objects.equals(name, that.name) && Objects.equals(joint, that.joint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, joint);
    }

    @Override
    public String toString() {
        return "JointStruct{" +
                "name='" + name + '\'' +
                ", joint=" + joint +
                '}';
    }

    // Static inner class representing the 'joint' object
    public static class Joint {

        private double jox;  // Rotation along X-axis
        private double joy;  // Rotation along Y-axis
        private double joz;  // Rotation along Z-axis

        // Getter and setter methods for jox, joy, joz
        public double getJox() {
            return jox;
        }

        public void setJox(double jox) {
            this.jox = jox;
        }

        public double getJoy() {
            return joy;
        }

        public void setJoy(double joy) {
            this.joy = joy;
        }

        public double getJoz() {
            return joz;
        }

        public void setJoz(double joz) {
            this.joz = joz;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Joint joint = (Joint) o;
            return Double.compare(jox, joint.jox) == 0 && Double.compare(joy, joint.joy) == 0 && Double.compare(joz, joint.joz) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(jox, joy, joz);
        }

        @Override
        public String toString() {
            return "Joint{" +
                    "jox=" + jox +
                    ", joy=" + joy +
                    ", joz=" + joz +
                    '}';
        }
    }
}

