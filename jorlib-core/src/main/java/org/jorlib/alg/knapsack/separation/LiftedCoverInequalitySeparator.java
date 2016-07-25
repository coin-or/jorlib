/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under LGPLv2.1
 *
 */
/* -----------------
 * LiftedCoverInequalitySeparator.java
 * -----------------
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * Original Author:  Joris Kinable
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 *
 */
package org.jorlib.alg.knapsack.separation;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jorlib.alg.knapsack.KnapsackAlgorithm;


/**
 * This class calculates both Lifted and Minimal Cover inequalities for binary knapsack constraints.<p>
 * The algorithms used to separate violated inequalities are provided in:<br>
 * {@literal Nemhauser, G.L., Wolsey, L.A., Integer and combinatorial optimization. 1999, John Wiley & Sons}
 * Given a knapsack constraint: {@code \sum_i a_i x_i <= b} where {@code x_i} are binary variables, {@code b} a positive integer and {@code a_i} integer coefficients.
 * Let {@code N} be the set of variables in the knapsack constraint.
 * For a given assignment of values to the variables, this class computes two types of violated cover inequalities:
 * <ol>
 * <li>Minimal covers: {@code \sum_{j\in C} \leq |C|-1}, where {@code C\subseteq N, \sum_{j\in C} a_i >b}</li>
 * <li>Lifted covers: {@code \sum_{j\in N\setminus C} \alpha_jx_j + \sum_{j\in C2} \gamma_jx_j + \sum_{j\in C1} x_j \leq |C1|-1+\sum_{j\in C2}\gamma_j}, where
 *    {@code C1 \cap C2= \emptyset}, {@code C1 \cup C2= C}, {@code C} a minimal cover as defined above.</li>
 * </ol><br>
 *
 * NOTE: Separating violated Lifted Cover Inequalities is NP-hard. Hence we rely on lifting and a separation heuristic. 
 * First we attempt to find a violated Lifted Cover inequality with {@code C2=\emptyset}. If we can't find such an inequality, we set {@code C2} to:
 * {@code C2={k}, k=arg max_{j\in C}a_j variableValue[j]}, and {@code C1=C\setminus C2}, and retry.<br><br>
 * 
 * Note: The precision of the calculations are accurate up to 0.000001.
 *  
 * @author Joris Kinable
 * @since April 8, 2015
 *
 */
public class LiftedCoverInequalitySeparator {

	/** Rounding precision **/
	public static final double PRECISION=0.000001;
	
	/** Knapsack solver used by the separator **/
	private final KnapsackAlgorithm knapsackAlgorithm;
	
	/** Knapsack constraint {@code \sum_{i=0}^n a_ix_i \leq b} **/
	private int nrVars; //Number of variables
	private int[] knapsackCoefficients;
	private int b;
	private double[] variableValues;
	
	/** COVER INEQUALITIES **/
	private boolean coverInequalityExists; //Indicates whether a cover inequality exists. If {@code \sum_i a_i \leq b}, then no cover exists and hence no inequality can be generated.
	
	/** Minimal cover **/
	private int minimalCoverRHS; //Right hand side of the minimal cover inequality
	private boolean[] minimalCover; //Boolean array indicating whether a variable is in the minimal cover 
	private Set<Integer> minimalCoverSet; //Set containing the variables which are part of the minimal cover
	private boolean minimalCoverIsViolated; //Returns true if LHS > minimalCoverSize-1 for the given variableValues
	
	/** Lifted cover **/
	private double liftedCoverLHS=0; // Evaluation of the left hand side of the equality
	private int liftedCoverRHS; //Right hand side of lifted cover inequality
	private int[] liftedCoverCoefficients; //Coefficients of variables in lifted cover inequality
	private boolean liftedCoverIsViolated;
	
	/**
	 * Creates a new separator
	 * @param knapsackAlgorithm This separator requires an algorithm to solve knapsack problems
	 */
	public LiftedCoverInequalitySeparator(KnapsackAlgorithm knapsackAlgorithm){
		this.knapsackAlgorithm=knapsackAlgorithm;
	}
	
