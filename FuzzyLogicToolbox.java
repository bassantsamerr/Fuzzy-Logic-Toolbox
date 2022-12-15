import java.util.ArrayList;
import java.util.Scanner;

public class FuzzyLogicToolbox {
    //Fuzzification
    public static boolean inRangeVariable(Variable variable,int crispValue){
        if(crispValue>variable.start&&crispValue<variable.end)return true;
        return false;
    }
    public static boolean inRangeFuzzSet(FuzzySet fuzzySet,int crispValue){
        //in range with first and last point in this fuzzySet
        if(crispValue>fuzzySet.points.get(0).x&&crispValue<fuzzySet.points.get(fuzzySet.points.size()-1).x)return true;
        return false;
    }
    public static boolean onPoint(FuzzySet fuzzySet,int crispValue){
        if(fuzzySet.Shape.equals("TRI")){
            if(fuzzySet.points.get(1).x==crispValue){
                return true;
            }
        }
        if(fuzzySet.Shape.equals("TRAP")){
            if(crispValue>=fuzzySet.points.get(1).x&&crispValue<=fuzzySet.points.get(2).x){
                return true;
            }
        }
        return false;
    }
    public static ArrayList<Point>  inRangeBetweenPoints(FuzzySet fuzzySet,int crispValue){
        ArrayList<Point>points=new ArrayList<>();
        for(int i=0;i<fuzzySet.points.size()-1;i++){
            if(crispValue>fuzzySet.points.get(i).x&&crispValue<fuzzySet.points.get(i+1).x){
                points.add(fuzzySet.points.get(i));
                points.add(fuzzySet.points.get(i+1));
            }
        }
        return points;
    }
    public static double calculateSlope (ArrayList<Point> points ){
        Point p1=points.get(0);
        Point p2=points.get(1);
        return (p2.y-p1.y)/(p2.x-p1.x);
    }

    public static double calculateIntercept(Point point,double slope){
        return point.y - (slope * point.x);
    }

