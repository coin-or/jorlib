/* ==========================================
 * jORLib : a free Java OR library
 * ==========================================
 *
 * Project Info:  https://github.com/jkinable/jorlib
 * Project Creator:  Joris Kinable (https://github.com/jkinable)
 *
 * (C) Copyright 2015, by Joris Kinable and Contributors.
 *
 * This program and the accompanying materials are licensed under GPLv3
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jorlib.alg.knapsack.BinaryKnapsack;
import org.jorlib.alg.knapsack.KnapsackAlgorithm;


/**
 * This class calculates both Lifted and Minimal Cover inequalities for binary knapsack constraints.
 * The algorithms used to separate violated inequalities are provided in: 
 * Nemhauser, G.L., Wolsey, L.A., Integer and combinatorial optimization. 1999, John Wiley & Sons
 * Given a knapsack constraint: \sum_i a_i x_i <= b where x_i are binary variables, b a positive integer and a_i integer coefficients.
 * Let N be the set of variables in the knapsack constraint.
 * For a given assignment of values to the variables, this class computes two types of violated cover inequalities:
 * 1. Minimal covers: \sum_{j\in C} \leq |C|-1, where C\subseteq N, \sum_{j\in C} a_i >b
 * 2. Lifted covers: \sum_{j\in N\setminus C} \alpha_jx_j + \sum_{j\in C2} \gamma_jx_j + \sum_{j\in C1} x_j \leq |C1|-1+\sum_{j\in C2}\gamma_j, where
 *    C1 \cap C2= \emptyset, C1 \cup C2= C, C a minimal cover as defined above.
 *    
 * NOTE: Separating violated Lifted Cover Inequalities is NP-hard. Hence we rely on lifting and a separation heuristic. 
 * First we attempt to find a violated Lifted Cover inequality with C2=\emptyset. If we can't find such an inequality, we set C2 to:
 * C2={k}, k=arg max_{j\in C}a_j variableValue[j], and C1=C\setminus C2, and retry.
 * 
 * Note: The precision of the calculations are accurate up to 0.000001.
 *  
 * @author Joris Kinable
 * @since April 8, 2015
 *
 */
public class LiftedCoverInequalitySeparator {
	
	public static final double PRECISION=0.000001;
	
	//Knapsack solver used by the separator
	private final KnapsackAlgorithm knapsackAlgorithm;
	
	//Knapsack constraint \sum_{i=0}^n a_ix_i \leq b
	private int nrVars; //Number of variables
	private int[] knapsackCoefficients;
	private int b;
	private double[] variableValues;
	
	//COVER INEQUALITIES
	private boolean coverInequalityExists; //Indicates whether a cover inequality exists. If \sum_i a_i \leq b, then no cover exists and hence no inequality can be generated.
	
	//Minimal cover
	private double minimalCoverLHS=0; //THIS VAR IS NOT SET Evaluation of the left hand side of the equality
	private int minimalCoverRHS; //Right hand side of the minimal cover inequality
	private boolean[] minimalCover; //Boolean array indicating whether a variable is in the minimal cover 
	private Set<Integer> minimalCoverSet; //Set containing the variables which are part of the minimal cover
	private boolean minimalCoverIsViolated; //Returns true if LHS > minimalCoverSize-1 for the given variableValues
	
	//Lifted cover
	private double liftedCoverLHS=0; // Evaluation of the left hand side of the equality
	private int liftedCoverRHS; //Right hand side of lifted cover inequality
	private int[] liftedCoverCoefficients; //Coefficients of variables in lifted cover inequality
	private boolean liftedCoverIsViolated;
	
	/**
	 * @param knapsackAlgorithm This separator requires an algorithm to solve knapsack problems
	 */
	public LiftedCoverInequalitySeparator(KnapsackAlgorithm knapsackAlgorithm){
		this.knapsackAlgorithm=knapsackAlgorithm;
	}
	