	/**
	 * Given a knapsack constraint: {@code \sum_{i=0}^n a_ix_i \leq b} This method separates minimal Cover Inequalities, i.e it will search for a valid cover
	 * {@code \sum_{i\in C} x_i \leq |C|-1} which is violated by the current variable values
	 * @param nrVars number of variables in the knapsack constraint
	 * @param knapsackCoefficients a_i
	 * @param b right hand side of the knapsack constraint
	 * @param variableValues values of the x_i variables
	 */
	public void separateMinimalCover(int nrVars, int[] knapsackCoefficients, int b, double[] variableValues){
		this.nrVars=nrVars;
		this.knapsackCoefficients=knapsackCoefficients;
		this.b=b;
		this.variableValues=variableValues;
		
		this.computeMinimalCover();
	}
	
	/**
	 * Given a knapsack constraint: {@code \sum_{i=0}^n a_ix_i \leq b} This method separates Lifted Cover Inequalities, i.e it will search for a valid lifted cover inequality
	 * {@code \sum_{j\in N\setminus C} \alpha_jx_j + \sum_{j\in C2} \gamma_jx_j + \sum_{j\in C1} x_j \leq |C1|-1+\sum_{j\in C2}\gamma_j} which is violated by the current
	 *  variable values, where {@code C1 \cap C2= \emptyset, C1 \cup C2= C, C} a minimal cover
	 * @param nrVars number of variables in the knapsack constraint
	 * @param knapsackCoefficients a_i
	 * @param b right hand side of the knapsack constraint
	 * @param variableValues values of the x_i variables
	 * @param performDownLifting When set to true, additional effort is performed to find a violated Lifted Cover AbstractInequality. When this value is false, C2 will be an empty set.
	 * 
	 */
	public void separateLiftedCover(int nrVars, int[] knapsackCoefficients, int b, double[] variableValues, boolean performDownLifting){
		this.nrVars=nrVars;
		this.knapsackCoefficients=knapsackCoefficients;
		this.b=b;
		this.variableValues=variableValues;
		
		//First compute a lifted cover
		this.computeMinimalCover();
//		System.out.println("coverInequalityExists: "+coverInequalityExists);
		if(!coverInequalityExists) return;
		//Which we then try to lift
		//PART 1: Lifting heuristic to produce a lifted cover inequality with empty C2, see (Lifting heuristic, p461)
		this.computeLiftedCover(Collections.emptySet());
		if(liftedCoverIsViolated) //If we found a violated cover inequality, return, else continue with part 2
			return;
		//PART 2: Lifting heuristic to produce a lifted cover inequality with C2={k}, see (Separation algorithm, p462)
		if(performDownLifting)
			this.computeLiftedCoverWithNonEmptyC2();
	}
	
	
	/**
	 * Compute a minimal cover by solving:
	 * {@code S=min \sum_{i=0}^n (1-variableValues[i]) z_i}<br>
	 *    {@code s.t. \sum_{i=0}^n a_iz_i \geq b+1} <br>
	 *    {@code z_i binary}<br><br>
	 *    
	 * if {@code S<1}, the cover is violated.<br><br>
	 * 
	 * Note: Instead of solving the above problem, we transform it into a knapsack problem by substituting {@code z_i=1-y_i}, i.e. we solve:<br>
	 * {@code max  \sum_{i=0}^n (1-variableValues[i])y_i - \sum_{i=0}^n (1-variableValues[i])}<br>
	 * {@code s.t. \sum_{i=0}^n a_i y_i \leq \sum_{i=0}^n a_i -b-1}<br>
	 *      {@code y_i binary}<br><br>
	 *      
	 * The desired z_i values can be obtained: {@code z_i=1-y_i}
	 * 
	 */
	private void computeMinimalCover(){
		double[] itemValues=new double[nrVars];
		int maxKnapsackWeight=0;
		for(int i=0; i<nrVars; i++){
			itemValues[i]=1.0-variableValues[i];
			maxKnapsackWeight+= knapsackCoefficients[i];
		}
		if(maxKnapsackWeight <= b){
			coverInequalityExists=minimalCoverIsViolated=liftedCoverIsViolated=false;
			return;
		}else{
			coverInequalityExists=true;
		}
		maxKnapsackWeight-=b+1;
		
		//Solve problem as knapsack problem using your favorite knapsack implementation
		knapsackAlgorithm.solveKnapsackProblem(nrVars, maxKnapsackWeight, itemValues, knapsackCoefficients);
		boolean[] selectedItems=knapsackAlgorithm.getKnapsackItems();
		//Convert back into cover by substituting y_i=1-z_i
		double minimalCoverValue=0;
		minimalCoverRHS=0;
		minimalCover=new boolean[nrVars];
		minimalCoverSet=new LinkedHashSet<>();
		for(int i=0; i<nrVars; i++){
			minimalCover[i]=!selectedItems[i];
			if(minimalCover[i]){
				minimalCoverValue+=itemValues[i];
				minimalCoverRHS++;
				minimalCoverSet.add(i);
			}
		}
		minimalCoverRHS--;
		minimalCoverIsViolated=minimalCoverValue<=1-PRECISION;
	}
	
	
	/**
	 * Implementation of the lifting heuristic, p461
	 * @param C2 Set of variables C2. C=C1 \cup C2, C1 \cap C2= \emptyset
	 */
	private void computeLiftedCover(Set<Integer> C2){
		
		//List<Integer> cover=new ArrayList<Integer>(); //Variable ids which are part of the cover C
		Set<Integer> Lk=new LinkedHashSet<>(); //Variable ids of N/C, where N is the complete set of vars.
		List<Integer> NminLk=new ArrayList<>(); //List maintaining N\L1
		liftedCoverCoefficients=new int[nrVars]; //Array of alpha's
		//Compute Lk=N\C
		for(int i=0; i<nrVars; i++){
			if(minimalCover[i]){
				liftedCoverCoefficients[i]=1;
			}else
				Lk.add(i);
		}
		Lk.removeAll(C2); //Ignore all variables in set C2
		NminLk.addAll(minimalCoverSet);
		
		while(!Lk.isEmpty()){
			//Compute betas
			int[] betas=new int[Lk.size()];
			int index1=0;
			for(int j : Lk){ //Calculate Beta_j for j\in Lk, equation (2.4), p462
				double[] itemValues=new double[NminLk.size()];
				int[] itemWeights=new int[NminLk.size()];
				int maxKnapsackWeight=b-knapsackCoefficients[j];
				
				for(int index2=0; index2<NminLk.size(); index2++){
					int i=NminLk.get(index2);
					itemValues[index2]=liftedCoverCoefficients[i];
					itemWeights[index2]=knapsackCoefficients[i];
				}
				double knapsackValue=knapsackAlgorithm.solveKnapsackProblem(NminLk.size(), maxKnapsackWeight, itemValues, itemWeights);
				betas[index1]=(int) (minimalCoverRHS-Math.round(knapsackValue));
				index1++;
			}
			
			//Find j*=arg max j\in Lk \beta_j variableValues[j]
			int jstar=-1;
			int jstarIndex=-1;
			double bestValue=-Double.MAX_VALUE;
			int index=0;
			for(int j : Lk){
				double value=betas[index]*variableValues[j];
				if(value > bestValue){
					bestValue=value;
					jstar=j;
					jstarIndex=index;
				}
				index++;
			}
			
			//Set Lk=Lk\j*
			liftedCoverCoefficients[jstar]=betas[jstarIndex]; //set alpha_j*=beta_j*
			Lk.remove(jstar); //Remove j* from Lk
			NminLk.add(jstar);
			
		}
		//Lk should be empty here. Test whether \sum_j\in N \alpha_j variableValues[j] > |C| -1, if so, we found a violated inequality \sum_j\in N \alpha_j variableValues[j] <= |C| -1
		liftedCoverRHS=minimalCoverRHS;
		liftedCoverLHS=0;
		for(int i=0; i<nrVars; i++){
			liftedCoverLHS+=liftedCoverCoefficients[i]*variableValues[i];
		}
		liftedCoverIsViolated=liftedCoverLHS > liftedCoverRHS+PRECISION;
	}
	
