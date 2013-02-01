package de.bmotionstudio.core.model.event;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.PropertyChangeSupportObject;
import de.bmotionstudio.core.model.control.BControl;

public abstract class Event extends PropertyChangeSupportObject implements
		IEvent {

	private transient String type;
	private transient String description;
	private transient String ID;

	private String name;

	public Event() {
		init();
	}

	protected Object readResolve() {
		init();
		return this;
	}

	/**
	 * This method initializes the observer. Gets the ID, name and description
	 * from the corresponding extension point
	 */
	private void init() {
		IConfigurationElement configElement = BMotionEditorPlugin
				.getObserverExtension(getClass().getName());
		if (configElement != null) {
			this.setID(configElement.getAttribute("class"));
			this.setName(configElement.getAttribute("name"));
			this.setDescription(configElement.getAttribute("description"));
		}
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
	public abstract ObserverWizard getWizard(Shell shell, BControl control);

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		String oldVal = this.description;
		this.description = description;
		firePropertyChange("description", oldVal, description);
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		String oldVal = this.ID;
		this.ID = ID;
		firePropertyChange("ID", oldVal, ID);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldVal = this.name;
		this.name = name;
		firePropertyChange("name", oldVal, name);
	}
	
}
