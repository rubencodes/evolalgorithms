/*****************************************************************
 * READ ME:
 * Evolutionary Algorithms						          
 * An Implementation of Genetic Algorithms			          
 * and Population-Based Incremental Learning			          
 *											          
 * By Tyler Higgins, Marissa Rosenthal, and	Ruben Martinez	     
 *											          
 * Included Files:								          
 * SATFileReader.java							          
 * EvolAlg.java								          
 * 											          
 * Required Arguments:							          
 * For Genetic Algorithms: 						          
 * [1] Name of File								          
 *	  i.e. file.cnf							          
 * [2] Number of Individuals (Population)			        
 *     i.e. 100								          
 * [3] Selection Method							          
 *     [a] Tournament select ( ts )				          
 *     [b] Rank select ( rs )						          
 *     [c] Boltzmann ( bs )						          
 * [4] Crossover Method							          
 *     [a] One-Point Crossover ( 1c )				          
 *     [b] Uniform Crossover ( uc )				          
 * [5] Crossover Probability						          
 *     i.e. 0.7								          
 * [6] Mutation Probability						          
 *     i.e. 0.02								          
 * [7] Number of Generations						          
 *     i.e. 100								          
 * [8] Algorithm								        
 *     [a] Genetic Algorithm ( g )					          
 * Example: java EvolAlg file.cnf 100 ts 1c 0.7 0.01 100 g       
 *										               
 * For Population-Based Incremental Learning:		          
 * [1] Name of File								          
 *     i.e. file.cnf							          
 * [2] Number of Individuals (Population)			          
 *     i.e. 100								          
 * [3] Positive Learning Rate						          
 * 	  i.e. 0.1								          
 * [4] Negative Learning Rate						          
 *     i.e. 0.075								          
 * [5] Mutation Probability						          
 *     i.e. 0.02								          
 * [6] Mutation Amount							          
 *     i.e. 0.05								          
 * [7] Number of Generations						          
 * 	  i.e. 100								          
 * [8] Algorithm								          
 * 	  [a] PBIL ( p ) 							          
 * Example: java EvolAlg file.cnf 100 0.1 0.075 0.02 0.05 100 p 
 *											          
 * Instructions:									     
 * Our program takes in a file containing formatted MAXSAT       
 * problems. Depending on the arguments, it will either run      
 * a genetic algorithm, or run a population-based incremental    
 * learning algorithm for a specified number of generations,    
 * and outputs the best individual created by the algorithm,    
 * the generation it was found, the number of clauses it 	     
 * satisfies, the percent satisfied clauses, as well as some     
 * information about the input file such as name, number of      
 * variables, number of clauses, and the maximum number of       
 * variables in a clause. 							     
 * When running a genetic algorithm, the user can choose among   
 * tournament selection, rank selection, or Boltzmann selection. 
 * They can also choose between one-point crossover and uniform  
 * crossover, as well as select custom values for population     
 * size, number of generations, and crossover and mutation       
 * probabilities.								          
 * When running in PBIL mode, the user can also choose a custom  
 * population size, number of generations, and mutation amount.  
 * They can also specify positive and negative learning rates,   
 * as well as, mutation amount.						     
 *****************************************************************/
