import java.io.*;
import java.util.*;
public class EvolAlg extends SATFileReader {
	//variables
	public static int size = 0;				// population size
	public static double crossoverProb = 0;	// crossover probability
	public static double mutationProb = 0;	// mutation probability
	public static int numVariables = 0; 	//number of variables in clause
    public static int numClauses = 0; 		//number of clauses/individuals
    public static int[][] initPopulation; 	// initial population of randomly positive/negative individuals
    public static int[][] breedingPool; 	// inital array containing individuals
    public static int[] bestIndividual; 	// clause with best fitness
    public static int bestGeneration = 0; 	// best generation found
    public static int currentGeneration = 0;// counter to keep track of generation
    
	public static void main(String args[]) {
		String select = "";		// selection method
		String crossover = "";	// crossover method
		int numIterations = 0; 	// number of iterations
		String alg;				// algorithm chosen by user
        SATFileReader reader = new SATFileReader(); 	//create a new file reader
        reader.filename = args[0];  					//set the filename as what the user input
		size = Integer.parseInt(args[1]); 				//get size of population from user input
		alg = args[7]; 									//get algorithm to use from user input
		numIterations = Integer.parseInt(args[6]); 		//get generations to run from user input
		
		//ensures arguement was entered correctly by user
		if (args.length != 8) {
			System.out.println("Invalid number of arguments entered!");
			System.exit(1);
		}
		
        System.out.println("");
        System.out.println("File: "+reader.filename); 	//prints file name to user
        reader.openSATFile(); 							//open the user file
        numVariables = reader.getnVariables(); 			//get total number of variables in file
		numClauses = reader.getnClauses(); 				//get total number of clauses in file
        int[][] SATFormula = reader.getSATFormula(); 	//retrieve 2D array of variables and clauses
        initPopulation = new int[size][numVariables]; 	//2D array to contain population
		boolean isPopGenerated = true; 					//checks for a fully generated initial population
        breedingPool = new int[size][numVariables]; 	//2D array to contain population to breed
        bestIndividual = new int[numVariables]; 		//best individual in population
		int overallMax = 0; 							//best fitness in population
		isPopGenerated = generatePopulation(); 			//generates an initial population
		reader.readSATFile(); 							//read the user file
		
		if (!isPopGenerated) {
			System.out.println("Population could not be generated!"); 	//population generation error
			System.exit(1);
		}
		
		//verifies user input for algorithm
        if (!(alg.equalsIgnoreCase("p") || (alg.equalsIgnoreCase("g"))) 
            && !(alg.equalsIgnoreCase("g") || (alg.equalsIgnoreCase("p")))){
            System.out.println("Please enter either p or g");
            System.exit(1);
        }
		
        for (int w = 0; w < numIterations; w++) { 	//for the number of generations desired
            if (alg.equalsIgnoreCase("g")) {    	//run a genetic algorithm
                select = args[2]; 		//selection method
                crossover = args[3]; 	//crossover method
                crossoverProb = Double.parseDouble(args[4]);	//crossover probability
                mutationProb = Double.parseDouble(args[5]);		//mutation probability
                
				//verifies user input for crossover probability
                if(crossoverProb>1){ 
                    System.out.println("Please enter a correct crossover probability");
                    System.exit(1);
                }
                
				//verifies user input for mutation probability
                if(mutationProb >1){
                    System.out.println("Please enter a correct mutation probability");
                    System.exit(1);
                }
                
                if (select.equals("ts")) 			//run tournament selection
                    breedingPool = tourSelect();	//retrieve breeding pool

                else if (select.equals("rs"))		//run rank selection
                    breedingPool = rankSelect();	//retrieve breeding pool
                
                else if (select.equals("bs"))		//run boltzmann selection
                    breedingPool = boltSelect();	//retrieve breeding pool
				
				//verifies user input for selection method
                else {
                    System.out.println("Please enter a correct selection method: ts, rs, or bs");
                    System.exit(1);
                }
                
                if(crossover.equals("1c"))                  //run 1 point crossover
                    initPopulation = onePointCrossOver(); 	//retrieve the new initial population
                
                else if(crossover.equals("uc"))   			//run uniform crossover
                    initPopulation = uniformCrossOver();	//retrieve the new initial population
				
				//verifies user input for crossover type
                else{
                    System.out.println("Please enter a correct crossover method: 1c or uc");
                    System.exit(1);
                }
                
                currentGeneration++; 	//increment the generation counter
                mutatorGA(); 			//mutate the new population
                overallMax = getBestIndividual(overallMax);	//get the best individual
            }
            
            if (alg.equalsIgnoreCase("p")) { //run population-based incremental learning
                double posLearnRate = Double.parseDouble(args[2]); 		//shift up probability
                double negLearnRate = Double.parseDouble(args[3]); 		//shift down probability
                double muteProbability = Double.parseDouble(args[4]); 	//mutation probability
                double muteAmount = Double.parseDouble(args[5]); 		//mutation amount
				
				//verify user input for positive learning rate
                if(posLearnRate>1){
                    System.out.println("Please enter a correct probability");
                    System.exit(1);
                }
				
				//verify user input for negative learning rate
                if(negLearnRate>1){
                    System.out.println("Please enter a correct probability");
                    System.exit(1);
                }
				
				//verify user input for mutation probability
                if(muteProbability>1){
                    System.out.println("Please enter a correct probability");
                    System.exit(1);
                }
				
				//verify user input for mutation amount
                if(muteAmount>1){
                    System.out.println("Please enter a correct probability");
                    System.exit(1);
                }
				
                pbil(posLearnRate, negLearnRate, muteProbability, muteAmount); 	//if no errors, run PBIL
                overallMax = getBestIndividual(overallMax); 					//get the best individual
            }
            if(overallMax == numClauses) {  //if we satisfy every clause
                w = numIterations;          //exit the for loop immediately
            }
        }
		
        //calculate percentage of satisfied clauses by best individual
        int percent = (100*overallMax)/(numClauses);
        System.out.println("");
        System.out.println("Number of clauses satisfied by best assignment: "+overallMax);
        System.out.println("Percent of clauses satisfied by best assignment: " +percent+"%");
        System.out.println("");
        System.out.println("Best individual: ");
		
		//print out the best individual
        for (int x = 0; x < numVariables-1; x++) {
            System.out.print(bestIndividual[x]+", ");
        }
        System.out.print(bestIndividual[numVariables-1]);
        System.out.println("");
		
		//print out the individual's generation
        System.out.println("Generation Found: "+bestGeneration);
        System.out.println("");
	}

