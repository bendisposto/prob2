package de.prob.check;

public class CheckInterrupted implements IModelCheckingResult {

	@Override
	public String getMessage() {
		return "The model checking has been interrupted.";
	}

}