	/**
	 * Implementation of the Separation Algorithm, p.462
	 */
	private void computeLiftedCoverWithNonEmptyC2(){
		//STEP 2 in Separation Algorithm
		
		//1. Find  k=arg max_{j\in C} a_j variableValue[j]
		double bestValue=-Double.MAX_VALUE;
		int k=-1;
		for(int j: minimalCoverSet){
			double value = knapsackCoefficients[j]*variableValues[j];
			if(value > bestValue){
				bestValue=value;
				k=j;
			}
		}
		
		//Temporarily adjust minimal cover and knapsack constraint
		minimalCoverSet.remove(k);
		minimalCover[k]=false;
		minimalCoverRHS--;
		b-=knapsackCoefficients[k];
		
		this.computeLiftedCover(new HashSet<>(Collections.singletonList(k)));
		
		//STEP 3 in Separation Algorithm: lift variable k back in using Proposition 1.2 p262
		double[] itemValues=new double[nrVars];
		for(int i=0; i<nrVars; i++){ //Convert int[] to double[]
			itemValues[i]=liftedCoverCoefficients[i];
		}
		itemValues[k]=0;
		double knapsackValue=knapsackAlgorithm.solveKnapsackProblem(nrVars, b+knapsackCoefficients[k], itemValues, knapsackCoefficients);
		int gamma= (int)Math.round(knapsackValue)-liftedCoverRHS;
		
		liftedCoverCoefficients[k]=gamma;
		liftedCoverRHS+=gamma;
		
		//STEP 4 check whether lifted inequality has been violated
		liftedCoverLHS=0;
		for(int i=0; i<nrVars; i++){
			liftedCoverLHS+=liftedCoverCoefficients[i]*variableValues[i];
		}
		liftedCoverIsViolated=liftedCoverLHS > liftedCoverRHS+PRECISION;
		
		//Restore minimal cover and knapsack constraint to their original state
		minimalCoverSet.add(k);
		minimalCover[k]=true;
		minimalCoverRHS++;
		b+=knapsackCoefficients[k];
	}
	
