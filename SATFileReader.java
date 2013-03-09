// code taken from Jon Todd's SAT solver
// 2003

import java.io.*;
import java.util.*;

public class SATFileReader {

    //Variables for the input and parsing of the SAT file
    public static BufferedReader input;      // input buffer for array
    public static String filename = "";      // name of file
    public static int nClauses = 0;          // number of clauses in the file
    public static int nVariables = 0;        // number of variables in the file
    public static int maxVarInClause = 0;    // max number of variables in a clause
    public static int SATFormula[][];        // array that contains the SAT formula
    public static BufferedReader inputFile;  //input buffer for max var count
    

    // Main mehtod
    public static void main(String args[]) {
  	
	// Try to read in argument 0 for filename, if none provided then prompt for it
	try {
	    filename = args[0];
	}
	catch (ArrayIndexOutOfBoundsException e) {
	    System.out.print("Please specify a valid SAT plan File: ");
	    filename = readString(System.in);
	}
	
	openSATFile();
	SATArrayPrint(SATFormula);
	
    }
    

    // ****************************************************************************************************
    // Make sure the file exisits and if so prepare to read it in, if not loop till a valid file is given
    public static void openSATFile(){
	boolean goodfile = false;
	while (!goodfile){
	    try {
		input = new BufferedReader (new FileReader(filename));
		goodfile = true;
	    }
	    catch (FileNotFoundException e) {
		System.out.println("File not found: " + filename);
		System.out.print("Please specify a valid SAT plan File: ");
		filename = readString(System.in);
		goodfile = false;
	    }
	}
	readSATFile(); // now we have a valid file... read it in
    }

    

    // ****************************************************************************************************
    // method to read file into an array
    public static void readSATFile() {
	String s = ""; //temp string for each line read
	
	// read in first line
	try { 
	    s = input.readLine();
	} 
	catch (IOException e) {
	    System.out.print("Please specify a valid SAT plan File: ");
	    filename = readString(System.in);
	    openSATFile();
	}
	
	
	//First read the comment lines and print them to the screen
	System.out.println("File Comments:");
	while(s != null && s.startsWith("c")){
	    if(s.length() > 2)    
		System.out.println(s.substring(2));
	    // read line
	    try { 
		s = input.readLine();
	    } 
	    catch (IOException e) {
		System.out.print("Please specify a valid SAT plan File: ");
		filename = readString(System.in);
		openSATFile();
	    }
	}
	
	//Now get important info from the program line
	nVariables = 0;
	while(s != null && s.startsWith("p")){
	    s = s.substring(6); // cut off: "p cnf "
	    // get number of variables
	    int i = 0;
	    while(!Character.isWhitespace(s.charAt(i))){
		if(Character.isDigit(s.charAt(i)))
		    if (nVariables > 0)
			nVariables = (nVariables * 10)+ Character.getNumericValue(s.charAt(i));
		    else
			nVariables = Character.getNumericValue(s.charAt(i));
		i++;
	    }
	    
	    // get number of clauses
	    i++;
	    while(i < s.length()){
		if(Character.isDigit(s.charAt(i)))
		    if (nClauses > 0)
			nClauses = (nClauses * 10)+ Character.getNumericValue(s.charAt(i));
		    else
			nClauses = Character.getNumericValue(s.charAt(i));
		i++;
	    }
	    
	    // Print out info to user
	    System.out.println("");
	    System.out.println("Number Of Variables: " + nVariables);
	    System.out.println("Number Of Clauses: " + nClauses);
	    
	    // Calculate the most number of variables in a clause
	    maxVarInClause = maxVarsInClause();
	    System.out.println("Max number of Variables in a Clause: " + maxVarInClause);
	    
	    // read the next line
	    try { 
		s = input.readLine();
	    } 
	    catch (IOException e) {
		System.out.println("Please specify a valid SAT plan File: " + filename);
		filename = readString(System.in);
		openSATFile();
	    }
	    // Set size to the array
	    SATFormula = new int[nClauses][maxVarInClause];
	    
	    // input the data into the array[rows][cols]
	    int x = 0;
	    while(s != null && x < nClauses){
		if (!s.startsWith("c") && !s.startsWith("p")){
		    StringTokenizer st = new StringTokenizer(s);
		    int y = 0;
		    int j = 0;
		    while (st.hasMoreTokens()) {
			j = Integer.parseInt(st.nextToken());
			if (j != 0){
			    SATFormula[x][y] = j;
			    y++;
			}
			else
			    break;
		    }
		}
		// read line
		try {
		    s = input.readLine();
		} 
		catch (IOException e) {
		    System.out.print("Please specify a valid SAT plan File: ");
		    filename = readString(System.in);
		    openSATFile();
		}
		x++;
	    }
	}
    }
    

    // ****************************************************************************************************
    // method to find the max number of vars in all the clauses
    public static int maxVarsInClause(){
	int max = 0;
	String s = "";
	try{inputFile = new BufferedReader (new FileReader(filename));}
	catch (FileNotFoundException e) {openSATFile();System.exit(1);}
	// read line
	try { 
	    s = inputFile.readLine();
	} 
	catch (IOException e) {
	    System.out.print("Please specify a valid SAT plan File: ");
	    filename = readString(System.in);
	    openSATFile();
	}
	
	while(s != null){
	    if (!s.startsWith("c") && !s.startsWith("p")){
		StringTokenizer st = new StringTokenizer(s);
		int i = st.countTokens();
		if (i > max)
		    max = i;
	    }
	    // read line
	    try {
		s = inputFile.readLine();
	    } 
	    catch (IOException e) {
		System.out.print("Please specify a valid SAT plan File: ");
		filename = readString(System.in);
		openSATFile();
	    }
	}
	return (max-1);
    }
    
    
    
    // ****************************************************************************************************
    // Print contents of SAT Array
    public static void SATArrayPrint(int Formula[][]){
	// print contents of array
	for(int l = 0;l < nClauses; l++){
	    for(int t = 0; t < maxVarInClause; t++){
		//if (SATFormula[l][t] != 0)
		if (Formula[l][t] < 0)
		    System.out.print(Formula[l][t] + " ");
		else if (Formula[l][t] > 0)
		    System.out.print(" " + Formula[l][t] + " ");
		//else break;
	    }
	    System.out.println();
	}
	System.out.println();
    }
    
    
    // ****************************************************************************************************
    // method to read in string input from user
    public static String readString(InputStream is){
	int ch;
	String r = "";
	boolean done = false;
	while (!done){
	    try{
		ch = is.read();
		if (ch < 0 || (char)ch == '\n')
		    done = true;
		else if ((char)ch != '\r')
		    r = r + (char) ch;
	    }
	    catch(java.io.IOException e){
		done = true;
	    }
	}
	return r;
    }
    public static int getnVariables(){
        return nVariables;
    }
    public static int getnClauses(){
        return nClauses;
    }
    public static int[][] getSATFormula(){
        return SATFormula;
    }
}
