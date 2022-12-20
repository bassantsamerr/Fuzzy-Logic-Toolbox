import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import java.util.InputMismatchException;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;

public class FuzzyLogicToolbox {
    static FileWriter myWriter;


    public FuzzyLogicToolbox() throws IOException {
    }

    //Fuzzification
    public static boolean inRangeVariable(Variable variable, int crispValue) {
        return crispValue > variable.start && crispValue < variable.end;
    }

    public static boolean inRangeFuzzSet(FuzzySet fuzzySet, int crispValue) {
        //in range with first and last point in this fuzzySet
        return crispValue > fuzzySet.points.get(0).x && crispValue < fuzzySet.points.get(fuzzySet.points.size() - 1).x;
    }

    public static boolean onPoint(FuzzySet fuzzySet, int crispValue) {
        if (fuzzySet.Shape.equals("TRI")) {
            if (fuzzySet.points.get(1).x == crispValue) {
                return true;
            }
        }
        if (fuzzySet.Shape.equals("TRAP")) {
            return crispValue >= fuzzySet.points.get(1).x && crispValue <= fuzzySet.points.get(2).x;
        }
        return false;
    }

    public static ArrayList<Point> inRangeBetweenPoints(FuzzySet fuzzySet, int crispValue) {
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 0; i < fuzzySet.points.size() - 1; i++) {
            if (crispValue > fuzzySet.points.get(i).x && crispValue < fuzzySet.points.get(i + 1).x) {
                points.add(fuzzySet.points.get(i));
                points.add(fuzzySet.points.get(i + 1));
            }
        }
        return points;
    }

    public static double calculateSlope(ArrayList<Point> points) {
        Point p1 = points.get(0);
        Point p2 = points.get(1);
        return (p2.y - p1.y) / (p2.x - p1.x);
    }

    public static double calculateIntercept(Point point, double slope) {
        return point.y - (slope * point.x);
    }

    public static double calculateMembership(double slope, int crispValue, double intercept) {
        return (slope * crispValue) + intercept;
    }

    public static void Fuzzification(ArrayList<Variable> inputVariables, ArrayList<Integer> crispValues) {
        //each variable has n memberships (n->number of fuzzy sets in this variable)
        for (int i = 0; i < inputVariables.size(); i++) {
            int inputCrispValue = crispValues.get(i);
            //in range with this variable
            if (inRangeVariable(inputVariables.get(i), inputCrispValue)) {
                for (int j = 0; j < inputVariables.get(i).getFuzzySets().size(); j++) {
                    //in range with this fuzzy set
                    if (inRangeFuzzSet(inputVariables.get(i).getFuzzySets().get(j), inputCrispValue)) {
                        //if the crispValue is (point b at TRI or ranges from point b to point c at TRAP)
                        if (onPoint(inputVariables.get(i).getFuzzySets().get(j), inputCrispValue)) {
                            inputVariables.get(i).getFuzzySets().get(j).setMembership(1);
                        } else {
                            //it will be between the start and end points of fuzzy set so we need to calculate the slope
                            ArrayList<Point> points = new ArrayList<>();
                            points = inRangeBetweenPoints(inputVariables.get(i).getFuzzySets().get(j), inputCrispValue);
                            double slope = calculateSlope(points);
                            double intercept = calculateIntercept(points.get(0), slope);
                            double membership = calculateMembership(slope, inputCrispValue, intercept);
                            inputVariables.get(i).getFuzzySets().get(j).setMembership(membership);
                        }
                    } else {
                        inputVariables.get(i).getFuzzySets().get(j).setMembership(0);
                    }
                }
            }
            //the crispValue is out of range in this variable
            else {
                for (int j = 0; j < inputVariables.get(i).getFuzzySets().size(); j++) {
                    inputVariables.get(i).getFuzzySets().get(j).setMembership(0);
                }
            }
        }
    }

    public static double AND(double input1, double input2) {
        if (input1 < input2) return input1;
        return input2;
    }

    public static double OR(double input1, double input2) {
        if (input1 < input2) return input2;
        return input1;
    }

    public static double NOT(double input) {
        return 1 - input;
    }

    public static double AND_NOT(double input1, double input2) {
        return AND(input1, NOT(input2));
    }

    public static double OR_NOT(double input1, double input2) {
        return OR(input1, NOT(input2));
    }

    //handles only one operator

    public static Vector<String> CalculateMemberShip(ArrayList<Variable> inputVariables, ArrayList<String> antecedents) {
        Vector<String> myVec = new Vector<>();
        double membership = 0.0;
        for (int k = 0; k < antecedents.size(); k += 3) {
            for (int i = 0; i < inputVariables.size(); i++) {
                if (inputVariables.get(i).Name.equals(antecedents.get(k))) {
                    for (int j = 0; j < inputVariables.get(i).FuzzySets.size(); j++) {
                        if (inputVariables.get(i).FuzzySets.get(j).Name.equals(antecedents.get(k + 1))) {
                            membership = inputVariables.get(i).FuzzySets.get(j).membership;
                            myVec.add(Double.toString(membership)); // x or y
                            if (k != antecedents.size() - 2)
                                myVec.add(antecedents.get(k + 2));
                        }
                    }
                }
            }
        }
        return myVec;
    }

    public static double evaluate(double membership1, String op, double membership2) {
        if (op.equals("and_not")) return AND_NOT(membership1, membership2);
        else if (op.equals("or_not")) return OR_NOT(membership1, membership2);
        else if (op.equals("and")) return AND(membership1, membership2);
        else if (op.equals("or")) return OR(membership1, membership2);
        return 0;
    }

    public static double Inference(Vector<String> myVec ) throws IOException {
        double temp = 0.0;
        for (int i = 0; i < myVec.size(); i++) {
            if (myVec.get(i).equals("and_not")) {
                temp = evaluate(Double.parseDouble(myVec.get(i - 1)), myVec.get(i), Double.parseDouble(myVec.get(i + 1)));
                myVec.set(i - 1, Double.toString(temp));
                myVec.remove(i);
                myVec.remove(i);
                i--;
            }
        }
        for (int i = 0; i < myVec.size(); i++) {
            if (myVec.get(i).equals("or_not")) {
                temp = evaluate(Double.parseDouble(myVec.get(i - 1)), myVec.get(i), Double.parseDouble(myVec.get(i + 1)));
                myVec.set(i - 1, Double.toString(temp));
                myVec.remove(i);
                myVec.remove(i);
                i--;
            }
        }

        for (int i = 0; i < myVec.size(); i++) {
            if (myVec.get(i).equals("and")) {
                temp = evaluate(Double.parseDouble(myVec.get(i - 1)), myVec.get(i), Double.parseDouble(myVec.get(i + 1)));
                myVec.set(i - 1, Double.toString(temp));
                myVec.remove(i);
                myVec.remove(i);
                i--;
            }
        }

        for (int i = 0; i < myVec.size(); i++) {
            if (myVec.get(i).equals("or")) {
                temp = evaluate(Double.parseDouble(myVec.get(i - 1)), myVec.get(i), Double.parseDouble(myVec.get(i + 1)));
                myVec.set(i - 1, Double.toString(temp));
                myVec.remove(i);
                myVec.remove(i);
                i--;
            }

        }

        return Double.parseDouble(myVec.get(0));
    }

    //we will make it return arraylist of strings when we have multiple output variable
    public static String getHighestMembershipName(ArrayList<Variable> outputVariables) {
        int max = Integer.MIN_VALUE;
        String membershipName = "";
        for (int i = 0; i < outputVariables.size(); i++) {
            for (int j = 0; j < outputVariables.get(i).FuzzySets.size(); j++) {
                if (outputVariables.get(i).FuzzySets.get(j).membership > max) {
                    max = (int) outputVariables.get(i).FuzzySets.get(j).membership;
                    membershipName = outputVariables.get(i).FuzzySets.get(j).Name;
                }

            }
            max = Integer.MIN_VALUE;
        }
        return membershipName;
    }

    //we will make it return arraylist of doubles when we have multiple output variable
    public static double Defuzzification(ArrayList<Variable> outputVariables) {
        double sum = 0;
        double down = 0;
        for (int i = 0; i < outputVariables.size(); i++) {
            for (int j = 0; j < outputVariables.size(); j++) {
                sum = 0;
                down = 0;
                if (outputVariables.get(i).FuzzySets.get(j).Shape.equals("TRAP")) {
                    //its membership * its centroid (Where centroid is the point where membership is equal to 1)
                    sum += outputVariables.get(i).FuzzySets.get(j).membership * outputVariables.get(i).FuzzySets.get(j).points.get(1).x;
                } else {
                    sum += outputVariables.get(i).FuzzySets.get(j).membership * (outputVariables.get(i).FuzzySets.get(j).points.get(1).x + outputVariables.get(i).FuzzySets.get(j).points.get(2).x) / 2;
                }
                down += outputVariables.get(i).FuzzySets.get(j).membership;
            }
        }
        return sum / down;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static void runProgram(String inputPath, String outputPath) throws IOException {
        File myObj = new File(inputPath);
        Scanner sc = new Scanner(myObj);  // Create a Scanner object
        ArrayList<Variable> inputVariables = new ArrayList<>();
        ArrayList<Variable> outputVariables = new ArrayList<>();
        ArrayList<String> Rules = new ArrayList<>();
        boolean x = true;
        int counter = 0;//to make sure that the system is ready to be simulated
        int variablesCount = 0;//to make sure that every variable has its own fuzzy sets

        try {
            myWriter = new FileWriter(outputPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            System.out.println("Main Menu:\n" +
                    "==========\n" +
                    "1- Add variables.\n" +
                    "2- Add fuzzy sets to an existing variable.\n" +
                    "3- Add rules.\n" +
                    "4- Run the simulation on crisp values.");

            String choice = "Close";
            if (sc.hasNextLine()) {
                choice = sc.nextLine();
                if (!isNumeric(choice)) {
                    System.out.println("wrong input");
                    choice = "Close";
                }
            }

            //Adding Variables
            if (choice.equals("1")) {
                System.out.println("Enter the variable's name, type (IN/OUT) and range ([lower, upper]):\n" +
                        "(Press x to finish)\n" +
                        "--------------------------------------------------------------------");

                while (x) {
                    Variable variable = new Variable();
                    String variableInput = sc.nextLine();
                    sc.hasNextLine();
                    if (!variableInput.equals("x")) {
                        String[] variableParts = variableInput.split(" ");
                        //setting the name of the variable
                        variable.setName(variableParts[0]);
                        //setting the start of the variable
                        variable.setStart(Integer.parseInt(variableParts[2].substring(1, variableParts[2].length() - 1)));
                        //setting the end of the variable
                        variable.setEnd(Integer.parseInt(variableParts[3].substring(0, variableParts[3].length() - 1)));
                        if (variableParts[1].equals("IN")) {
                            inputVariables.add(variable);
                        }
                        //"OUT" to handle multiple outputs
                        else {
                            outputVariables.add(variable);
                        }
                        System.out.println(variable);
                    } else {
                        x = false;
                    }
                }
                x = true;
                counter++;
            }
            //Add fuzzy sets to an existing variable.
            if (choice.equals("2")) {
                //check variable is exist
                int check = 0;
                int IO = 0;
                int index = 0;
                //Enter the variable’s name
                System.out.println("Enter the variable's name:\n" +
                        "--------------------------");
                String variableName = sc.nextLine();
                sc.hasNextLine();
                for (int i = 0; i < inputVariables.size(); i++) {
                    if (variableName.equals(inputVariables.get(i).Name)) {
                        IO = 1;
                        index = i;
                        check = 1;
                    }
                }
                for (int i = 0; i < outputVariables.size(); i++) {
                    if (variableName.equals(outputVariables.get(i).Name)) {
                        IO = 0;
                        index = i;
                        check = 1;
                    }
                }

                System.out.println("Enter the fuzzy set name, type (TRI/TRAP) and values: (Press x to finish)\n" +
                        "-----------------------------------------------------");

                ArrayList<FuzzySet> fuzzySets = new ArrayList<>();
                while (x) {
                    String fuzzySetInput = sc.nextLine();
                    sc.hasNextLine();
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
                    } else x = false;
                }
                x = true;
                if (IO == 1) {
                    variablesCount++;
                    inputVariables.get(index).setFuzzySets(fuzzySets);
                } else {
                    variablesCount++;
                    outputVariables.get(index).setFuzzySets(fuzzySets);
                }

                if (check == 0) {
                    System.out.println("Variable Not Found");
                    myWriter.write("Variable Not Found\n");
                }
            }

            // Add rules.

            if (choice.equals("3")) {
                System.out.println("Enter the rules in this format: (Press x to finish)\n" +
                        "IN_variable set operator IN_variable set => OUT_variable set\n" +
                        "------------------------------------------------------------");
//                myWriter.write("Enter the rules in this format: (Press x to finish)\n" +
//                        "IN_variable set operator IN_variable set => OUT_variable set\n" +
//                        "------------------------------------------------------------\n");
                while (x) {
                    String ruleInput = sc.nextLine();
                    sc.hasNextLine();
                    if (!ruleInput.equals("x")) {
                        Rules.add(ruleInput);
                    } else x = false;
                }
                x = true;
                counter++;
            }

            // Run the simulation on crisp values.
            if (choice.equals("4")) {
                //to make sure that all variables have fuzzy sets and the rules is added
                if (variablesCount == inputVariables.size() + outputVariables.size() && counter == 2) {
                    System.out.println("Enter the crisp values:\n" +
                            "-----------------------");
//                    myWriter.write("Enter the crisp values:\n" +
//                            "-----------------------");
                    ArrayList<Integer> crispValues = new ArrayList<>();
                    for (Variable inputVariable : inputVariables) {
                        System.out.print(inputVariable.Name + ": ");
                        int crispValue = sc.nextInt();
                        crispValues.add(crispValue);
                    }
                    System.out.println("Running the simulation....\n");
                    myWriter.write("Running the simulation....\n");
                    Fuzzification(inputVariables, crispValues);
                    System.out.println("Fuzzification => done");
                    myWriter.write("Fuzzification => done\n");
                    //inference
                    for (int i = 0; i < Rules.size(); i++) {
                        String[] ruleParts = Rules.get(i).split(" ");
                        //consequents (after "=>")
                        ArrayList<String> antecedents = new ArrayList<>();
                        ArrayList<String> consequents = new ArrayList<>();
                        boolean flag = true;
                        for (String rulePart : ruleParts) {
                            if (rulePart.equals("=>")) {
                                flag = false;
                            } else if (flag) {
                                antecedents.add(rulePart);
                            } else if (!flag) {
                                consequents.add(rulePart);
                            }
                        }
                        Vector<String> myVec = CalculateMemberShip(inputVariables, antecedents);
                        for (int k = 0; k < consequents.size() /2; k += 2) {
                            for (Variable outputVariable : outputVariables) {
                                if (outputVariable.Name.equals(consequents.get(k))) {
                                    for (int j = 0; j < outputVariable.FuzzySets.size(); j++) {
                                        if (outputVariable.FuzzySets.get(j).Name.equals(consequents.get(k + 1))) {
                                            if (outputVariable.FuzzySets.get(j).membership < Inference(myVec)) {
                                                outputVariable.FuzzySets.get(j).membership = Inference(myVec);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    System.out.println("Inference => done");
                    myWriter.write("Inference => done\n");
                    System.out.println("Defuzzification => done");
                    myWriter.write("Defuzzification => done\n");
                    System.out.print("The predicted risk is " + getHighestMembershipName(outputVariables) + ": " + Defuzzification(outputVariables) + '\n');
                    myWriter.write("The predicted risk is " + getHighestMembershipName(outputVariables) + ": " + Defuzzification(outputVariables) + '\n');
                } else {
                    System.out.println("CAN'T START THE SIMULATION! Please add the fuzzy sets and rules first.\n");
                    myWriter.write("CAN'T START THE SIMULATION! Please add the fuzzy sets and rules first.\n\n");
                }
            }

            //Close This System
            if (choice.equals("Close")) break;
        }
        myWriter.close();
    }

    public static void runProgram2() throws IOException {
        Scanner sc = new Scanner(System.in);  // Create a Scanner object
        ArrayList<Variable> inputVariables = new ArrayList<>();
        ArrayList<Variable> outputVariables = new ArrayList<>();
        ArrayList<String> Rules = new ArrayList<>();
        boolean x = true;
        int counter = 0;//to make sure that the system is ready to be simulated
        int variablesCount = 0;//to make sure that every variable has its own fuzzy sets

        while (true) {
            System.out.println("Main Menu:\n" +
                    "==========\n" +
                    "1- Add variables.\n" +
                    "2- Add fuzzy sets to an existing variable.\n" +
                    "3- Add rules.\n" +
                    "4- Run the simulation on crisp values.");
            String choice = sc.nextLine();
            //Adding Variables
            if (choice.equals("1")) {
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
                        if (variableParts[1].equals("IN")) {
                            inputVariables.add(variable);
                        }
                        //"OUT" to handle multiple outputs
                        else {
                            outputVariables.add(variable);
                        }
                        System.out.println(variable);
                    } else {
                        x = false;
                    }
                }
                x = true;
                counter++;
            }
            //Add fuzzy sets to an existing variable.
            if (choice.equals("2")) {
                //check variable is exist
                int check = 0;
                int IO = 0;
                int index = 0;
                //Enter the variable’s name
                System.out.println("Enter the variable's name:\n" +
                        "--------------------------");
                String variableName = sc.nextLine();
                for (int i = 0; i < inputVariables.size(); i++) {
                    if (variableName.equals(inputVariables.get(i).Name)) {
                        IO = 1;
                        index = i;
                        check = 1;
                    }
                }
                for (int i = 0; i < outputVariables.size(); i++) {
                    if (variableName.equals(outputVariables.get(i).Name)) {
                        IO = 0;
                        index = i;
                        check = 1;
                    }
                }

                System.out.println("Enter the fuzzy set name, type (TRI/TRAP) and values: (Press x to finish)\n" +
                        "-----------------------------------------------------");
                ArrayList<FuzzySet> fuzzySets = new ArrayList<>();
                while (x) {
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
                    } else x = false;
                }
                x = true;
                if (IO == 1) {
                    variablesCount++;
                    inputVariables.get(index).setFuzzySets(fuzzySets);
                } else {
                    variablesCount++;
                    outputVariables.get(index).setFuzzySets(fuzzySets);
                }

                if (check == 0) {
                    System.out.println("Variable Not Found");
                }
            }
            // Add rules.
            if (choice.equals("3")) {
                System.out.println("Enter the rules in this format: (Press x to finish)\n" +
                        "IN_variable set operator IN_variable set => OUT_variable set\n" +
                        "------------------------------------------------------------");
                while (x) {
                    String ruleInput = sc.nextLine();
                    if (!ruleInput.equals("x")) {
                        Rules.add(ruleInput);
                    } else x = false;
                }
                x = true;
                counter++;
            }

            // Run the simulation on crisp values.
            if (choice.equals("4")) {
                //to make sure that all variables have fuzzy sets and the rules is added
                if (variablesCount == inputVariables.size() + outputVariables.size() && counter == 2) {
                    System.out.println("Enter the crisp values:\n" +
                            "-----------------------");
                    ArrayList<Integer> crispValues = new ArrayList<>();
                    for (Variable inputVariable : inputVariables) {
                        System.out.print(inputVariable.Name + ": ");
                        int crispValue = sc.nextInt();
                        crispValues.add(crispValue);
                    }
                    System.out.println("Running the simulation....\n");
                    Fuzzification(inputVariables, crispValues);
                    System.out.println("Fuzzification => done");
                    //inference
                    for (int i = 0; i < Rules.size(); i++) {
                        String[] ruleParts = Rules.get(i).split(" ");

                        ArrayList<String> antecedents = new ArrayList<>();
                        ArrayList<String> consequents = new ArrayList<>();
                        boolean flag = true;
                        for (String rulePart : ruleParts) {
                            if (rulePart.equals("=>")) {
                                flag = false;
                            } else if (flag) {
                                antecedents.add(rulePart);
                            } else if (!flag) {
                                consequents.add(rulePart);
                            }
                        }
                        Vector<String> myVec = CalculateMemberShip(inputVariables, antecedents);
                        for (int k = 0; k < consequents.size()/2  ; k += 2) {
                            for (Variable outputVariable : outputVariables) {
                                if (outputVariable.Name.equals(consequents.get(k))) {
                                    for (int j = 0; j < outputVariable.FuzzySets.size(); j++) {
                                        if (outputVariable.FuzzySets.get(j).Name.equals(consequents.get(k + 1))) {
                                            if (outputVariable.FuzzySets.get(j).membership < Inference(myVec)) {
                                                outputVariable.FuzzySets.get(j).membership = Inference(myVec);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    System.out.println("Inference => done");
                    System.out.println("Defuzzification => done");
                    System.out.print("The predicted risk is " + getHighestMembershipName(outputVariables) + ": " + Defuzzification(outputVariables) + '\n');

                } else {
                    System.out.println("CAN'T START THE SIMULATION! Please add the fuzzy sets and rules first.\n");
                }
            }
            //Close This System
            if (choice.equals("Close")) break;
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int c2;
        //"D:\\FCAI-CU\\Level4\\softComputing\\assignments\\3\\Fuzzy-Logic-Toolbox\\input.txt";
        //"D:\\FCAI-CU\\Level4\\softComputing\\assignments\\3\\Fuzzy-Logic-Toolbox\\output.txt";
        String name="";
        ArrayList<String> description = new ArrayList<String>(); // Create an ArrayList object
        while (true) {
            System.out.println("Fuzzy Logic Toolbox\n" +
                    "===================\n" +
                    "1- Create a new fuzzy system\n" +
                    "2- Quit");
            c2 = scanner.nextInt();
            if (c2 == 1) {
                System.out.println("Enter the system's name and a brief description:\n" +
                        "------------------------------------------------");
                // Loop to store input values in nums array
                for (int i = 0; ; i++) {
                    name = scanner.next();
                    description.add(name) ;
                    if(description.get(i).contains("."))
                    {
                        break;
                    }
                }
                while(true)
                {System.out.println("1-GUI \n2-Reading from and writing to a file\n3-Reading from console \n4-Quit");
                    c2 = scanner.nextInt();

                    if (c2 == 1)  //GUI
                    {
                        gui g = new gui();

                    } else if (c2 == 2)  //reading from a console
                    {
                        System.out.println("please enter input file path");
                        String inputPath = scanner.next();
                        System.out.println("please enter output file path");
                        String outputPath = scanner.next();
                        runProgram(inputPath, outputPath);

                    }
                    else if (c2 == 3)  //reading input & output from a file
                    {
                        runProgram2();
                    }
                    else if (c2 == 4) //quit
                    {
                        break;
                    } else {
                        System.out.println("please enter a number from 1 or 2 or 3 or 5");
                    }}
            }
            else if (c2 == 2) {
                break;
            } else {
                System.out.println("please enter 1 or 2");
            }

        }
    }
}
//20190336
/*
1
proj_funding IN [0, 100]
exp_level IN [0, 60]
risk OUT [0, 100]
x
2
exp_level
beginner TRI 0 15 30
intermediate TRI 15 30 45
expert TRI 30 60 60
x
2
proj_funding
very_low TRAP 0 0 10 30
low TRAP 10 30 40 60
medium TRAP 40 60 70 90
high TRAP 70 90 100 100
x
2
risk
low TRI 0 25 50
normal TRI 25 50 75
high TRI 50 100 100
x
3
proj_funding high or exp_level expert => risk low
proj_funding medium and exp_level intermediate => risk normal
proj_funding medium and exp_level beginner => risk normal
proj_funding low and exp_level beginner => risk high
proj_funding very_low and_not exp_level expert => risk high
x
4
50
40
*/