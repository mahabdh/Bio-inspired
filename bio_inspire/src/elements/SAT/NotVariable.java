package elements.SAT;

public class NotVariable extends Variable {
    private Variable variable; //Variable

    //Constructor
    public NotVariable(String varName) {
        super(varName);
    }
    //Constructor
	public NotVariable(Variable variable){
		this.variable= variable;
		this.varName= variable.varName;
	}
    
	// getVal
    public boolean getVal() {
        return variable.getVal();
    }
    
    //frequence of the Notvariable : nbr of clauses where it appears
    public int frequence(){
        return variable.negationClauseCount;
    }

    // nbr of clauses where variable appears
    public int getClauseCount(){
        return variable.clauseCount;
    }
    //nbr of clauses where Notvariable appears
    public int getNegationClauseCount(){
        return variable.negationClauseCount;
    }

    //Evaluation
    @Override
    public boolean eval(){
        return !variable.val;
    }
    //Change Val of the not variable
    public void changeVal(boolean newVal){
        variable.val=newVal;
    }

    //Heuristic
    //increment the chance of NotVariable : ++ nbr of clause where it appears
    public void incrementChance(){
        variable.negationClauseCount++;
    }
    //Get the chance of the NotVariabe
    public int getChance() {
        if(variable.clauseCount>=variable.negationClauseCount) return variable.clauseCount;
        return variable.negationClauseCount;
    }
    //Reset the nbrs to zero
    public void resetCounters(){
        variable.clauseCount=0;
        variable.negationClauseCount=0;
    }
    //get optimal value
    public boolean optimalValue() {
        return variable.optimalValue();
    }
    //Get type of Notvariable
    //False in case of Variable the type will be true
    public boolean getType(){
        return false;
    }
}