	/*
	 * This method generates a population of individuals given a specified size. 
	 * We randomly assign positive or negative values to each element in an individual.
	 */
	public static boolean generatePopulation() {
		boolean check = true; //checks for unfilled variables
		int decider; //will hold 0 or 1, positive or negative
        Random random = new Random(); //for randomness
		
		for (int i = 0; i < size; i++) {
            for(int j = 0; j < numVariables; j++) {
                decider = Math.abs(random.nextInt(2)); //randomly assigns 0 or 1 to each variable in clause 
                if (decider == 0) //if we get a 0
                    initPopulation[i][j] = j+1; //make index+1 positive and store in array
                if (decider == 1) //if we get a 1
                    initPopulation[i][j] = (j+1)*-1; //make index+1 negative and store in array
				if (initPopulation[i][j] == 0) //if current location is ever empty
					check = false; //set check to false
            }
        }
		
		return check;
	}
	
    /* 
     *  This method uses PBIL to find the most fit individual using positive and negative learning rates, mutation
     *  probability, and mutation amount as parameters. These values are input by the user.
     *  It does not return anything, but generates the most fit individual by evolving the array of probabilities
     *  that generates individuals of a population.
     *  
     */
    public static void pbil(double posLearnRate, double negLearnRate, double muteProbability, double muteAmount) {
        currentGeneration++;
        double[] probVector = new double[numVariables]; //stores probabilities for each variable in clause
        Random random = new Random(); // generates a new random number
    
        //initialize probability array/vector
        for (int i = 0; i < numVariables; i++) {
            probVector[i] = 0.5;
        }
        //generate samples
        for (int j = 0; j < size; j++) {
            for (int k = 0; k < numVariables; k++) {
                if (random.nextDouble() < probVector[k])
                    initPopulation[j][k] = -1*(k+1);
                else
                    initPopulation[j][k] = (k+1);
            }
        }
        
        int[] mostFit = getBestIndividualInGeneration();	//gets the most fit individual in this generation
        int[] leastFit = getWorstIndividualInGeneration();	//gets the least fir individual in this generation
		int[] mFit = new int[numVariables];		//will hold 1's and -1's depending on most fit's positives and negative values
        int[] lFit = new int[numVariables];		//will hold 1's and -1's depending on least fit's positives and negative values
        for (int l = 0; l < numVariables; l++) {
            if (mostFit[l] > 0)	//if number at this index is positive in most fit individual
                mFit[l] = 1;	//set mFit at this index to 1
            else				//if number at this index is negative in most fit individual
                mFit[l] = -1;	//set mFit at this index to -1
            if (leastFit[l] > 0)//if number at this index is positive in least fit individual
                lFit[l] = 1;	//set lFit at this index to 1
            else				//if number at this index is negative in least fit individual
                lFit[l] = -1;	//set lFit at this index to -1
            
            //update probability  towards best solution based on formula
            double newProb = probVector[l] * (1-posLearnRate) + (mFit[l] * posLearnRate);
			
            if (lFit[l] != mFit[l]) { 
                //update probability away from worst solution
                newProb = newProb * (1-negLearnRate) - lFit[l] * negLearnRate;
                probVector[l] = newProb;
            }
        }
		
        //mutate probability vector
        probVector = mutatorPBIL(probVector, muteAmount, muteProbability);
    }
	
