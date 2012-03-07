package de.prob.model.languages;

public class Operation implements Name {

	private String identifier;
	private Predicate guard;
	
	public Operation(String identifier, Predicate guard) {
		this.identifier = identifier;
		this.guard = guard;
	}
	
	@Override
	public String getIdentifier() {
		return identifier;
	}
	
	public Predicate getGuard() {
		return guard;
	}	
}
