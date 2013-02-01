package de.bmotionstudio.core.model.event;

import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.core.Animation;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.control.BControl;

public class ExecuteOperation extends Event {

	private String operation;
	
	private String predicate;
	
	@Override
	public void execute(Animation animation, BControl bcontrol) {
	}
	
	@Override
	public ObserverWizard getWizard(Shell shell, BControl control) {
		return null;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		String oldVal = this.operation;
		this.operation = operation;
		firePropertyChange("operation", oldVal, operation);
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		String oldVal = this.predicate;
		this.predicate = predicate;
		firePropertyChange("predicate", oldVal, predicate);
	}

}
