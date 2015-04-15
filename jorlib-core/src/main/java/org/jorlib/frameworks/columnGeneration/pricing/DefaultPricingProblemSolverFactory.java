package org.jorlib.frameworks.columnGeneration.pricing;

import java.lang.reflect.InvocationTargetException;

import org.jorlib.frameworks.columnGeneration.colgenMain.Column;

/**
 * Factory class for instantiating new pricing problems
 * @author jkinable
 *
 */
public final class  DefaultPricingProblemSolverFactory<T,U extends Column<T,U,V>, V extends AbstractPricingProblem<T,U,V>> implements PricingProblemSolverFactory<T,U,V>{
	
	private final Class<? extends PricingProblemSolver<T, U, V>> solverClass;
	private final String solverName;
	private final T dataModel;
	
	
	//T dataModel, String name, V pricingProblem
	//public PricingProblemSolver(T dataModel, String name, V pricingProblem)
	
	public DefaultPricingProblemSolverFactory(Class<? extends PricingProblemSolver<T, U, V>> solverClass, String solverName, T dataModel){
		this.solverClass=solverClass;
		this.solverName=solverName;
		this.dataModel=dataModel;
	}
	
	public PricingProblemSolver<T, U, V> createSolverInstance(V pricingProblem){
		Class<?>[] cArg = new Class[3]; //Our constructor has 3 arguments
		cArg[0] = dataModel.getClass(); //First argument is of *object* type Long
		cArg[1] = String.class; //Second argument is of *object* type String
		cArg[2] = pricingProblem.getClass(); //Third argument is of *primitive* type int

		PricingProblemSolver<T, U, V> solverInstance=null;
		try {
			solverInstance=solverClass.getDeclaredConstructor(cArg).newInstance(dataModel, solverName, pricingProblem);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		return solverInstance;
	}
}
