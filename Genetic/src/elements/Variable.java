package elements;

public class Variable {

	private String nom;
	private boolean valeur; 

	public Variable() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Variable(String nom, boolean valeur) {
		super();
		this.nom = nom;
		this.valeur = valeur;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public boolean getValeur() {
		return valeur;
	}
	
	//Evaluer une variable 
	boolean evaluateVariable() {
		if(this.getNom().charAt(0)=='-') return !this.valeur;
		else return this.valeur;
	}
	
	
	
	public void setValeur(boolean valeur) {
		this.valeur = valeur;
	}
	
	
	
}
