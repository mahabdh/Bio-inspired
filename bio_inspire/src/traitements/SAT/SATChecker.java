package traitements.SAT;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import elements.SAT.Clause;
import elements.SAT.Variable;


/**
 * Class SATChecker permet de vérifier la satisfiabilité de l'ensemble des clauses pour chaque fichier 
 * En utilisant les differents algorithmes : BFS /DFS/A* 
 * Elle retourne un boolean Vrai si les clauses sont satisfaites sinon Faux 
 * A la fin nous pouvons afficher l'ensemble des variables qui satisfont les clauses avec la méthode printUsedVariables()
 **/

public class SATChecker {
    private long timeLimit; //temps_limite
    private long startTime; //temps_debut
    private boolean timeExpired;//temps_expire
    private CheckMethod method; //method

    public enum CheckMethod{ //enumeration
        DFS, BFS, A_STAR,ACS
    }

    private Variable[] variables; // les variables à traiter (file variables)
    private Variable[] orderedVariable; //variables triés
    private HashSet<Clause> clauses; // Les clauses à considérer (file clauses) 325 clauses 
    private int depthLimit; //limite de profondeur (DFS algorithmes) 
    private int maxClausesSatisfied= 0; //nbr maximum de clauses satisfaites dans le fichier (max satisfied clauses in file)
    private int clausesCount; // nbr totale de clauses dans le fichier (nbr of clauses in the file)
    private Stack<StackVariable> variableStack; // une pile de variables (DFS algorithme)
    private int bfsLength;  //longueur de l'arbre (BFS) 
    HashMap<String, Variable> indexed_variables; // les variables du fichiers indexées 
    
    /* CONSTRUCTORS */
    public SATChecker(Variable[] variables, HashSet<Clause> clauses) {
        this.variables = variables;
        orderedVariable= new Variable[variables.length]; //les variables triés
        for (Variable variable : variables)
            orderedVariable[Integer.valueOf(variable.getVarName())-1]= variable; //index demarre de zéro 

        this.clauses = clauses;
        depthLimit = variables.length; //par defaut on parcourt jusqu'à la fin (tous les variables)
        clausesCount= clauses.size(); //nbr totale de clauses

        indexed_variables= new HashMap<>(); //liste des variables indexées <nom_variable,variable>
        for (Variable variable : variables)
        	indexed_variables.put(variable.getVarName(),variable); //contient <nom_variable,variable> 
    }

//Constructor used to set the depthLimit when creating the checker
 public SATChecker(Variable[] variables, HashSet<Clause> clauses, int depthLimit) {
        this.variables = variables;
        this.clauses = clauses;
        this.depthLimit = (depthLimit==-1 || depthLimit>variables.length ? variables.length : depthLimit);
        clausesCount= clauses.size();
 }


    /* HELPING METHODS */
    
    //Evaluer l'ensemble des clauses du fichier en utilisant les valeurs des variables actuel
    public boolean isSat() {
        //evaluer chaq clauses
        int c= 0;
        for (Clause clause : clauses) //pour chaq clause dans l'ensembles des clauses
            if (clause.evaluate_clause()) c++; // si la clause est satisfaite (clause.evaluate()==true) incrémenter c++
        // si c > nbr max clauses satisfaites  => Maj de maxClausesSatisfied
        if(c>maxClausesSatisfied) maxClausesSatisfied= c; 
        // si nbr totale des clauses dans le fichier egale nbr des clauses satisfaites alors TRUE sinon FALSE
        return c==clausesCount; 
    }

	//Meme méthode que isSat seulement MAJ des fréquences des variables (pour l'heuristique) ++ chance de la variable d'être choisie
	public boolean isSatStar() {
        //Evaluer chaq clause 
        int c= 0;
        for (Clause clause : clauses)
            if (clause.evaluate_clause())
				c++; 
			else
				clause.incrementVariablesChance();
			
        if(c>maxClausesSatisfied) maxClausesSatisfied= c;

        return c==clausesCount;
    }
	
