package programme;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import elements.Clause;
import elements.Variable;
import traitement.SATParser;
import traitement.SATReader;


public class GeneticAlgorithm {
	
	public static LinkedHashMap<List<Variable>,Integer> sorted_fitness_pop;
	public static LinkedHashMap<List<Variable>,Integer> fitness_pop;
	public static LinkedHashMap<List<Variable>,Integer> enfants;

	public static void main(String[] args) throws Exception{
		
	        
    // Read file of the dataset
    SATReader satReader = new SATReader();
    String [] path= {"C:\\Users\\PC\\Downloads\\Bio-Inspired\\Dataset\\uuf75-076.cnf"};
	ArrayList<File> fileArrayList = satReader.Start(path);
 
	// Pour chaq fichier de dataset appliquer genetic algorithm 
    for (File file : fileArrayList) { 
    	
    //Parsing	
    SATParser satParser = new SATParser(file);
    satParser.parse();
    List<Clause> clauses = satParser.getClauses(); // tableau de clauses de fichier
    Variable [] variables = satParser.getVariables(); //tableau de Variables de fichier    
    System.out.println("Parsing terminé (" + (variables.length) + " variable, " + clauses.size() + " clauses).");        
    
    //Afficher les variables
    afficher_variables(variables);    
    //Afficher les clauses 
    //afficher_clauses(clauses);
   
    long start = System.currentTimeMillis();
    
    // paramétres emiriques 
    int max_iter=100;
    int M=100;//taille de la population 
    double tm=0.4;//taux de mutation
    double tc=1;
    int point_croisement=35;  
    
    // Une liste de tableaux de variables pour stocker les solutions générées aléatoirement
    List<Variable[]> population =new ArrayList<>();
    
    // Initialisation de la population
    // génération aléatoire des solutions
    // Une poputlation est un ensemble de solutions 
    population=initialisation_population(population,variables,M);
     	    
    //Evaluation de la population
    //Calculer la fonction fitness pour chaque solution s de la population
    //Fitness : nbr de clause satisfaites
    //Contient chaque solution s de la population et sa fitness
	fitness_pop=new LinkedHashMap<List<Variable>,Integer>();
	Evaluation_population(fitness_pop,population,clauses);
    
    //Trier la population actuel selon fitness 
    sorted_fitness_pop = sortByValue(fitness_pop); 
    
    //Afficher population
    //afficher_population(sorted_fitness_pop);
    
    // Récupérer GSbest : la meilleur solution pour la population actuel (initial)
    List<Variable> GSbest=GetBestSolution(sorted_fitness_pop);  
    //afficher_GSBest(GSbest,sorted_fitness_pop);
    //fitness de GSbest
    int fgsbest=sorted_fitness_pop.get(GSbest);
    
    
    //Jusqu'à max_itérations (génération) faire
    for(int i=0;i<max_iter;i++){ 	 
		//Séléction des meilleurs parents pour le croisement (M/2) dans notre n=50
		LinkedHashMap<List<Variable>,Integer> parents; 
		//en utilisant la méthode ranking(classement selon fitness)
		parents=SelectionParentsMethodRanking(sorted_fitness_pop,M); 
		//afficher les parents 
		//afficher_population(parents);
		//afficher nbr des parents séléctionnés 
		//System.out.println(" Nbr des parents séléctionnés "+parents.size());
			
	    //parents_lists contient des tableaux de variables qui représentent les parents
		List<Variable[]> parents_lists=new ArrayList<>();
	    // enfants_lists contients des tableau de variables qui représentents les enfants
		List<Variable[]> enfants_lists=new ArrayList<>(); // initialement aucun enfants
		
		//remplir parents_list
		for (Map.Entry<List<Variable>, Integer> en : parents.entrySet()) {
			parents_lists.add(en.getKey().toArray(new Variable[en.getKey().size()]));
		}
	
	    //pour chaq paire p1,p2 de parents on va appliquer croisement et mutation		
        for(int j=0;j<parents_lists.size();j=j+2) {
        //croisement	
        Variable[] enfant1=croisement(parents_lists.get(j), parents_lists.get(j+1),tc, point_croisement);
        Variable[] enfant2=croisement(parents_lists.get(j+1), parents_lists.get(j),tc, point_croisement);     
        //mutation
        enfant1=mutation(enfant1,tm);
        enfant2=mutation(enfant2,tm);
        //ajouter chaq enfant à la liste de tous les enfants
        enfants_lists.add(enfant1);
        enfants_lists.add(enfant2);
        }//fin for parent		
	
        //Evaluation des enfants
        enfants=new LinkedHashMap<List<Variable>, Integer>();
        Evaluation_population(enfants,enfants_lists, clauses);     
        //afficher les enfants évalués 
        //afficher_population(enfants);
        
    	//Récupérer SBest (l'enfant qui a la plus grand fitness)
        List<Variable> SBest=GetBestSolution(enfants);
        int fsbest=enfants.get(SBest);
        //afficher_GSBest(SBest,enfants);
        //System.out.println(fsbest);
        //comparer GSbest avec SBest
        if(fsbest>fgsbest) {
        	fgsbest=fsbest;
        	GSbest=SBest;
        }    
        //Remplacement de la population
        population=remplacement(parents,enfants,sorted_fitness_pop,M);
        
    	}//Fin for pour tous les itérations 
   
    long elapsedTime = System.currentTimeMillis() - start;
     
    System.out.println("Taille population = "+population.size());
    System.out.println("Nbr itération = "+max_iter);
    System.out.println("Temps écoulé "+elapsedTime+" milliseconde");  
    System.out.println("GSbest la meilleur solution global est ");
    afficher(GSbest);
    System.out.println("FGbest = "+fgsbest);
    
    
    }//Fin for parcour des fichies 
    
}//Fin main method
	

