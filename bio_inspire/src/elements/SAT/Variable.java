package elements.SAT;


public class Variable {
    private static final boolean DEFAULT_VALUE = false;

    protected boolean val= DEFAULT_VALUE; // variable value (true /false) (1/0)  By default we set all variables to false
    protected String varName; // Variable name
    
    protected int clauseCount= 0; // nbr of clauses where the variable appears 
    protected int negationClauseCount= 0; // nbr of clauses where the negation of the variable (NotVar) appears
    private boolean chosen; // boolean indicate if the variable was chosen or not 

    //default constructor
    public Variable() {
    }
    //Constructor
    public Variable(String varName) {
        this.varName= varName;
    }

    //Get current value
    public boolean getVal() {
        return val;
    }

    //Evaluation
    // Change the value 
    public void changeVal(boolean newVal){
        val=newVal;
    }
    //evaluate the variable, (this method return !val in NotVariable class)
    public boolean eval(){
        return val;
    }

    //for Heuristics
    public void incrementChance(){  //Increment chance of variable
        clauseCount++;//nbr of clauses where the variable appears
    }
    public void resetCounters(){ // reset counters to zero 
	    clauseCount=0; //nbr of clauses where var appears 
	    negationClauseCount=0; // nbr of clauses where notvar appears 
    }
    //Calculate the chance of variable 
    public int getChance() { 
        if(clauseCount>=negationClauseCount) return clauseCount-negationClauseCount;
        return negationClauseCount-clauseCount;
    }

    //Does the optimal value is got from a variable or its negation
    public boolean optimalValue() {
        return clauseCount>negationClauseCount;
    }

    //Set a variable chosen 
    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }
    //check if the variable is chosen 
    public boolean isChosen() {
        return chosen;
    }

    //get nbr of clauses where the variable appears 
    public int getClauseCount(){
        return clauseCount;
    }
    // get nbr of clauses where not variable appears
    public int getNegationClauseCount(){
        return negationClauseCount;
    }
    // The frequence of variable (nbr of clauses where the variable appears) 
    public int frequence(){
        return clauseCount;
    }

    //affichage
    public String getVarName() {
        return varName;
    }
    // The variable type in case of 'Variable' => True if notVariable=False
    public boolean getType(){
        return true;
    }

    //performance
    @Override
    public int hashCode() {
        return varName.hashCode();
    }


}