	/**
	 * Given a knapsack constraint: \sum_{i=0}^n a_ix_i \leq b This method separates minimal Cover Inequalities, i.e. it will search for a valid cover
	 * \sum_{i\in C} x_i \leq |C|-1 which is violated by the current variable values
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
	 * Given a knapsack constraint: \sum_{i=0}^n a_ix_i \leq b This method separates Lifted Cover Inequalities, i.e. it will search for a valid lifted cover inequality
	 * \sum_{j\in N\setminus C} \alpha_jx_j + \sum_{j\in C2} \gamma_jx_j + \sum_{j\in C1} x_j \leq |C1|-1+\sum_{j\in C2}\gamma_j which is violated by the current
	 *  variable values, where C1 \cap C2= \emptyset, C1 \cup C2= C, C a minimal cover
	 * @param knapsackCoefficients a_i
	 * @param b right hand side of the knapsack constraint
	 * @param variableValues values of the x_i variables
	 * @param performDownLifting When set to true, additional effort is performed to find a violated Lifted Cover Inequality. When this value is false, C2 will be an empty set.
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
	 * S=min \sum_{i=0}^n (1-variableValues[i]) z_i
	 *    s.t. \sum_{i=0}^n a_iz_i \geq b+1
	 *    z_i binary
	 *    
	 * if S<1, the cover is violated.
	 * 
	 * Note: Instead of solving the above problem, we transform it into a knapsack problem by substituting z_i=1-y_i, i.e. we solve:
	 * max  \sum_{i=0}^n (1-variableValues[i])y_i - \sum_{i=0}^n (1-variableValues[i])
	 * s.t. \sum_{i=0}^n a_i y_i \leq \sum_{i=0}^n a_i -b-1
	 *      y_i binary
	 *      
	 * The desired z_i values can be obtained: z_i=1-y_i
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
		minimalCoverSet=new LinkedHashSet<Integer>();
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
		Set<Integer> Lk=new LinkedHashSet<Integer>(); //Variable ids of N/C, where N is the complete set of vars.
		List<Integer> NminLk=new ArrayList<Integer>(); //List maintaining N\L1
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
		
		int iteration=1;
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
			
			iteration++;
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
		
		this.computeLiftedCover(new HashSet<Integer>(Arrays.asList(k)));
		
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
	 * @return Indicates whether a cover inequality exists, i.e. returns whether \sum_i a_i > b. You should NOT ask for a minimal or lifted cover if this function returns false!
	 */
	public boolean coverInequalityExists(){
		return coverInequalityExists;
	}
	
	/**
	 * @return Returns C: the variables that are in the minimal cover
	 */
	public Set<Integer> getMinimalCover(){
		return Collections.unmodifiableSet(minimalCoverSet);
	}
	
	/**
	 * @return Returns an boolean array indicating which variables belong to the minimal cover inequality C: \sum_{i\in C} x_i <= |C|-1, i.e. minimalCover[i] is true if
	 * variable i is in the cover.  
	 */
	public boolean[] getMinimalCoverMask(){
		return minimalCover;
	}
	/**
	 * @return Returns true if the cover inequality \sum_{i\in C} x_i <= |C|-1 is violated
	 */
	public boolean isMinimalCoverViolated(){
		return minimalCoverIsViolated;
	}
	/**
	 * @return Returns evaluation of the LHS of the cover. If the cover is violated, than LHS>RHS
	 */
	public double getMinimalCoverLHS(){
		double value=0;
		for(int i : minimalCoverSet)
			value+=variableValues[i];
		return value;
	}
	/**
	 * @return returns the RHS of the minimal cover inequality
	 */
	public int getMinimalCoverRHS(){
		return minimalCoverRHS;
	}
	
	/**** Lifted cover functions ****/
	
	/**
	 * @return returns an array of alpha coefficients of the lifted cover inequality
	 */
	public int[] getLiftedCoverCoefficients(){
		return liftedCoverCoefficients;
	}
	/**
	 * @return Returns evaluation of the LHS of the cover. If the cover is violated, than LHS>RHS
	 */
	public double getLiftedCoverLHS(){
		return liftedCoverLHS;
	}
	/**
	 * @return returns the RHS of the lifted cover inequality
	 */
	public int getLiftedCoverRHS(){
		return liftedCoverRHS;
	}
	/**
	 * @return Returns true if the lifted cover inequality is violated
	 */
	public boolean isLiftedCoverViolated(){
		return liftedCoverIsViolated;
	}
	
