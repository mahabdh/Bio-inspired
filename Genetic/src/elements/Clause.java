package elements;

public class Clause {

	private Variable [] variables;

	public Clause() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Clause(Variable[] variables) {
		super();
		this.variables = variables;
	}

	public Variable[] getVariables() {
		return variables;
	}

	public void setVariables(Variable[] variables) {
		this.variables = variables;
	}

	// Fonction pour évaluer la satisfiabilité d'une clause
	// variables : l'ensemble de tous les variables
	// this.variables : les variables de la clause
	public boolean Evaluer_Clause(Variable [] variables) {
		
		for(int i=0;i<variables.length;i++) {
			for(int j=0;j<this.variables.length;j++) {
	            String var_name=this.variables[j].getNom();
	            boolean isnotnegation=this.variables[j].getNom().charAt(0)!='-';
	            var_name=isnotnegation ? var_name : var_name.substring(1);
	          
				if(variables[i].getNom().equals(var_name)) this.variables[j].setValeur(variables[i].getValeur());
			}
		}
		
	    for (Variable v:this.variables) {
	    	if(v.evaluateVariable()) return true;
	    }	    
	    return false;
	}


}