   //Some Helping methods
	
	//Afficher les variables
	public static void afficher_variables(Variable[] variables) {
	    System.out.print("Variables :");
	    System.out.print("{");
	    for(int i=0;i<variables.length;i++) {
	    	System.out.print(variables[i].getNom());
	    	if(i==variables.length-1) continue;
	    	System.out.print(",");
	    	
	    }
	    System.out.print("}");
	    System.out.println("\n");
	}
	
	//Afficher les clauses 
	public static void afficher_clauses(List<Clause> clauses) {
		System.out.println("Les clauses");
	    for(Clause c:clauses) {
	        System.out.print("{");
	        for(int i=0;i<c.getVariables().length;i++) {
	        	System.out.print(c.getVariables()[i].getNom());
	        	if(i==c.getVariables().length-1) continue;
	        	System.out.print(",");
	        	
	        }
	        System.out.print("}");
	        System.out.println("\n");}
	    }
	
	//initialisation population
	public static List<Variable[]> initialisation_population(List<Variable[]> population,Variable[] variables, int M){
		int s=0; 
	    for(int i=0;i<M;i++) {
	    	//représentation d'une soluion comme vecteur de variables booléenes 
	    	Variable [] sol=new Variable[variables.length]; 
	        for(int j=0;j<sol.length;j++) {
	        	sol[j]=new Variable();
	        	sol[j].setNom(variables[j].getNom());	
	        }
	        s++;
	        String binaire=intToBinary (s, variables.length);
	        int k=0;
	        while(k<variables.length) {
	        	if(binaire.charAt(k)=='1')  sol[k].setValeur(true);
	        k++;
	        }
	        population.add(sol);	
	    	}//Fin pop

		return population;		
	}
	
	//Evaluation de la population
	public static void Evaluation_population(LinkedHashMap<List<Variable>,Integer> fitness_pop,List<Variable[]> population,List<Clause>clauses) {
        for(int i=0;i<population.size();i++) {
    	 int f=Fitness(population.get(i), clauses);
    	 List<Variable> c=new LinkedList<>();
    	 for(int j=0;j<population.get(i).length;j++) {
    		 c.add(population.get(i)[j]);
    	 }
    	 fitness_pop.put(c,f);
     }
	}
	
	//Afficher population 
	public static void afficher_population(LinkedHashMap<List<Variable>,Integer> pop) {
		
		System.out.print("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------Population--------------------------------------------------------------------------------------------------------------- | -----------Fitness----------");
	     System.out.println("\n");
	     for (Map.Entry<List<Variable>,Integer> en : pop.entrySet()) { 
	    	 List<Variable> l=en.getKey();
	    	 for(Variable v:l) {
	    		 System.out.print(" "+v.getNom()+"=>"+v.getValeur()+" ");
	    	 }	 
	         System.out.println("\t\t\t"+en.getValue());
	}
	}
	
    //nombre de bit dans notre cas est 75	 
    public static String intToBinary (int n, int numOfBits) {
		   String binary = "";
		   for(int i = 0; i < numOfBits; ++i, n/=2) {
		      switch (n % 2) {
		         case 0:
		            binary = "0" + binary;
		         break;
		         case 1:
		            binary = "1" + binary;
		         break;
		      }
		   }

		   return binary;
	}
	 
    //Fitness 
    public static int Fitness(Variable [] variables,List<Clause> clauses) {
       //evaluer chaq clauses
       int max_c= 0;
       for (Clause clause : clauses) //pour chaq clause dans l'ensembles des clauses 
    
       if (clause.Evaluer_Clause(variables)) max_c++; // si la clause est satisfaite (clause.isSatClause()==true) incrémenter max_c++
       return max_c;
   }

