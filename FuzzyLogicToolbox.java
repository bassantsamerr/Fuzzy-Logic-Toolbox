import java.util.ArrayList;
import java.util.Scanner;

public class FuzzyLogicToolbox {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);  // Create a Scanner object
        ArrayList<Variable> variables = new ArrayList<>();
        Variable variable = new Variable();
        boolean x = true;

        while (true) {
            String choice = sc.nextLine();
            if(choice.equals("Close"))break;
            System.out.println("Main Menu:\n" +
                    "==========\n" +
                    "1- Add variables.\n" +
                    "2- Add fuzzy sets to an existing variable.\n" +
                    "3- Add rules.\n" +
                    "4- Run the simulation on crisp values.\n");
            //Adding Variables
            if(choice.equals("1")) {
                while (x) {
                    String variableInput = sc.nextLine();
                    System.out.println(variableInput);
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
            }
            x = true;

            //Add fuzzy sets to an existing variable.
            if(choice.equals("2")) {
                //check variable is exist
                int check = 0;
                //Enter the variable’s name
                String variableName = sc.nextLine();
                for (int i = 0; i < variables.size(); i++) {
                    if (variableName == variables.get(i).Name) {
                        check = 1;
                        String fuzzySetInput = sc.nextLine();
                        System.out.println(fuzzySetInput);
                        while (!fuzzySetInput.equals("x")) {
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
                            }
                        }
                    }
                }
                if (check == 0) {
                    System.out.println("Variable Not Found");
                }
            }
        }
    }
}