	/*
     *  This method finds the best individual in each generation. It does not take any parameters
     *  and returns an array of integer values of the initPopulation of the most fit individual.
     */
    public static int[] getBestIndividualInGeneration() {
        int maxFitness = 0;		//maximum fitness in generation
        int[] currentBestIndividual = new int[numVariables]; //best individual in generation
        for (int i = 0; i < size; i++) { 						//for all individuals in population get fitness
            int currentFitness = getFitness(initPopulation[i]);	//get their fitness
            if (currentFitness >= maxFitness) { 				//if greater than current max fitness
                maxFitness = currentFitness;					//make it the max fitness
                currentBestIndividual = initPopulation[i]; 		//set individual to best individual in generation
            }
        }
		
        return currentBestIndividual;
    }
    
    /*
     *  This method finds the best individual in each generation. It does not take any parameters
     *  and returns an array of integer values of the initPopulation of the least fit individual.
     */
    public static int[] getWorstIndividualInGeneration() {
        int minFitness = numClauses;	//worst fitness in generation
        int[] currentWorstIndividual = new int[numVariables]; //worst individual in generation
        for (int i = 0; i < size; i++) {						//for all individuals in the population
            int currentFitness = getFitness(initPopulation[i]);	//get their fitness
            if (currentFitness <= minFitness) {					//if less than current min fitness
                minFitness = currentFitness;					//make it the min fitness
                currentWorstIndividual = initPopulation[i];		//set individual to worst individual in generation
            }
        }
		
        return currentWorstIndividual;
    }
	
    /*
     *  This method does mutation on the probability vector, thus mutating the population. 
     *  @param: array of probabilities, mutation amount, and mutation probability.
     *  Method returns a 'mutated' probability vector. 
     */
    public static double[] mutatorPBIL(double[]probVector, double muteAmount, double muteProbability){
        Random random = new Random(); //for randomness
        for (int i=0; i<numVariables; i++){	//for each int in clause of probability vector
            double decider = random.nextDouble();	//generate a random double
            if (decider <= muteProbability && decider != 0) {	//if the random is smaller than our mutation prob
				if(random.nextDouble() >= 0.5){		//if a new random double is greater than 0.5
					probVector[i] = probVector[i] + muteAmount;	//mutate our probability vector up
                }
            }
            else
                probVector[i] = probVector[i] - muteAmount;	//otherwise mutate out probability down
        }
        return probVector;
    }
	