    // function to sort hashmap by values 
    public static LinkedHashMap<List<Variable>,Integer> sortByValue(LinkedHashMap<List<Variable>,Integer> hm) 
    { 
        // Create a list from elements of HashMap 
        List<Map.Entry<List<Variable>,Integer>> list = 
               new LinkedList<Map.Entry<List<Variable>,Integer> >(hm.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<List<Variable>,Integer> >() { 
            public int compare(Map.Entry<List<Variable>,Integer> o1,  
                               Map.Entry<List<Variable>,Integer> o2) 
            { 
                return (o2.getValue()).compareTo(o1.getValue()); 
            } 
        }); 
          
        // put data from sorted list to hashmap  
        LinkedHashMap<List<Variable>,Integer> temp = new LinkedHashMap<List<Variable>,Integer>(); 
        for (Map.Entry<List<Variable>,Integer> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        } 
        return temp; 
    }     
    
    //GetBestSolution
    public static List<Variable> GetBestSolution(LinkedHashMap<List<Variable>,Integer> sorted_map) {
    	List<Variable> best=null;
    	int max_fitness=0;
        for (Map.Entry<List<Variable>,Integer> en : sorted_map.entrySet()) { 
        List<Variable> v;
        v=en.getKey();
        if(sorted_map.get(v)>max_fitness) {
        	max_fitness=sorted_map.get(v);
        	best=new LinkedList<>();
        	for(Variable var:v) {
        		best.add(var);
        		
        	}
        }	
        }
        return best;
    }
    
    //afficher GSbest
    public static void afficher_GSBest(List<Variable> GSbest,LinkedHashMap<List<Variable>,Integer> sorted_fitness_pop){
    	System.out.println("\n The GSbest is ");
        for(Variable var:GSbest) {
       	 System.out.print(" "+var.getNom()+"=>"+var.getValeur()+" ");
        }
        System.out.println("\n f(GSbest)= "+sorted_fitness_pop.get(GSbest));
    }
    //Selection des parents depuis la population par la méthode ranking

    public static LinkedHashMap<List<Variable>,Integer>SelectionParentsMethodRanking(LinkedHashMap<List<Variable>,Integer> sorted_pop,int taille_pop){
            int n=taille_pop/2;
            
            if(n%2!=0) n=n+1;
    	    
            LinkedHashMap<List<Variable>,Integer> parents=new LinkedHashMap<List<Variable>,Integer>();
            
    	    int i=0;
    	    for (Map.Entry<List<Variable>, Integer> en : sorted_pop.entrySet()) {
    	    parents.put(en.getKey(), en.getValue());
    	    i++;
    	    if(i>=n) break;
    	    	
    	    }   	    
    	return parents;
    }
    
    //Croisement 
    public static Variable[] croisement(Variable[] p1,Variable[] p2,double tc,int point_croisement) {
    Variable[] res=new Variable[p1.length];
    
    for(int i=0;i<p1.length;i++) {
    	if(i<point_croisement) {
    		res[i]=p1[i];
    	}else {res[i]=p2[i];}
    }
    return res;
    
    }
    
    //mutation
    public static Variable[] mutation(Variable [] child,double tm) {

    	//nbr bit à muter
    	double length=(double)child.length;
    	double nbr_bit=Math.round(length*tm);
    	nbr_bit=(int)nbr_bit;
    	
    	for(int i=0;i<child.length;i++) {
    	if(i<nbr_bit) {
    		if(child[i].getValeur()==true) {
    			child[i].setValeur(false);
    		}else {
    			child[i].setValeur(true);
    		}
    	}	
    	}
    	return child;
    }
    
    //remplacement population
    public static List<Variable[]>remplacement(LinkedHashMap<List<Variable>, Integer> parents,LinkedHashMap<List<Variable>, Integer> enfants,LinkedHashMap<List<Variable>, Integer> sorted_fitness_pop,int M){
    	List<Variable[]> nvpop=new ArrayList<>();
    	
            
		//ajouter les 50 parents 
	    for (Map.Entry<List<Variable>, Integer> en : parents.entrySet()) {
			nvpop.add(en.getKey().toArray(new Variable[en.getKey().size()]));
	    }
    	  
    	//ajouter les 25 enfants nouveaux générés  
	    for (Map.Entry<List<Variable>, Integer> en : enfants.entrySet()) {
			nvpop.add(en.getKey().toArray(new Variable[en.getKey().size()]));
	    }
    	 
    	return nvpop;
    	
    	
    }
    
    public static  void afficher(List<Variable> l) {
    	for(Variable v : l) {
    		System.out.print(v.getNom()+"=>"+v.getValeur()+" ");
    	}
    	System.out.print("\n");
    } 
    
    
    
}// Fin class Main 	