    public static double calculateMembership(double slope, int crispValue, double intercept){
        return (slope * crispValue)+ intercept;
    }
    public static void Fuzzification(ArrayList<Variable> variables,ArrayList<Integer> crispValues){
        //each variable has n memberships (n->number of fuzzy sets in this variable)
        for(int i=0;i<variables.size()-1;i++){
            int inputCrispValue=crispValues.get(i);
            //in range with this variable
            if(inRangeVariable(variables.get(i),inputCrispValue)){
                for(int j=0;j<variables.get(i).getFuzzySets().size();j++){
                    //in range with this fuzzy set
                    if(inRangeFuzzSet(variables.get(i).getFuzzySets().get(j),inputCrispValue)){
                        //if the crispValue is (point b at TRI or ranges from point b to point c at TRAP)
                        if(onPoint(variables.get(i).getFuzzySets().get(j),inputCrispValue)){
                            variables.get(i).getFuzzySets().get(j).setMembership(1);
                        }
                        else{
                          //it will be between the start and end points of fuzzy set so we need to calculate the slope
                           ArrayList<Point>points=new ArrayList<>();
                           points=inRangeBetweenPoints(variables.get(i).getFuzzySets().get(j),inputCrispValue);
                           double slope=calculateSlope(points);
                           double intercept=calculateIntercept(points.get(0),slope);
                           double membership=calculateMembership(slope,inputCrispValue,intercept);
                            variables.get(i).getFuzzySets().get(j).setMembership(membership);
                        }

                    }
                    else{
                        variables.get(i).getFuzzySets().get(j).setMembership(0);
                    }
                }
            }
            //the crispValue is out of range in this variable
            else{
              for(int j=0;j<variables.get(i).getFuzzySets().size();j++){
                  variables.get(i).getFuzzySets().get(j).setMembership(0);
              }
            }
        }
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);  // Create a Scanner object
        ArrayList<Variable> variables = new ArrayList<>();
        boolean x = true;
        int counter=0;
        int variablesCount=0;
        while (true) {
            System.out.println("Main Menu:\n" +
                    "==========\n" +
                    "1- Add variables.\n" +
                    "2- Add fuzzy sets to an existing variable.\n" +
                    "3- Add rules.\n" +
                    "4- Run the simulation on crisp values.");
            String choice = sc.nextLine();
            //Adding Variables
            if(choice.equals("1")) {
                System.out.println("Enter the variable's name, type (IN/OUT) and range ([lower, upper]):\n" +
                        "(Press x to finish)\n" +
                        "--------------------------------------------------------------------");
                while (x) {
                    Variable variable = new Variable();
                    String variableInput = sc.nextLine();
                    if (!variableInput.equals("x")) {
                        String[] variableParts = variableInput.split(" ");
                        variable.setName(variableParts[0]);
                        variable.setStart(Integer.parseInt(variableParts[2].substring(1, variableParts[2].length() - 1)));
                        variable.setEnd(Integer.parseInt(variableParts[3].substring(0, variableParts[3].length() - 1)));
                        variables.add(variable);
                        System.out.println(variable.toString());
                    } else {
                        x = false;
                    }
                }
                x = true;
                counter++;
            }

            //Add fuzzy sets to an existing variable.
            if(choice.equals("2")) {
                //check variable is exist
                int check = 0;
                //Enter the variable’s name
                System.out.println("Enter the variable's name:\n" +
                        "--------------------------");
                String variableName = sc.nextLine();
                for (int i = 0; i < variables.size(); i++) {
                    if (variableName.equals(variables.get(i).Name)) {
                        System.out.println("Enter the fuzzy set name, type (TRI/TRAP) and values: (Press x to finish)\n" +
                                "-----------------------------------------------------");
                        check = 1;
                        ArrayList<FuzzySet> fuzzySets=new ArrayList<>();
                        while(x) {
                            String fuzzySetInput = sc.nextLine();
                            if (!fuzzySetInput.equals("x")) {
                                FuzzySet fuzzySet = new FuzzySet();
                                String[] fuzzySetParts = fuzzySetInput.split(" ");
                                fuzzySet.setName(fuzzySetParts[0]);
                                //its shape is triangle
                                if (fuzzySetParts[1].equals("TRI")) {
                                    fuzzySet.setShape(fuzzySetParts[1]);
                                    Point a = new Point(Integer.parseInt(fuzzySetParts[2]), 0);
                                    Point b = new Point(Integer.parseInt(fuzzySetParts[3]), 1);
                                    Point c = new Point(Integer.parseInt(fuzzySetParts[4]), 0);
                                    ArrayList<Point> points = new ArrayList<>();
                                    points.add(a);
                                    points.add(b);
                                    points.add(c);
                                    fuzzySet.setPoints(points);
                                    fuzzySets.add(fuzzySet);
                                }
                                //its shape is trap
                                else {
                                    fuzzySet.setShape(fuzzySetParts[1]);
                                    Point a = new Point(Integer.parseInt(fuzzySetParts[2]), 0);
                                    Point b = new Point(Integer.parseInt(fuzzySetParts[3]), 1);
                                    Point c = new Point(Integer.parseInt(fuzzySetParts[4]), 1);
                                    Point d = new Point(Integer.parseInt(fuzzySetParts[5]), 0);
                                    ArrayList<Point> points = new ArrayList<>();
                                    points.add(a);
                                    points.add(b);
                                    points.add(c);
                                    points.add(d);
                                    fuzzySet.setPoints(points);
                                    fuzzySets.add(fuzzySet);
                                }
                            }
                            else{
                                x=false;
                            }
                        }
                        x=true;
                        variablesCount++;
                        variables.get(i).setFuzzySets(fuzzySets);
                        System.out.println(variables.get(i).toString());
                    }
                }
                if (check == 0) {
                    System.out.println("Variable Not Found");
                }
            }

            // Add rules.
            if(choice.equals("3")) {
                System.out.println("Enter the rules in this format: (Press x to finish)\n" +
                        "IN_variable set operator IN_variable set => OUT_variable set\n" +
                        "------------------------------------------------------------");
                counter++;
            }


            // Run the simulation on crisp values.
            if(choice.equals("4")) {
                //to make sure that all variables have fuzzy sets and the rules is added
                if (variablesCount == variables.size() && counter == 2) {
                    System.out.println("Enter the crisp values:\n" +
                            "-----------------------");
                    ArrayList<Integer>crispValues=new ArrayList<>();
                    for(int i=0;i<variables.size()-1;i++) {
                        System.out.print(variables.get(i).Name + ": ");
                        int crispValue = sc.nextInt();
                        crispValues.add(crispValue);
                    }
                    Fuzzification(variables,crispValues);
                }
                else{
                    System.out.println("CAN’T START THE SIMULATION! Please add the fuzzy sets and rules first.\n");
                }
            }

            //Close This System
            if(choice.equals("Close"))break;
        }
    }
}
//    proj_funding IN [0, 100]
//        exp_level IN [0, 60]
//        risk OUT [0, 100]
//        x


//    beginner TRI 0 15 30
//        intermediate TRI 15 30 45
//        expert TRI 30 60 60

//    very_low TRAP 0 0 10 30
//        low TRAP 10 30 40 60
//        medium TRAP 40 60 70 90
//        high TRAP 70 90 100 100