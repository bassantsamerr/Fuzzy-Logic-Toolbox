import java.util.ArrayList;

public class FuzzySet {
    public String Name;
    public String Shape;

    public ArrayList<Point> points;

    public FuzzySet(String name, String shape, ArrayList<Point> points) {
        Name = name;
        Shape = shape;
        this.points = points;
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

    @Override
    public String toString() {
        return "FuzzySet{" +
                "Name='" + Name + '\'' +
                ", Shape='" + Shape + '\'' +
                ", points=" + points +
                '}';
    }
}
