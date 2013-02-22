package de.bmotionstudio.core.model.event;

import de.bmotionstudio.core.Animation;
import de.bmotionstudio.core.model.control.BControl;

public interface IEvent {

	public abstract void execute(Animation animation, BControl bcontrol);

	public String getName();
	
	public void setName(String name);
	
	public String getDescription();
	
	public String getID();
	
}
