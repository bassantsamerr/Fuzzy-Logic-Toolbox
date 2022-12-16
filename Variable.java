import java.util.ArrayList;

public class Variable {
    public String Name;
    public ArrayList<FuzzySet> FuzzySets;
    public int start;
    public int end;

    public Variable() {
    }

    public Variable(String name, ArrayList<FuzzySet> fuzzySets, int start, int end) {
        Name = name;
        FuzzySets = fuzzySets;
        this.start = start;
        this.end = end;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public ArrayList<FuzzySet> getFuzzySets() {
        return FuzzySets;
    }

    public void setFuzzySets(ArrayList<FuzzySet> fuzzySets) {
        FuzzySets = fuzzySets;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "Name='" + Name + '\'' +
                ", FuzzySets=" + FuzzySets +
                ", start=" + start +
                ", end=" + end +
                '}'+'\n';
    }
}
