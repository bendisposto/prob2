package de.prob.model.languages;

public class Constant implements Name {

	private String identifier;
	
	public Constant(String identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}
	
	
}