	//Get le nbr des clauses satisfaites 
    public int getSatisfiedClausesCount(){
        int c=0;
        for (Clause clause : clauses)
            if(clause.evaluate_clause())c++;
        return c;
    }
    //Get le nbr max des clauses satisfaites 
    public int getMaxClausesSatisfied(){
        return maxClausesSatisfied;
    }

    /* CHECK METHODS {BFS, DFS, A*) */ 
 
    //BFS (Breadth First Search)
    public boolean bfsCheck() throws Exception {
       startTime = System.currentTimeMillis(); //temps début
       bfsLength=1;
        for(; bfsLength<=depthLimit && System.currentTimeMillis()- startTime < timeLimit; bfsLength++){
         //Début de la boucle
        	int valSwitch[]= new int[bfsLength]; //Tableau pour contrôler quand nous changeons la valeur d'une variable pour générer toutes les combinaisons
            int combinations= (int) Math.pow(2,bfsLength); //nbr de combinaison possible 

            for(int i=0; i<bfsLength; i++){
                valSwitch[i]=(int) Math.pow(2,bfsLength-i-1); //if we take 2 variable we have (11,10,01,00) we notice that each index changes its value by a frequency of 2^(length-index)
          
                variables[i].changeVal(true); //initial combination, all variables set to true
            }
            if(isSat()) return true; //evaluate first combination
            int beforeLast = bfsLength - 1;
            //trying combinations from left to right (if we see it as a tree)
            for(int j=1; j<combinations; j++){
                //constructing the current combination of variables's values
                if(combinations/2==j) variables[0].changeVal(false); //first variable change once from 1 (true) to false (0)
                int i = 1;
                for(; i< beforeLast; i++){
                    if (--valSwitch[i] == 0){
                        valSwitch[i]= (int) Math.pow(2,bfsLength-i-1);
                        variables[i].changeVal(!variables[i].eval());
                    }
                }
                variables[i].changeVal(!variables[i].eval());
                if(isSat()) return true;
            }
        }// Fin for 
        
        return false;
    }

    
//DFS (Depth first search)    
    public boolean dfsCheck() throws Exception {
        int depth = 0; //track the depth

        variableStack = new Stack<StackVariable>(); //variables stack

        //startTime with the first variable (randomly chosen), we explore this variable with true than with false (two main branches of the tree)
        variableStack.push(new StackVariable(true, depth));
        variableStack.push(new StackVariable(false, depth));

        startTime = System.currentTimeMillis();
        while (!variableStack.isEmpty() && System.currentTimeMillis()- startTime < timeLimit) {
            StackVariable variable = variableStack.pop(); //variable at the top is the variable to process
            variables[variable.depth].changeVal(variable.value); //it can be a leaf (or branch) with true or false value

            if (isSat()) return true;

            depth++;
            if (depth < depthLimit) { //we did't reach the bottom we continue with the next variable
                variableStack.push(new StackVariable(true, depth));
                variableStack.push(new StackVariable(false, depth));
            } else if(!variableStack.isEmpty()){
                depth = variableStack.peek().depth; //jump to next depth, (this is equivalent to recursive call end)
            }
        }

        //are combinations tried and no one satisfy the set so the set is not satisfied/or time elapsed
        return false;
    }