    /*
     *  This method is for tournament selection. It does not take any parameters and returns a two-dimensional
     *  array representative of the breeding pool (i.e. individuals and their relative fitnesses).
     *
     */
	public static int[][] tourSelect() {
        int[] mateOne;		//first mate to create a child
        int[] mateTwo;		//second mate to create a child
        int mateOneFitness;	//fitness of first mate
        int mateTwoFitness;	//fitness of second mate
        Random random = new Random(); //for randomness
        
        //choosing random element in array
		for (int i = 0; i < size; i++) {
            int positionOfMateOne = Math.abs(random.nextInt()%size);	//randomly select an individual
            int positionOfMateTwo = Math.abs(random.nextInt()%size);	//randomly select another individual
            while (positionOfMateOne == positionOfMateTwo) {	//if the two individuals are the same
                positionOfMateTwo = Math.abs(random.nextInt())%size;			//pick a new individual
            }
            mateOne = initPopulation[positionOfMateOne];	//first mate
            mateTwo = initPopulation[positionOfMateTwo];	//second mate
            
            // select the assignment that is more fit and add to breeding pool
            mateOneFitness = getFitness(mateOne);	//get fitness of mate one
            mateTwoFitness = getFitness(mateTwo);	//get fitness of mate two
            if (mateOneFitness > mateTwoFitness)//if mate one is more fit
				breedingPool[i] = mateOne;		//add mate one to breeding pool
            else 							//if mate two is more fit
				breedingPool[i] = mateTwo;	//add mate two to breeding pool
        }
		
        //returns a new population of most fit individuals
        return breedingPool;
	}
   
    /*
     *  This method is for rank selection. It does not take any parameters and returns a two-dimensional
     *  array representative of the breeding pool (i.e. individuals and their relative fitnesses).
     *
     */
    public static int[][] rankSelect() {
        int[] fitnesses = new int[size]; 	//array of all fitnesses
        int[] rankProb = new int[size];		//rank probabilities
        int[][] sortedinitPopulation = new int[size][numVariables]; //2D array to store individuals sorted by fitness
        int rankSum = 0; 					//sum of all ranks
        int counter = 0; 					//keep track of location in array
		Random random = new Random();		//for randomness
        
		//get fitnesses for all individuals
		for (int i = 0; i < size; i++) {
            fitnesses[i] = getFitness(initPopulation[i]); //store fitnesses in array
        }
        
		//sort initial population by fitness
        for (int m = 0; m < size; m++) {
            for (int n = 0; n < size; n++) {
                if (fitnesses[m] == getFitness(initPopulation[n])) {
                    sortedinitPopulation[m] = initPopulation[n]; //store in new array
                }
            }
        }
        
		//find total sum of ranks
        for (int j = 1; j <= size; j++) {
            rankSum += j; 
        }
        
		//get probability for each rank
        for (int k = 0; k < size; k++) {
            rankProb[k] = k/rankSum; //store into array
        }
        
        while(counter != size) { 				//while breeding pool is not full
            int choice = Math.abs(random.nextInt(size)); 	//generate a random int smaller than size
            if(rankProb[choice] < random.nextDouble()) { 				//if our rank probability is smaller than a random double
                breedingPool[counter] = sortedinitPopulation[choice]; 	//add to breeding pool
                counter++; 	//increment location in breeding pool
            }
        }
        
        return breedingPool;
    }
	
