package traitements.SAT;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

import elements.SAT.Clause;
import elements.SAT.NotVariable;
import elements.SAT.Variable;

/**
 * class: SATParser
 * is used the parse every file of the dataset
 * Parsing a file means read every line and convert it into a meaningful Type : Clause / Variable / NotVariable
 * every line is a clause which look like this : -55 47 54 0
 * -55 variable means notX55 
 * 0 means end of clause 
 * % or ' ' means end of file 
 */
public class SATParser {
	
    private File fileToParse;
    private Variable[] variables;// Contains file variables 
    private HashSet<Clause> clauses= new HashSet<Clause>();// Contains file clauses
    
    // Constructer
    public SATParser(File file){
        fileToParse= file;
    }

	// Method parse
    public void parse() throws Exception {
    	
        BufferedReader bufferedReader= new BufferedReader(new FileReader(fileToParse));
        String line;
        
        /*First step reading line by line until the first clause of the file appears
        the first clause is marked by a space at begining " -7 -2 -37 0"   */     
        do line=bufferedReader.readLine();
        while(line!=null && line.charAt(0)!=' ');

        
        // Verify if the File contains a valid structure
        // Which means : the first clause is not null and it begins by a space. 
        if(line==null || line.charAt(0)!=' ')
            throw new Exception("file does not contains valid structure : "+ fileToParse.getAbsolutePath());

        // Début Parsing 
        HashSet<Variable> clauseVariables;
        HashMap<String, Variable> indexed_variables = new HashMap<String, Variable>();// We save here all the variables of the file
        
        do{
            line= line.trim();// Remove white space from the line 
            String[] strings = line.split("[\t ]+");// clause={"-55","47","54","0"}
            if (strings.length==0)
                throw new Exception("file does not contains valid structure : "+ fileToParse.getAbsolutePath());

            clauseVariables= new HashSet<Variable>(); // Contains varaibles of the clause
            for (String string : strings)
            {
                if(string.equals("0")) // Fin de la clause
                    break;

                boolean isNotNegation = string.charAt(0) != '-';// True if "47" or false if "-55"
                
                String varName = isNotNegation ? string : string.substring(1);
                // if isNotNegation equals to true varName="47"
         	    // if isNotNegation equals to false which means that the variable is "-55" => varName=55
                
                Variable variable;
                if(indexed_variables.containsKey(varName))
                	variable= indexed_variables.get(varName);
                else{
                	variable= new Variable(varName);
                	indexed_variables.put(varName, variable);
                }

                if (!clauseVariables.contains(variable))// Verify that the variable doesn't appear twice
                {
                    Variable variableToAdd= isNotNegation ? variable : new NotVariable(variable);
                    clauseVariables.add(variableToAdd);
                    variableToAdd.incrementChance();
                }
            }//end for

            // Add the variables_clause into clause
            // Add the clause into clauses of the file
            if(clauseVariables.size()>0) clauses.add(new Clause(clauseVariables));
           
            //Go to the next line
            line= bufferedReader.readLine();
            
        }while(line!=null && line.length()>0 && line.charAt(0)!='%'); // % indique line vide / fin du fichier 

        // Add the variables from indexed_variables into variables tab (tableau des variables du fichier)
        variables = indexed_variables.values().toArray(new Variable[]{});
        bufferedReader.close();
    }
    
    // get variables of the file
    public Variable[] getVariables() {
        return variables;
    }
	// get clauses of the file
    public HashSet<Clause> getClauses() {
        return clauses;
    }
}
