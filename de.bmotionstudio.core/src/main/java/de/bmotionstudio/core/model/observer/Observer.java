/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.observer;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.PropertyChangeSupportObject;
import de.bmotionstudio.core.model.control.BControl;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.Trace;

/**
 * 
 * Observers are used to link controls to the model's state, i.e., they do the
 * same as the animation function in ProB. The main difference is, that we allow
 * to decompose the animation function into different aspects, i.e., if our
 * model contains information about the speed of a motor, we can separate all
 * information regarding the speed from the information regarding the
 * temperature. This allows us to write small functions and combine them rather
 * than writing a single function covering all aspects of the model.
 * 
 * @author Lukas Ladenberger
 * 
 */
public abstract class Observer extends PropertyChangeSupportObject implements
		IObserver {

	private String name;
	
	public Observer() {
		this.name = getType();
	}
	
	protected Object readResolve() {
		return this;
	}

	/**
	 * Makes a copy of the observer
	 * 
	 * @return the cloned observer
	 */
	public Observer clone() throws CloneNotSupportedException {
		return (Observer) super.clone();
	}

	/**
	 * Returns a corresponding wizard for the observer.
	 * 
	 * @param bcontrol
	 *            The corresponding control
	 * @return the corresponding wizard
	 */
	public abstract ObserverWizard getWizard(Shell shell, BControl control);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldVal = this.name;
		this.name = name;
		firePropertyChange("name", oldVal, name);
	}
	
	public String getDescription() {
		return null;
	}
	
	public List<IEvalElement> prepareObserver(Trace history,
			BControl control) {
		return Collections.emptyList();
	}
	
	public void afterCheck(Trace history, BControl control) {
	}

}