    /*
     *  This method is for Boltzman selection.  It does not take any parameters and returns a two-dimensional
     *  array representative of the breeding pool (i.e. individuals and their relative fitnesses).
     *
     */
    public static int[][] boltSelect() {
        int[] fitnesses = new int[size]; 	//store fitnesses
        double denominator = 0; 			//sum of all probabilities
        double runningTotal = 0; 			//running sum of probabilities
        int counter = 0; 					//keep track of where we are in array
        double randDouble; 					//random double
        Random random = new Random();		//for randomness

        //gets array of fitnesses to be sorted
		for (int i = 0; i < size; i++) {
            fitnesses[i] = getFitness(initPopulation[i]);	//store in array of fitnesses
        }
        
        //calculate size of array must be depending on proportions of fitness
        for (int j = 0; j < size; j++) {
            denominator += Math.exp(fitnesses[j]/numClauses);
        }
        
		//pick a random double
        randDouble = random.nextDouble();
		
		/*
		 * For the code below, note that while we don't add anything 
		 * to the breeding pool the running total does not get reset, 
		 * but the for loop will get restarted if the breeding pool 
		 * is not full. This means that if my i goes to location 99, 
		 * it will start over at position 0 without losing its running 
		 * total. This is meant to add some more 'randomness' to 
		 * selecting individuals to add to the array.
		 */
        while (counter != size) { //while breeding pool is not full
            for (int i = 0; i < size; i++) {
                double percentEntry = Math.exp(fitnesses[i]/numClauses)/denominator; //generate probability for this individual
                runningTotal += percentEntry;	//add to running total
                if(randDouble < runningTotal && counter != size) { 	//if our random double is less than the running total
                    breedingPool[counter] = initPopulation[i]; 		//add to array
                    counter++; 										//increment location in array
                    runningTotal = 0; 								//clear running total
                    randDouble = random.nextDouble(); 				//get a new random double
                    i = Math.abs(random.nextInt(size)); 						//send i to random start location
                }
            }
        }
        
        return breedingPool;
    }
    
    /*
     *  This method finds the fitness for each individual in the population (i.e. how 'good' the clause
     *  is). It takes an array of integers as a parameter so that for every clause, the fitness is checked. 
     *  An individual's (clause's) fitness is determined by how many clauses are true.
     *  The method returns an integer that represents the number of clauses satisified by a particular assignment.
     *
     */
	public static int getFitness(int[] partner) {
        boolean clauseValidity = false;	//determine if clause is valid
        int fitness = 0;				//fitness counter
		
        for(int j=0; j<numClauses; j++){	//for each clause
            for(int i = 0; i<partner.length; i++){	//for each partner
                for(int k=0; k<SATFormula[j].length; k++){ 	//for each element                   
                    if(partner[i] == SATFormula[j][k])		//if the element is equal to the element in file
                        clauseValidity = true;				//increase fitness
                }
				//if we want to increase the fitness and this is the last element
                if(clauseValidity && (partner[i] == SATFormula[j].length-1)){
                    fitness++;	//increase fitness counter
                    clauseValidity = false;	//reset clause validity
                }
            }
        }
		
        return fitness;
    }

    /*
     *  This method returns the 'best' fitness of an individual in the population over every generation.
     *  It takes the overall maximum found so far from all generations and compares it to the new maximum
     *  for the current generation. It returns the overall maximum fitness found thus far. 
     *
     */
    public static int getBestIndividual(int overallMax) {
        int maxFitness = 0;				//stores maximum fitness in generation
        int[] currentBestIndividual = new int[numVariables];	//stores the best individual in generation
        int currentBestGeneration = 0;	//stores generation of best individual
        
        //iterates through the population to find the fitness of each individual.
        for (int i = 0; i < size; i++) {
            int currentFitness = getFitness(initPopulation[i]);
            //finds maximum fitness in generation
            if (currentFitness > maxFitness) {	//if the current fitness is greater than stored max
                maxFitness = currentFitness;	//make current the new stored max
                currentBestIndividual = initPopulation[i];	//store this individual as best individual
                currentBestGeneration = currentGeneration;	//store individual's generation
            }
			//compares overall maximum fitness to generation's maximum fitness
            if (maxFitness > overallMax) {	//if the max fitness in generation is greater than best overall fitness
                overallMax = maxFitness;	//store this generation's max as the best overall fitness
                bestIndividual = currentBestIndividual;	//store this individual as the best overall individual
                bestGeneration = currentBestGeneration;	//store the generation as the current best
            }
        }
        
        return overallMax;
    }
    
