package de.prob.model.eventb;

import java.util.List;

public class Event {
	String name;
	List<String> params;
	
	public Event(String name,List<String>params) {
		this.name = name;
		this.params = params;
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getParams() {
		return params;
	}
}
