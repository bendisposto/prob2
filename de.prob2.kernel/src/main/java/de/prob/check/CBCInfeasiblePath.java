package de.prob.check;

public class CBCInfeasiblePath implements IModelCheckingResult {

	@Override
	public String getMessage() {
		return "Infeasible Path";
	}
	
	@Override
	public String toString() {
		return getMessage();
	}

}
