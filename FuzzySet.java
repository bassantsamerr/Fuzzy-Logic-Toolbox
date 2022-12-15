import java.util.ArrayList;

public class FuzzySet {
    public String Name;
    public String Shape;
    public ArrayList<Point> points;

    public double membership=-1;

    public FuzzySet(String name, String shape, ArrayList<Point> points, double membership) {
        Name = name;
        Shape = shape;
        this.points = points;
        this.membership = membership;
    }

    public FuzzySet() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getShape() {
        return Shape;
    }

    public void setShape(String type) {
        Shape = type;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public double getMembership() {
        return membership;
    }

    public void setMembership(double membership) {
        this.membership = membership;
    }

    @Override
    public String toString() {
        return "FuzzySet{" +
                "Name='" + Name + '\'' +
                ", Shape='" + Shape + '\'' +
                ", points=" + points +
                ", membership=" + membership +
                '}';
    }
}
