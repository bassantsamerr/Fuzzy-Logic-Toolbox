import java.util.ArrayList;
import java.util.Scanner;

public class FuzzyLogicToolbox {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);  // Create a Scanner object
        ArrayList<Variable>variables=new ArrayList<>();
        Variable variable =new Variable();
        boolean x=true;
        //Adding Variables
        while(x) {
            String variableInput = sc.nextLine();
            if(variableInput!="x") {
                String[] variableParts = variableInput.split(" ");
                variable.setName(variableParts[0]);
                variable.setStart(Integer.parseInt(variableParts[2].substring(1, variableParts[2].length() - 1)));
                variable.setEnd(Integer.parseInt(variableParts[3].substring(0, variableParts[3].length() - 1)));
                variables.add(variable);
                System.out.println(variable.toString());
            }
            else{
                x=false;
            }
        }
        x=true;
        //Add fuzzy sets to an existing variable.


    }
}
