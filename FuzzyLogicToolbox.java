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
    public static void Fuzzification(ArrayList<Variable> inputVariables,ArrayList<Integer> crispValues){
        //each variable has n memberships (n->number of fuzzy sets in this variable)
        for(int i=0;i<inputVariables.size();i++){
            int inputCrispValue=crispValues.get(i);
            //in range with this variable
            if(inRangeVariable(inputVariables.get(i),inputCrispValue)){
                for(int j=0;j<inputVariables.get(i).getFuzzySets().size();j++){
                    //in range with this fuzzy set
                    if(inRangeFuzzSet(inputVariables.get(i).getFuzzySets().get(j),inputCrispValue)){
                        //if the crispValue is (point b at TRI or ranges from point b to point c at TRAP)
                        if(onPoint(inputVariables.get(i).getFuzzySets().get(j),inputCrispValue)){
                            inputVariables.get(i).getFuzzySets().get(j).setMembership(1);
                        }
                        else{
                          //it will be between the start and end points of fuzzy set so we need to calculate the slope
                           ArrayList<Point>points=new ArrayList<>();
                           points=inRangeBetweenPoints(inputVariables.get(i).getFuzzySets().get(j),inputCrispValue);
                           double slope=calculateSlope(points);
                           double intercept=calculateIntercept(points.get(0),slope);
                           double membership=calculateMembership(slope,inputCrispValue,intercept);
                            inputVariables.get(i).getFuzzySets().get(j).setMembership(membership);
                        }
                    }
                    else{
                        inputVariables.get(i).getFuzzySets().get(j).setMembership(0);
                    }
                }
            }
            //the crispValue is out of range in this variable
            else{
              for(int j=0;j<inputVariables.get(i).getFuzzySets().size();j++){
                  inputVariables.get(i).getFuzzySets().get(j).setMembership(0);
              }
            }
        }
    }

    public static double AND (double input1, double input2){
        if(input1<input2) return input1;
        return input2;
    }

    public static double OR (double input1, double input2){
        if(input1<input2) return input2;
        return input1;
    }

    public static double NOT (double input){
        return 1-input;
    }

    public static double AND_NOT (double input1, double input2){
        return AND(input1,NOT(input2));
    }

    public static double OR_NOT (double input1, double input2){
        return OR(input1,NOT(input2));
    }

    //handles only one operator
    public static double Inference(ArrayList<Variable> inputVariables, ArrayList<String> antecedents){
        String variableName1=antecedents.get(0);
        String fuzzySetName1=antecedents.get(1);
        String op=antecedents.get(2);
        String variableName2=antecedents.get(3);
        String fuzzySetName2=antecedents.get(4);
        double membership1=0;
        double membership2 = 0;
        for (int i=0;i<inputVariables.size();i++){
            if(inputVariables.get(i).Name.equals(variableName1)){
                for(int j=0;j<inputVariables.get(i).FuzzySets.size();j++){
                    if(inputVariables.get(i).FuzzySets.get(j).Name.equals(fuzzySetName1)){
                        membership1=inputVariables.get(i).FuzzySets.get(j).membership;
                    }
                }
            }

            if(inputVariables.get(i).Name.equals(variableName2)){
                for(int j=0;j<inputVariables.get(i).FuzzySets.size();j++){
                    if(inputVariables.get(i).FuzzySets.get(j).Name.equals(fuzzySetName2)){
                        membership2=inputVariables.get(i).FuzzySets.get(j).membership;
                    }
                }
            }

        }
        if(op.equals("and_not")) return AND_NOT(membership1,membership2);
        else if(op.equals("or_not")) return OR_NOT(membership1,membership2);
        else if(op.equals("and")) return AND(membership1,membership2);
        else if(op.equals("or")) return OR(membership1,membership2);
        return 0;
    }

    //we will make it return arraylist of strings when we have multiple output variable
    public static String getHighestMembershipName(ArrayList<Variable>outputVariables){
        int max=Integer.MIN_VALUE;
        String membershipName="";
        for(int i=0;i<outputVariables.size();i++) {
            for (int j = 0; j < outputVariables.size(); j++) {
                if(outputVariables.get(i).FuzzySets.get(j).membership>max){
                    outputVariables.get(i).FuzzySets.get(j).membership=max;
                    membershipName=outputVariables.get(i).FuzzySets.get(j).Name;
                }
            }
            max=Integer.MIN_VALUE;
        }
        return membershipName;
    }

    //we will make it return arraylist of doubles when we have multiple output variable
    public static double Defuzzification(ArrayList<Variable>outputVariables){
        double sum = 0;
        double down = 0;
            for(int i=0;i<outputVariables.size();i++) {
                for (int j = 0; j < outputVariables.size(); j++) {
                     sum = 0;
                     down = 0;
                    if (outputVariables.get(i).FuzzySets.get(j).Shape.equals("TRAP")) {
                        //its membership * its centroid (Where centroid is the point where membership is equal to 1)
                        sum += outputVariables.get(i).FuzzySets.get(j).membership * outputVariables.get(i).FuzzySets.get(j).points.get(1).x;
                    }
                    else{
                        sum += outputVariables.get(i).FuzzySets.get(j).membership * (outputVariables.get(i).FuzzySets.get(j).points.get(1).x+outputVariables.get(i).FuzzySets.get(j).points.get(2).x)/2;
                    }
                    down+= outputVariables.get(i).FuzzySets.get(j).membership;
                }
            }
           return sum/down;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);  // Create a Scanner object
        ArrayList<Variable> inputVariables = new ArrayList<>();
        ArrayList<Variable> outputVariables = new ArrayList<>();
        ArrayList<String> Rules=new ArrayList<>();
        boolean x = true;
        int counter=0;//to make sure that the system is ready to be simulated
        int variablesCount=0;//to make sure that every variable has its own fuzzy sets
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
                        //setting the name of the variable
                        variable.setName(variableParts[0]);
                        //setting the start of the variable
                        variable.setStart(Integer.parseInt(variableParts[2].substring(1, variableParts[2].length() - 1)));
                        //setting the end of the variable
                        variable.setEnd(Integer.parseInt(variableParts[3].substring(0, variableParts[3].length() - 1)));
                        if(variableParts[1].equals("IN")){
                            inputVariables.add(variable);
                        }
                        //"OUT" to handle multiple outputs
                        else{
                            outputVariables.add(variable);
                        }
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
                int IO=0;
                int index=0;
                //Enter the variable’s name
                System.out.println("Enter the variable's name:\n" +
                        "--------------------------");
                String variableName = sc.nextLine();
                for (int i = 0; i < inputVariables.size(); i++) {
                    if (variableName.equals(inputVariables.get(i).Name)) {
                        IO=1;
                        index=i;
                        check = 1;
                    }
                }
                for (int i = 0; i < outputVariables.size(); i++) {
                    if (variableName.equals(outputVariables.get(i).Name)) {
                        IO=0;
                        index=i;
                        check = 1;
                    }
                }

                System.out.println("Enter the fuzzy set name, type (TRI/TRAP) and values: (Press x to finish)\n" +
                        "-----------------------------------------------------");
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
                    else x = false;
                }
                x=true;
                if(IO==1) {
                    variablesCount++;
                    inputVariables.get(index).setFuzzySets(fuzzySets);
                }
                else{
                    variablesCount++;
                    outputVariables.get(index).setFuzzySets(fuzzySets);
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
                while(x) {
                    String ruleInput = sc.nextLine();
                    if (!ruleInput.equals("x")) {
                        Rules.add(ruleInput);
                    }
                    else x = false;
                }
                x=true;
                counter++;
            }

            // Run the simulation on crisp values.
            if(choice.equals("4")) {
                //to make sure that all variables have fuzzy sets and the rules is added
                if (variablesCount == inputVariables.size() + outputVariables.size()&& counter == 2) {
                    System.out.println("Enter the crisp values:\n" +
                            "-----------------------");
                    ArrayList<Integer>crispValues=new ArrayList<>();
                    for (Variable inputVariable : inputVariables) {
                        System.out.print(inputVariable.Name + ": ");
                        int crispValue = sc.nextInt();
                        crispValues.add(crispValue);
                    }
                    System.out.println("Running the simulation....\n");
                    Fuzzification(inputVariables,crispValues);
                    System.out.println("Fuzzification => done");
                    //inference
                    for(int i=0;i<Rules.size();i++){
                        String[] ruleParts = Rules.get(i).split(" ");
                        //consequents (after "=>")

                        String outputVariableName=ruleParts[ruleParts.length-2];
                        String outputFuzzySetName=ruleParts[ruleParts.length-1];
                        ArrayList<String> antecedents=new ArrayList<>();
                        for (String rulePart : ruleParts) {
                            if (!rulePart.equals("=>")) {
                                antecedents.add(rulePart);
                            } else
                                break;
                        }
                        for (Variable outputVariable : outputVariables) {
                            if (outputVariable.Name.equals(outputVariableName)) {
                                for (int j = 0; j < outputVariable.FuzzySets.size(); j++) {
                                    if (outputVariable.FuzzySets.get(j).Name.equals(outputFuzzySetName)) {
                                        if(outputVariable.FuzzySets.get(j).membership < Inference(inputVariables, antecedents)){
                                            outputVariable.FuzzySets.get(j).membership = Inference(inputVariables, antecedents);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    System.out.println("Inference => done");

                    System.out.println("Defuzzification => done");
                    System.out.print ("The predicted risk is "+getHighestMembershipName(outputVariables) +": "+Defuzzification(outputVariables)+'\n');

                }
                else{
                    System.out.println("CAN'T START THE SIMULATION! Please add the fuzzy sets and rules first.\n");
                }
            }

            //Close This System
            if(choice.equals("Close"))break;
        }
    }
}
 /*
proj_funding IN [0, 100]
exp_level IN [0, 60]
risk OUT [0, 100]
x


beginner TRI 0 15 30
intermediate TRI 15 30 45
expert TRI 30 60 60
x

very_low TRAP 0 0 10 30
low TRAP 10 30 40 60
medium TRAP 40 60 70 90
high TRAP 70 90 100 100
x


low TRI 0 25 50
normal TRI 25 50 75
high TRI 50 100 100
x
  */