package de.bmotionstudio.core.model.event;

import de.bmotionstudio.core.model.control.BControl;
import de.prob.statespace.History;

public interface IEvent {

	public abstract void execute(History history, BControl control);

	public String getName();
	
	public void setName(String name);
	
	public String getDescription();
	
	public String getType();
	
}
