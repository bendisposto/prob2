package de.prob.model.languages;

public class Predicate implements Name {

	private String identifier;
	private String predicate;
	
	public Predicate(String identifier, String predicate) {
		this.identifier = identifier;
		this.predicate = predicate;
	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}
	
	public String getPredicate() {
		return predicate;
	}

}