	/**** Minimal cover functions ****/
	
	/**
	 * Indicates whether a cover inequality exists, i.e returns whether {@code \sum_i a_i > b}. You should NOT ask for a minimal or lifted cover if this function returns false!
	 * @return Indicates whether a cover inequality exists, i.e returns whether {@code \sum_i a_i > b}. You should NOT ask for a minimal or lifted cover if this function returns false!
	 */
	public boolean coverInequalityExists(){
		return coverInequalityExists;
	}
	
	/**
	 * Returns C: the variables that are in the minimal cover
	 * @return Returns C: the variables that are in the minimal cover
	 */
	public Set<Integer> getMinimalCover(){
		return Collections.unmodifiableSet(minimalCoverSet);
	}
	
	/**
	 * Returns an boolean array indicating which variables belong to the minimal cover inequality C: {@code\sum_{i\in C} x_i <= |C|-1}, i.e. minimalCover[i] is true if
	 * variable i is in the cover.
	 * @return an boolean array indicating which variables belong to the minimal cover inequality C: {@code\sum_{i\in C} x_i <= |C|-1}, i.e. minimalCover[i] is true if
	 * variable i is in the cover.  
	 */
	public boolean[] getMinimalCoverMask(){
		return minimalCover;
	}
	/**
	 * Returns true if the cover inequality {@code\sum_{i\in C} x_i <= |C|-1} is violated
	 * @return Returns true if the cover inequality {@code\sum_{i\in C} x_i <= |C|-1} is violated
	 */
	public boolean isMinimalCoverViolated(){
		return minimalCoverIsViolated;
	}
	/**
	 * Returns evaluation of the LHS of the cover. If the cover is violated, than {@code LHS>RHS}
	 * @return evaluation of the LHS of the cover. If the cover is violated, than {@code LHS>RHS}
	 */
	public double getMinimalCoverLHS(){
		double value=0;
		for(int i : minimalCoverSet)
			value+=variableValues[i];
		return value;
	}
	/**
	 * returns the RHS of the minimal cover inequality
	 * @return returns the RHS of the minimal cover inequality
	 */
	public int getMinimalCoverRHS(){
		return minimalCoverRHS;
	}
	
	/**** Lifted cover functions ****/
	
	/**
	 * Returns an array of alpha coefficients of the lifted cover inequality
	 * @return an array of alpha coefficients of the lifted cover inequality
	 */
	public int[] getLiftedCoverCoefficients(){
		return liftedCoverCoefficients;
	}
	/**
	 * Returns evaluation of the LHS of the cover. If the cover is violated, than {@code LHS>RHS}
	 * @return evaluation of the LHS of the cover. If the cover is violated, than {@code LHS>RHS}
	 */
	public double getLiftedCoverLHS(){
		return liftedCoverLHS;
	}
	/**
	 * Returns the RHS of the lifted cover inequality
	 * @return the RHS of the lifted cover inequality
	 */
	public int getLiftedCoverRHS(){
		return liftedCoverRHS;
	}
	/**
	 * Returns true if the lifted cover inequality is violated
	 * @return true if the lifted cover inequality is violated
	 */
	public boolean isLiftedCoverViolated(){
		return liftedCoverIsViolated;
	}
	
}
