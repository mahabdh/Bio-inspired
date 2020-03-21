package elements.SAT;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Clause {
    private HashSet<Variable> variables; // Variables of the clause
    private HashMap<Integer, Boolean> indexedVariables= new HashMap<>(); //indexed variables of the clause 
  
    //Constructor
    public Clause(HashSet<Variable> variables) {
        this.variables= variables;
        for (Variable variable : variables) {
            int i;
            i= Integer.parseInt((!variable.getType()?"-":"")+ variable.getVarName());
            //if !variable.getType() ==true means its notvariable ex: -45 
            //if !variable.getType() ==false means itsvariable ex: 45
            indexedVariables.put(Math.abs(i)-1, i>0); //index begins from zero 
        } 
    }

    //Methods
    
    //Check if the clause has a variable for example if c={"12","-45","16"} has the variable -45
    public boolean hasVar(Variable variable){
        for (Variable variable1 : variables)
            if(variable1.getVarName().equals(variable.getVarName()) && variable1.getType()==variable.getType())
                return true;
        return false;    
   }

    //Check if the clause is satisfied by a variable 
    //Example: c={"12","-14","15"}  variable=["12","14","15"] variables values=[true,false,true}
    // if var=12   satisfied the clause ?  Yes because same name && eval(12)=true
    // if var=14  satisfied the clause ?   Yes because same name && eval(-14)=-eval(14)=true
    public boolean isSatisfiedByVar(Variable variable){
        for (Variable variable1 : variables)
            if(variable1.getVarName().equals(variable.getVarName()) && variable1.eval())
                return true;
        return false;
    }

    //For the heuristic
    //Every variable that appears in the clause increments her chance => ++nbr of clauseCount
	public void incrementVariablesChance(){
		for (Variable variable : variables)
			variable.incrementChance(); 
	}

    //Evaluation
	//Evaluate a clause 
	// if one variable of the clause is true (var.eval==true) ==> clause satisfied 
    //Ilustration to explain more
/*    c= {-12,13,14} clause
    same as c= {notx12, x13, x14}	
    variables=[12,13,14]
    for example values=[true,false,true]
    evaluate the clause => for every variable of the clause 
    eval(-12)=> -eval(12)=> false  ====>
    eval(13) => false   ===============>clause Satisfied 
    eval(13)=> true ===================>
*/		    
    public boolean evaluate_clause(){
        for (Variable variable : variables) //For every variable of the clause
            if(variable.eval()) // variable.eval()==true so clause sat
                return true;
        return false;
    }
    // Evaluate if an array of boolean solution satisfy the clause 
    public boolean evalSolution(boolean[] solution){
        for (Map.Entry<Integer, Boolean> entry : indexedVariables.entrySet()) {
            if(solution[entry.getKey()]==entry.getValue())
                return true;
        }
        return false;
    }
    // Every variable that satisfy the clause increments her chance
    public void calculateSatisfyingVars(){
        for (Variable variable : variables)
            if(variable.eval())
                variable.incrementChance();
    }
    //Print the clause
    public String toString(){
        String r= "";
        for (Variable variable : variables) {
            r+= (variable instanceof NotVariable ? "-" : "")+variable.getVarName()+":"+variable.val+" ";
        }
        return r;
    }
   
}