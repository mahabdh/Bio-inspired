package main.programme;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import elements.SAT.Clause;
import elements.SAT.Variable;
import traitements.SAT.SATChecker;
import traitements.SAT.SATParser;
import traitements.SAT.SATReader;

public class Main {
    static int timeLimit = 1000*300; //Le temps limite(max) pour faire verification satisfiabilité

	 public static void main(String[] args) throws Exception{
		// Création des répertoires pour sauvegarder des résultats
	    	// BFS Recherche par largeur d'abord 
	        File bfs = new File("BFS");
	        if (bfs.exists() || !bfs.isDirectory())
	            bfs.mkdir();
	        // DFS Recherche en profondeur d'abord
	        File dfs = new File("DFS");
	        if (dfs.exists() || !dfs.isDirectory())
	            dfs.mkdir();
	        // Algorithme A*
	        File astar = new File("A_STAR");
	        if (astar.exists() || !astar.isDirectory())
	            astar.mkdir();
	        //Ant colony system
	        File acs = new File("ACS");
	        if (acs.exists() || !acs.isDirectory())
	            acs.mkdir();


	        
	        // Read files of the dataset
	        SATReader satReader = new SATReader();
	        ArrayList<File> fileArrayList = satReader.Start(args);

	        Scanner scanner= new Scanner(System.in);

	        sop("Veuillez choisir l'algorithme que vous voulez utiliser pour la vérification de la satisfiabilité ");
	        sop("1 - BFS : Recherche en largeur d'abord");
	        sop("2 - DFS : Recherche en profondeur d'abord");
	        sop("3 - A* : A Etoile, Avec N° de clause satisfaites comme heuristique");
	        sop("4 - ACS : Ant Colony System");
	        
	        int choice= scanner.nextInt();
	        while (choice>4|| choice<1){
	            sop("Veuillez entrer un choix valide (1 jusqu'à 4)");
	            choice= scanner.nextInt();
	        }
	        sop("Le délai est de 5 minutes");
	        String time = scanner.nextLine();
	        time= time.trim();
	        try{
	            int convertedTime= Integer.valueOf(time);
	            if(convertedTime>0)
	                timeLimit = convertedTime;
	        }catch(Exception ignored){}
	        
	        
	        switch(choice){
	            case 1 : checkBlindlyOrUsingHeuristic(fileArrayList,"BFS", SATChecker.CheckMethod.BFS);
	                break;
	            case 2 : checkBlindlyOrUsingHeuristic(fileArrayList,"DFS", SATChecker.CheckMethod.DFS);
	                break;
	            case 3 : checkBlindlyOrUsingHeuristic(fileArrayList,"A_STAR", SATChecker.CheckMethod.A_STAR);
	                break;
	            case 4 : checkACS(fileArrayList,"ACS",SATChecker.CheckMethod.ACS);
                break;     
	        }
		
	
}//Fin main method
	
	 //Methodes
	 