    private StackVariable bestChoice(int depth){
		int max=0;
		int index= 0;
        StackVariable stackVariable = new StackVariable(true, depth, index);

        for(int i=0; i<variables.length; i++){
			if(!variables[i].isChosen() && variables[i].getChance()>max){
				max= variables[i].getChance();
                stackVariable.index= i;
                stackVariable.value= variables[i].optimalValue();
			}
            variables[i].resetCounters();
		}

		variables[stackVariable.index].setChosen(true);
		return stackVariable;
	}

    
//A étoile  
    public boolean aStartCheck() throws Exception {
        int depth = 0; //track the depth
        variableStack = new Stack<StackVariable>(); //variables stack
        //startTime with the first variable , choice is done based on the number of clause satisfied by this variable
		StackVariable vToPush= bestChoice(0);
        variableStack.push(vToPush.getNegation());
        variableStack.push(vToPush);
        boolean reAddToChoiceList= false;
        startTime = System.currentTimeMillis();
        while (!variableStack.isEmpty() && System.currentTimeMillis()- startTime < timeLimit) {
            StackVariable variable = variableStack.pop(); //variable at the top is the variable to process
            variables[variable.index].changeVal(variable.value); //it can be a leaf (or branch) with true or false value
            if (isSatStar()) return true;
            if(reAddToChoiceList){
                variables[variable.index].setChosen(false);
                reAddToChoiceList= false;
            }
            depth++;
            if (depth < depthLimit) { //we did't reach the bottom we continue with the next variable
				vToPush= bestChoice(depth);
				variableStack.push(vToPush.getNegation());
				variableStack.push(vToPush);
            } else if(!variableStack.isEmpty()){
                depth = variableStack.peek().depth; //jump to next depth, (this is equivalent to recursive call end)
                reAddToChoiceList= true;
            }
        }
        //all combinations tried and no one satisfy the set so the set is not satisfiable/or time elapsed
        return false;
    }
    
    
    public static int ITERATIONS= 1000;



    //HELPING FUNCTIONS
    public boolean check(CheckMethod method, long timeLimit) throws Exception { 
        this.timeLimit = timeLimit; 
        this.method= method;
        boolean result;

        if(method==CheckMethod.A_STAR) result= aStartCheck();
        else if (method==CheckMethod.DFS) result=  dfsCheck();
        else if(method==CheckMethod.DFS) result= bfsCheck();
        else {
        	boolean[] booleans = acsCheck(a,b,q0,v);
            result= booleans!=null;
            if(result) {
                for (int i = 0; i < booleans.length; i++)
                    variables[i].changeVal(booleans[i]);
            }
        }	
        timeExpired = System.currentTimeMillis()-startTime>= this.timeLimit;
        return result;
    }

    // Affichage des variables qui satisfont l'ensemble de clauses
    public String printUsedVariables(){
        StringBuilder stringBuilder= new StringBuilder();
        if(method==CheckMethod.BFS || method==CheckMethod.DFS || method==CheckMethod.A_STAR || method==CheckMethod.ACS ){
            int i=0;
            for(; i<variables.length && !clauses.isEmpty(); i++){
                int satisfiedClauses= 0;
                Iterator<Clause> iterator= clauses.iterator();
                while (iterator.hasNext()){
                    if(iterator.next().isSatisfiedByVar(variables[i])) {
                        satisfiedClauses++;
                        iterator.remove();
                    }
                }
                if(satisfiedClauses==0) continue;
                stringBuilder.append(variables[i].getVal() ? "" : "-").append(variables[i].getVarName()).append(":(").append(satisfiedClauses).append(") ");
            }
        }
 
        return stringBuilder.toString();
    }

    public boolean isTimeExpired(){
        return timeExpired;
    }
    
    
    
    //Ant Colony System 
/****************************************************************************************************************************************************/   
    private int fitness(boolean[] solution){
        int c=0;
        for (Clause clause : clauses)
            if(clause.evalSolution(solution))
                c++;

        if(c>maxClausesSatisfied)
            maxClausesSatisfied = c;

        return c;
    }

