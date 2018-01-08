package de.prob.animator.domainobjects;

public class DotCommandItem {

	private final String command;
	
	private final String name;
	
	private final String description;
	
	private final int arity;
	
	public DotCommandItem(String command, String name, String description, int arity) {
		this.command = command;
		this.name = name;
		this.description = description;
		this.arity = arity;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getArity() {
		return arity;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
