
public class FuzzySet {
    public String Name;
    public String Type;

    public FuzzySet(String name, String type, int start, int end) {
        Name = name;
        Type = type;

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

}
