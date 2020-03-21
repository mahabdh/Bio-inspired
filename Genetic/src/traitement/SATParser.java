package traitement;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import elements.Clause;
import elements.Variable;

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
    private Variable [] variables; // tableau des Variables de fichier
    private List<Clause> clauses= new ArrayList<Clause>(); // Ensemble des clauses de fichier
    
    
    
    // Constructer
    public SATParser(File file){
        this.fileToParse= file;
    }

	// Method parse
    public void parse() throws Exception {
    	
        BufferedReader bufferedReader= new BufferedReader(new FileReader(fileToParse));
        String line;
        
        /*First step reading line by line until the first clause of the file appears
        the first clause is marked by a space at begining " -7 -2 -37 0"   */     
        do line=bufferedReader.readLine();
        while(line!=null && line.charAt(0)!=' ');
     
        // Début Parsing 
        Variable [] clauseVariables; // les variables de la clause 
        HashMap<String, Variable> vars=new HashMap<String,Variable>();  
        
        do{
        	clauseVariables=new Variable[3];
            line= line.trim();// Remove white space from the line 
            String[] strings = line.split("[\t ]+"); // clause={"-55","47","54","0"}
            
            for (int i=0;i<strings.length;i++)
            {   
                if(strings[i].equals("0")) break; // Fin clause         
                clauseVariables[i]=new Variable(strings[i], false);
              
               
            } // end
            
            for(String str:strings) {
            	if(str.equals("0")) break;
            	boolean isnotnegation=str.charAt(0)!='-';
            	Variable v;
            	String var_name=isnotnegation ? str : str.substring(1);
            	if(vars.containsKey(var_name)) {
            		v=vars.get(var_name);
            	}else{
            		v=new Variable();
            		v.setNom(var_name);
            		vars.put(var_name,v);
            	}
            }
            
           //Ajouter clauseVariables à la clause
           Clause c=new Clause();
           c.setVariables(clauseVariables);
           //Ajouter la clause à l'ensemble des clauses 
           clauses.add(c);
  
           //Go to the next line
           line= bufferedReader.readLine();
         
           
        }while(line!=null && line.length()>0 && line.charAt(0)!='%'); // % indique line vide / fin du fichier 

        
        variables = vars.values().toArray(new Variable[]{});
        
        
           
        bufferedReader.close();
   
    }



	public Variable[] getVariables() {
		return variables;
	}

	public void setVariables(Variable[] variables) {
		this.variables = variables;
	}

	public List<Clause> getClauses() {
		return clauses;
	}

	public void setClauses(List<Clause> clauses) {
		this.clauses = clauses;
	}
    
	

	
}