	    private static int checkBlindlyOrUsingHeuristic(ArrayList<File> fileArrayList, String method, SATChecker.CheckMethod checkMethod) throws IOException {
	        //Creation du fichier pour sauvegarder les résultats 
	    	String fileName= method+"/result1.txt";
	        File resultFile = new File(fileName);
	        int i=2;
	        while(resultFile.exists()){ //Ne pas effacer les anciens fichiers, ils peuvent être utiles
	            resultFile = new File(method+"/result"+i+".txt");
	            i++;
	        }
	        BufferedWriter outputWriter= new BufferedWriter(new FileWriter(resultFile)); //résultat pour écrire dans le fichier 
	        String instanceLine ="", clausesSatLine="", timeLine="";

	        double wholeTime= 0; //Temps global du traitement de tous les fichiers 
	        int satisfiedClauses=0; //nbr de clauses satisfaites au début 0
	        int instanceNumber=0; //nbr des instances représentent les fichiers à traiter (Files of dataset)
	        
	        // Parcourir les fichiets du dataset 
	        for (File file : fileArrayList) { // Pour chaq fichier faire le traitement 
	            System.out.println("\n\n\nTraitement du fichier : " + file.getAbsolutePath());
	            
	            //1- Parsing du fichier
	            System.out.println("\t-Parsing de fichier ...");
	            SATParser satParser = new SATParser(file);
	            try {
	                satParser.parse();
	            } catch (Exception e) {
	                System.out.println("\t^Erreur pendent le parsing du fichier " + file.getName());
	                System.out.println("\t\t" + e.getMessage());
	                continue;
	            }
	            Variable[] variables = satParser.getVariables(); //Variables parsed from the file
	            HashSet<Clause> clauses = satParser.getClauses(); //Clauses parsed from the file
	            System.out.println("\t-parsing terminé (" + variables.length + " variable, " + clauses.size() + " clauses).");     
	            
	            //2- Vérification de la satisfiabilité 
	            int depthlimit=variables.length; //Limite de la profondeur de l'arbre
	            SATChecker satChecker=new  SATChecker(variables,clauses,depthlimit);
	            long start = System.currentTimeMillis(); // démarrer le calcul du temps de traitement 
	            boolean satisfiable = false; 
	            try {
	                System.out.println("\t-Vérification de la satisfiabilité en utilisant la méthode  (" + method + ")...");
	                satisfiable = satChecker.check(checkMethod, timeLimit);
	            } catch (Exception e) {
	                System.out.println("\t\t*Exception !! : " + e.getMessage());
	                e.printStackTrace();
	            }
	            long elapsedTime = System.currentTimeMillis() - start;
	            System.out.println("\t-Vérification terminée, temps passé : " + elapsedTime + " millis");
	            wholeTime += elapsedTime;

	            //3- Afficher les résultats sur la console et dans les fichiers 
	            String usedVariables = satChecker.printUsedVariables(); //Les variables utilisés pendant le traitement 
	                                                                    //qui satisfont les clauses 
	            satisfiedClauses+=satChecker.getMaxClausesSatisfied();
	            instanceNumber++;
	            if (satisfiable) {
	                System.out.println("\t-L'ensemble des clauses est satisfiables, valeurs des variables et le nombre des clauses satisfaites par chaque variable : ");
	                System.out.print("\t\t" + usedVariables);
	            } else {
	                System.out.println("\t" + "Aucune solution trouvée" + (satChecker.isTimeExpired() ? "(temps expiré)" : "(L'ensemble des clauses est insatisfiable)"));
	                System.out.println("\t-Le nbr max des clauses satisfaites pendant le traitement est " + satChecker.getMaxClausesSatisfied());
	                if (satChecker.isTimeExpired())
	                    System.out.println("\t-Le nombre de clauses satisfaites par chaque variable lorsque le programme est arrêté est : ");
	                System.out.print("\t\t" + usedVariables);
	            }

	            instanceLine+= "Les fichiers traités \n"+file.getName()+"\t";
	            clausesSatLine+= "Le nbr max des clauses satisfaites\n"+satChecker.getMaxClausesSatisfied()+"\t";
	            timeLine+="Temps passé\n "+ elapsedTime+"\t";

	            System.out.println("\n");
	        }

	        outputWriter.write(instanceLine);
	        outputWriter.newLine();
	        outputWriter.write(clausesSatLine);
	        outputWriter.newLine();
	        outputWriter.write(timeLine);

	        System.out.println("Temps globale de traitement  (millis) : "+wholeTime);
	        outputWriter.write("Temps globale de traitement : "+wholeTime);
	        outputWriter.close(); //Fermer le fichier resultat

	        return satisfiedClauses/(instanceNumber>0?instanceNumber:1);
	    }
 
	    
	//Ant Colony System 
	    private static int checkACS(ArrayList<File> fileArrayList,String method, SATChecker.CheckMethod checkMethod) throws IOException {
	        String fileName= "ACS/result1.txt";
	        File resultFile = new File(fileName);
	        int i=2;
	        while(resultFile.exists()){ //don't erase old files, they may be useful
	            resultFile = new File("ACS/result"+i+".txt");
	            i++;
	        }
	        BufferedWriter outputWriter= new BufferedWriter(new FileWriter(resultFile)); //result
	        String instanceLine ="", clausesLine="", timeLine="";

	        double a=0; double b=0;  double q0=0;  double v=0;
	        sop("Voulez-vous changer les paramètres de l'algorithme (y/Y pour Yes) ?");

	        final Scanner scanner = new Scanner(System.in);
	        char changeParam= 'n';
	        String s = scanner.nextLine();
	        if(s.length()>0){
	            changeParam= s.charAt(0);
	        }

	        if(changeParam=='Y' || changeParam=='y'){
	            sop("Veuillez entrer les paramètres souhaités comme suit : alpha, beta, q0 ensuite taux d'évaporation");
	            a= scanner.nextDouble();
	            b= scanner.nextDouble();
	            q0= scanner.nextDouble();
	            v= scanner.nextDouble();
	        }

	        if(a>=0) SATChecker.a=a;
	        if(b>=0) SATChecker.b=b;
	        if(q0>=0) SATChecker.q0=q0;
	        if(v>=0) SATChecker.v=v;

	        double wholeTime= 0;
	        int satisfiedClauses=0;
	        int instanceNumber=0;
	        for (File file : fileArrayList) {
	            System.out.println("\n\n\nProcessing file : " + file.getAbsolutePath());

	            //1- parse the file
	            System.out.println("\t-parsing file ...");
	            SATParser satParser = new SATParser(file);
	            try {
	                satParser.parse();
	            } catch (Exception e) {
	                System.out.println("\t^error parsing file file : " + file.getName());
	                System.out.println("\t\t" + e.getMessage());
	                continue;
	            }
	            Variable[] variables = satParser.getVariables(); //variables parsed
	            HashSet<Clause> clauses = satParser.getClauses(); //clauses parsed
	            System.out.println("\t-parsing terminé (" + variables.length + " variable, " + clauses.size() + " clauses).");

	            //2- check satisfiability
	            SATChecker satChecker = new SATChecker(variables, clauses);
	            long start = System.currentTimeMillis();
	            boolean satisfiable = false;
	            try {
	                System.out.println("\t-Vérification de la satisfiabilité en utilisant la méthode  (" + method + ")...");
	                satisfiable = satChecker.check(SATChecker.CheckMethod.ACS, timeLimit);
	            } catch (Exception e) {
	                System.out.println("\t\t*exception thrown : " + e.getMessage());
	                e.printStackTrace();
	            }
	            long elapsedTime = System.currentTimeMillis() - start;
	            System.out.println("\t-Vérification terminée, temps passé : " + elapsedTime + " millis");
	            wholeTime += elapsedTime;

	          //3- Afficher les résultats sur la console et dans les fichiers 
	            String usedVariables = satChecker.printUsedVariables(); //Les variables utilisés pendant le traitement 
	                                                                    //qui satisfont les clauses 
	            satisfiedClauses+=satChecker.getMaxClausesSatisfied();
	            instanceNumber++;
	            if (satisfiable) {
	                System.out.println("\t-L'ensemble des clauses est satisfiables, valeurs des variables et le nombre des clauses satisfaites par chaque variable : ");
	                System.out.print("\t\t" + usedVariables);
	            } else {
	                System.out.println("\t" + "Aucune solution trouvée" + (satChecker.isTimeExpired() ? "(temps expiré)" : "(L'ensemble des clauses est insatisfiable)"));
	                System.out.println("\t-Le nbr max des clauses satisfaites pendant le traitement est " + satChecker.getMaxClausesSatisfied());
	                if (satChecker.isTimeExpired())
	                    System.out.println("\t-Le nombre de clauses satisfaites par chaque variable lorsque le programme est arrêté est : ");
	                System.out.print("\t\t" + usedVariables);
	            }

	            instanceLine+= "Les fichiers traités \n"+file.getName()+"\t";
	            clausesLine+= "Le nbr max des clauses satisfaites\n"+satChecker.getMaxClausesSatisfied()+"\t";
	            timeLine+="Temps passé\n "+ elapsedTime+"\t";

	            System.out.println("\n");
	        }

	        outputWriter.write(instanceLine);
	        outputWriter.newLine();
	        outputWriter.write(clausesLine);
	        outputWriter.newLine();
	        outputWriter.write(timeLine);

	        System.out.println("WHOLE TIME (millis) : "+wholeTime);
	        outputWriter.write("whole time : "+wholeTime);
	        outputWriter.close();

	        return satisfiedClauses/(instanceNumber>0?instanceNumber:1);
	    }    
	
	// Helping Methods
	 
    /* a helping method, to avoid writing a long instrcution when printing message  */
    public static void sop(Object o){
        System.out.println(o.toString());
    }

    
    
    
}// Fin class Main 	
       