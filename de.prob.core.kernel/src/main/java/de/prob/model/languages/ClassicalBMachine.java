package de.prob.model.languages;

import java.util.List;

public class ClassicalBMachine implements IModel {

	private List<Variable> variables;
	private List<Constant> constants;
	private List<Predicate> predicates;
	private List<Operation> operations;
	
	public List<Constant> getConstants() {
		return constants;
	}
	
	public List<Variable> getVariables() {
		return variables;
	}
	
	public List<Predicate> getPredicates() {
		return predicates;
	}
	
	public List<Operation> getOperations() {
		return operations;
	}
}
