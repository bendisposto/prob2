package de.prob.animator.domainobjects;

import java.util.List;
import java.util.Objects;

public class DynamicCommandItem {

	private final String command;
	
	private final String name;
	
	private final String description;
	
	private final int arity;
	
	private final List<String> relevantPreferences;
	
	private final String available;
	
	public DynamicCommandItem(String command, String name, String description, int arity, List<String> relevantPreferences, String available) {
		this.command = command;
		this.name = name;
		this.description = description;
		this.arity = arity;
		this.relevantPreferences = relevantPreferences;
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
	
	public List<String> getRelevantPreferences() {
		return relevantPreferences;
	}
	
	public String getAvailable() {
		return available;
	}
	
	public boolean isAvailable() {
		return "available".equals(available);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		final DynamicCommandItem other = (DynamicCommandItem)obj;
		return Objects.equals(this.getCommand(), other.getCommand()) && this.getArity() == other.getArity();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.getCommand(), this.getArity());
	}
	
	@Override
	public String toString() {
		return name;
	}
}
