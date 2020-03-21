package traitements.SAT;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * class : SATReader
 * this class is used to read files of the dataset 
 */

public class SATReader {
  
  // The method Start is used : 
  // => to read files if the path is passed 
  // => read path from user (if the path is not passed)
  // => read a directory (if the path is a directory)
  
  // args c'est un tableau de chaine de caractère tel que chaq éléments c'est un nom de fichier	
  // args represente the PATH 	
  public ArrayList<File> Start(String args []){ 
	 // Debut 
	 ArrayList<File> files=new ArrayList<File>(); // Liste contient les fichiers SAT à traiter
	 
	 for(String str: args) {
		 File f=new File(str);
		 if(f.isFile()) files.add(f);
		 else if (f.isDirectory()) Read_Directory(f,files); //Si le path est un répertoire
	 }
	 
	 // if no path passed we read from user 
	 if(args.length<1 || files.isEmpty()) {
		 File f=readPathFromUser(); // Read from User
		 if(f.isFile()) files.add(f);
		 else Read_Directory(f,files);
	 }
	  
	  return files; // Return an array list of files which are the dataset files 
	  // Fin
  }
	
  // Read_Directory
  // file is a File of type Directory it contain a  lot of files
  private void Read_Directory(File file, ArrayList<File> files){
      for (File f : file.listFiles()) {
		    if (f.isFile()) 
		        files.add(f);
		}
  }
  
  // Read the path from user
  public File readPathFromUser(){ 
      File file;
      Scanner scanner= new Scanner(System.in);
      System.out.println("Aucun path valide passé, Entrer le chemin  du fichier / répértoire manuellement :");

      do {
          file = new File(scanner.nextLine());
          if (file.isDirectory() || file.isFile()) {
              return file;
          }else
              System.out.println("Veuillez entrer un chemin valide !! ");
      }while(true);
  }
  
}