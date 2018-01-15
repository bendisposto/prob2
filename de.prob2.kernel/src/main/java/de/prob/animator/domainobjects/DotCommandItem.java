package de.prob.animator.domainobjects;

public class DotCommandItem {

	private final String command;
	
	private final String name;
	
	private final String description;
	
	private final int arity;
	
	private final boolean available;
	
	public DotCommandItem(String command, String name, String description, int arity, boolean available) {
		this.command = command;
		this.name = name;
		this.description = description;
		this.arity = arity;
		this.available = available;
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
	
	public boolean isAvailable() {
		return available;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