	/*public static void main(String[] args){
		LiftedCoverInequalitySeparator separator= new LiftedCoverInequalitySeparator();
		
		//Example 1 - Violated cover: x1+x7<=1, minimalCoverValue: 0.29
		double[] variableValues1={0.71, 0, 0.35, 1, 1, 0, 1, 1, 0};
		int[] knapsackCoefficients1={774, 76, 22, 42, 21, 760, 818, 62, 785};
		int b1=1500;
		
		separator.separateMinimalCover(variableValues1.length, knapsackCoefficients1, b1, variableValues1);
		System.out.println("Example 1:");
		System.out.println("Cover: "+Arrays.toString(separator.getMinimalCoverMask()));
		System.out.println("isViolated: "+separator.isMinimalCoverViolated()+"\n");
		
		
		//Example 2 - Violated cover: x3+x7<=1, minimalCoverValue: 0.65
		double[] variableValues2={0.71, 0, 0.35, 1, 1, 0, 1, 1, 0};
		int[] knapsackCoefficients2={67, 27, 794, 53, 234, 32, 797, 97, 435};
		int b2=1500;
		
		separator.separateMinimalCover(variableValues2.length, knapsackCoefficients2, b2, variableValues2);
		System.out.println("Example 2:");
		System.out.println("Cover: "+Arrays.toString(separator.getMinimalCoverMask()));
		System.out.println("isViolated: "+separator.isMinimalCoverViolated()+"\n");
		
		
		//Example 3 - Violated cover: x3+x4+x5<=2, minimalCoverValue: 7/53=0.13207547169
		double[] variableValues3={0, 0, 1, 1, 46.0/53};
		int[] knapsackCoefficients3={47, 45, 79, 53, 53};
		int b3=178;
		
		separator.separateMinimalCover(variableValues3.length, knapsackCoefficients3, b3, variableValues3);
		System.out.println("Example 3:");
		System.out.println("Cover: "+Arrays.toString(separator.getMinimalCoverMask()));
		System.out.println("isViolated: "+separator.isMinimalCoverViolated()+"\n");
		
		
		//Example 4 - minimal cover: x3+x4+x5<=2, minimalCoverValue: 1, NO violation
		double[] variableValues4={.5, .5, 1, .5, .5};
		int[] knapsackCoefficients4={47, 45, 79, 53, 53};
		int b4=178;
		
		separator.separateMinimalCover(variableValues4.length, knapsackCoefficients4, b4, variableValues4);
		System.out.println("Example 4:");
		System.out.println("Cover: "+Arrays.toString(separator.getMinimalCoverMask()));
		System.out.println("isViolated: "+separator.isMinimalCoverViolated()+"\n");
		
		
		//Example 5 - violated minimal cover: x1+x7<=1, violated lifted cover: x1+x6+x7+x9<=1
		double[] variableValues5={0.71, 0, 0.35, 1, 1, 0, 1, 1, 0};
		int[] knapsackCoefficients5={774, 76, 22, 42, 21, 760, 818, 62, 785};
		int b5=1500;
		
		separator.separateLiftedCover(variableValues5.length, knapsackCoefficients5, b5, variableValues5);
		System.out.println("Example 5:");
		System.out.println("Minimal Cover: "+Arrays.toString(separator.getMinimalCoverMask()));
		System.out.println("minimal cover isViolated: "+separator.isMinimalCoverViolated()+"\n");
		System.out.println("Lifted Cover: "+Arrays.toString(separator.getLiftedCoverCoefficients())+" RHS: "+separator.getLiftedCoverRHS());
		System.out.println("lifted cover isViolated: "+separator.liftedCoverIsViolated+"\n");
		
		
		//Example 6 - minimal cover: x3+x4+x5<=2 (NOT violated), violated lifted cover: x1+2x3+x4+x5<=3
		double[] variableValues6={.5, .5, 1, .5, .5};
		int[] knapsackCoefficients6={47, 45, 79, 53, 53};
		int b6=178; //178
		
		separator.separateLiftedCover(variableValues6.length, knapsackCoefficients6, b6, variableValues6);
		System.out.println("Example 6:");
		System.out.println("Minimal Cover: "+Arrays.toString(separator.getMinimalCoverMask()));
		System.out.println("minimal cover isViolated: "+separator.isMinimalCoverViolated()+"\n");
		System.out.println("Lifted Cover: "+Arrays.toString(separator.getLiftedCoverCoefficients())+" RHS: "+separator.getLiftedCoverRHS());
		System.out.println("lifted cover isViolated: "+separator.liftedCoverIsViolated+"\n");
	}*/
	
	public static void main(String[] args){
		KnapsackAlgorithm knapsackAlgorithm=new BinaryKnapsack();
		LiftedCoverInequalitySeparator separator= new LiftedCoverInequalitySeparator(knapsackAlgorithm);
		double[] variableValues6={0.8333333333333334, 0.8333333333333333};
		int[] knapsackCoefficients6={3,3};
		int b6=5;
		
		separator.separateLiftedCover(variableValues6.length, knapsackCoefficients6, b6, variableValues6, true);
		System.out.println("Example 6:");
		System.out.println("Minimal Cover: "+Arrays.toString(separator.getMinimalCoverMask()));
		System.out.println("minimal cover isViolated: "+separator.isMinimalCoverViolated()+"\n");
		System.out.println("Lifted Cover: "+Arrays.toString(separator.getLiftedCoverCoefficients())+" RHS: "+separator.getLiftedCoverRHS());
		System.out.println("lifted cover isViolated: "+separator.liftedCoverIsViolated+"\n");
	}
}
