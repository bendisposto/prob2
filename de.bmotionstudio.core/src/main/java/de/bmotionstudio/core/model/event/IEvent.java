package de.bmotionstudio.core.model.event;

import de.bmotionstudio.core.model.control.BControl;
import de.prob.statespace.Trace;

public interface IEvent {

	public abstract void execute(Trace history, BControl control);

	public String getName();
	
	public void setName(String name);
	
	public String getDescription();
	
	public String getType();
	
}