    private boolean[] acsCheck(double alpha, double beta, double q0, double vaporation){
        double p0= 0.1; // valeur intiale de la phéromone
        double[][] pheromone= new double[variables.length][2];
        for (int i = 0; i < pheromone.length; i++)
            pheromone[i][0]=pheromone[i][1]= p0; //initialiser la phéromone de toutes les fourmis

        Ant[] antTable= new Ant[variables.length]; //nbr fourmis = nbr variables 

        //paramètres
        //double alpha=a, beta=b, vaporation=v;
        boolean[] bestGlobal= new boolean[variables.length];
        int bestGlobalQuality= fitness(bestGlobal);
        double[] bestGlobalPheromone= new double[variables.length];
        for (int i = 0; i < bestGlobal.length; i++) {
            bestGlobal[i] = ThreadLocalRandom.current().nextBoolean();
            bestGlobalPheromone[i]= p0;
        }

        Ant bestAnt= null;
        int bestAntQuality=0;

        int iteration= 0;
        while(iteration++<ITERATIONS){
            for (int i = 0; i < antTable.length; i++) {
                antTable[i]= new Ant(variables.length);

                double literalsWeight= 0;
                for (int j = 0; j < pheromone.length; j++) { //calculer niveau pheromone de chaque etat
                    literalsWeight+= Math.pow(pheromone[j][0], alpha)* Math.pow(orderedVariable[j].getNegationClauseCount(), beta);
                    literalsWeight+= Math.pow(pheromone[j][1], alpha)* Math.pow(orderedVariable[j].getClauseCount(), beta);
                }

                //construction de solution
                for (int j = 0; j < variables.length; j++) {
                    double q= ThreadLocalRandom.current().nextDouble(0, 1);

                    double variableWeight= Math.pow(pheromone[j][0], alpha)* Math.pow(orderedVariable[j].getNegationClauseCount(), beta);
                    variableWeight+= Math.pow(pheromone[j][1], alpha)* Math.pow(orderedVariable[j].getClauseCount(), beta);

                    //transition //regle proportionnelle pseudo-aléatoire
                    boolean selectedLit;
                    if(q<q0){
                        double arg0= Math.pow(pheromone[j][0], alpha)* Math.pow(orderedVariable[j].getNegationClauseCount(), beta);
                        double arg1= Math.pow(pheromone[j][1], alpha)* Math.pow(orderedVariable[j].getClauseCount(), beta);

                        selectedLit = arg1 > arg0;
                        antTable[i].setLiteral(j, selectedLit);
                    }else{
                        double variablePercentage= variableWeight/literalsWeight;

                        selectedLit = Math.abs(pheromone[j][1] - variablePercentage) > Math.abs(pheromone[j][0] - variablePercentage);
                        antTable[i].setLiteral(j, selectedLit);
                    }
                    
                    
                    //online update


                    literalsWeight-= variableWeight;
                }

                //ant quality
                int quality= fitness(antTable[i].solution);
                if(quality>bestAntQuality) {
                    if(quality==clauses.size())
                        return antTable[i].solution;
                    bestAntQuality = quality;
                    bestAnt= antTable[i];
                }

                for (int j = 0; j < antTable[i].solution.length; j++) {
                    int column = antTable[i].solution[j] ? 1 : 0;
                    pheromone[j][column]= (1-vaporation)*pheromone[j][column]+ (1/(325-quality));
                    antTable[i].setPhiromone(j, pheromone[j][column]);
                }


            }
            // update best solution

            if(bestAntQuality>bestGlobalQuality) {
                bestGlobalQuality = bestAntQuality;
                bestGlobal= bestAnt.solution;
                bestGlobalPheromone= bestAnt.solutionPheromone;
            }

            //offline update
            for (int j = 0; j < bestGlobal.length; j++){
                int column = bestGlobal[j] ? 1 : 0;
                pheromone[j][column]= (1-vaporation)*pheromone[j][column]+  1/(325-bestAntQuality);
                if(pheromone[j][column]<0.1) pheromone[j][column]=0.1;
            }
        }

        return bestGlobal;
    }


    public static double a=0.5,b=0.5,q0=0.7,v=0.1;
    
/***********************************************************************************************************************************************************************************/    
  
}

class StackVariable{
    StackVariable(boolean value, int depth) {
        this.value = value;
        this.depth = depth;
    }

	StackVariable(boolean value, int depth, int index) {
        this.value = value;
        this.depth = depth;
		this.index= index;
    }

	public StackVariable getNegation(){
		return new StackVariable(!value, depth, index);
	}
	
    boolean value;
    int depth;
	int index;
	
	
	
}

class Ant{
    public boolean[] solution;
    public double[] solutionPheromone;
    int hash= -1;
    int fitness;

    public Ant(int length){
        this.solution= new boolean[length];
        this.solutionPheromone= new double[length];
    }

    public void setPhiromone(int i, double val){
        solutionPheromone[i]= val;
    }

    public void setLiteral(int i, boolean val){
        solution[i]= val;
    }
}



