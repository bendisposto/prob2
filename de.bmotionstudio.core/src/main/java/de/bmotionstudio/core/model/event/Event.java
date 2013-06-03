package de.bmotionstudio.core.model.event;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.editor.wizard.event.EventWizard;
import de.bmotionstudio.core.model.PropertyChangeSupportObject;
import de.bmotionstudio.core.model.control.BControl;
import de.prob.statespace.Trace;

public abstract class Event extends PropertyChangeSupportObject implements
		IEvent {

	public static final transient String CLICK_ACTION = "click";
	public static final transient String MOUSEOVER_ACTION = "mouseover";
	
	private String name;
	
	private String action;

	public Event() {
		this.name = getType();
		this.action = CLICK_ACTION;
	}
	
	protected Object readResolve() {
		return this;
	}

	/**
	 * Makes a copy of the event
	 * 
	 * @return the cloned event
	 */
	public Event clone() throws CloneNotSupportedException {
		return (Event) super.clone();
	}

	/**
	 * Returns a corresponding wizard for the observer.
	 * 
	 * @param bcontrol
	 *            The corresponding control
	 * @return the corresponding wizard
	 */
	public abstract EventWizard getWizard(Shell shell, BControl control);

	public String getDescription() {
		return null;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldVal = this.name;
		this.name = name;
		firePropertyChange("name", oldVal, name);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		String oldVal = this.action;
		this.action = action;
		firePropertyChange("action", oldVal, action);
	}

	public String getTooltipText(Trace history, BControl control) {
		return "";
	}
	
}