    /*
     *  This method is for one-point crossover recombination.  It does not take any parameters and returns a 
     *  two-dimensional array representative of the new population (i.e. individuals and their relative fitnesses).
     *
     */
    public static int[][] onePointCrossOver(){
        int[] mateOne = new int[numVariables];	//store first mate
        int[] mateTwo = new int[numVariables];	//store second mate
        int crossoverPoint = 0;					//stores a crossover point
        int[][] newGen = new int[size][numVariables];	//stores next generation of individuals
        Random random = new Random();			//for randomness
        double cProb = 0;						//random crossover probability
        
        //iterates through individuals and chooses two individuals next to each other
        for (int i = 0; i < size; i=i+2) {
            mateOne = breedingPool[Math.abs(random.nextInt(size))];	//get a mate
            mateTwo = breedingPool[Math.abs(random.nextInt(size))];	//get another mate
            crossoverPoint = Math.abs(random.nextInt(numVariables));    //generate a random crossover point
            cProb = random.nextDouble();								//generate a random crossover probability
            
            if (cProb <= crossoverProb && cProb != 0) {	//if crossover is going to happen
                //iterates through individuals and creates new individuals
                for(int j = 0; j < crossoverPoint; j++) { //keep first part of individual the same
                    newGen[i][j] = mateOne[j]; 	//part one of first child
                    newGen[i+1][j] = mateTwo[j];//part one of second child
                }
                for(int k = 0; k < numVariables; k++) { //now crossover for second part
                    newGen[i][k] = mateTwo[k];	//second half of first child
                    newGen[i+1][k] = mateOne[k];//second half of second child
                }
            }
            //no crossover in this case
            else {
                newGen[i] = mateOne;	//directly pass mate one
                newGen[i+1] = mateTwo;	//directly pass mate two
            }
        }
		
        return newGen;
    }
    
    /*
     *  This method is for uniform crossover recombination.  It does not take any parameters and returns a
     *  two-dimensional array representative of the new population (i.e. individuals and their relative fitnesses).
     *
     */
    public static int[][] uniformCrossOver(){
        int[] mateOne = new int[numVariables];	//store a first mate
        int[] mateTwo = new int[numVariables];	//store a second mate
        int[][] newGen = new int[size][numVariables];	//store new generation
        int prob;								//store probability
        Random random = new Random();			//for randomness
        
        for (int i = 0; i < size/2; i = i+2) {
            mateOne = breedingPool[i];	//select a mate
            mateTwo = breedingPool[i+1];//select another mate
            double cProb = random.nextDouble();
            //allows for possibility of no crossover
            if (cProb <= crossoverProb && cProb != 0) {
                for(int j = 0; j < numVariables; j++) {
                    prob = Math.abs(random.nextInt(2));
                    if (prob == 0)
                        newGen[i][j] = mateOne[j];
                    if (prob == 1)
                        newGen[i][j] = mateTwo[j];
                    
                    prob = Math.abs(random.nextInt(2));
                    if (prob == 0)
                        newGen[i+1][j] = mateOne[j];
                    if (prob == 1)
                        newGen[i+1][j] = mateTwo[j];
                }
            }
            
            //no crossover
            else {
                newGen[i] = mateOne;
                newGen[i+1] = mateTwo;
            }
        }
        
        return newGen;
    }
	
    /*
     *  This method does mutation on the initPopulation of a breeded population for GA. It does not take
     *  any parameters and does not return a value.
     */
    public static void mutatorGA() {
		double decider;					//store a random decider
		Random random = new Random();	//for randomness
		
		for (int i = 0; i < size; i++) {				//for each individual
			for (int j = 0; j < numVariables; j++) {	//for each variable
				decider = random.nextDouble();			//generate a random double, decider
				//decide to mutate or not
				if (decider <= mutationProb && decider != 0) {			//if decider is less than our mutation probability
					initPopulation[i][j] = -1*(initPopulation[i][j]);	//flip sign on individual element
				}
			}
		}
    }    
}
