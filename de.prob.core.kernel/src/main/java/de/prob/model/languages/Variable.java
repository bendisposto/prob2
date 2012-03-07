package de.prob.model.languages;

public class Variable implements Name {

	private String identifier;
	
	public Variable(String identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}

}
