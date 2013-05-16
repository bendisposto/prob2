/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.wizard.event;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.event.Event;

/**
 * 
 * The BMotion Studio provides an easy way to handle Observers. For this,
 * Observers can have a corresponding wizard. The user can open it by calling
 * the context menu of a Control.
 * 
 * @author Lukas Ladenberger
 * 
 */
public abstract class EventWizard extends TitleAreaDialog {

	private Event event;
	
	private BControl control;

	public EventWizard(Shell shell, BControl control, Event event) {
		super(shell);
		this.control = control;
		this.event = event;
	}

	public Event getEvent() {
		return this.event;
	}

	public abstract Point getSize();

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(newShell, event.getClass().getName());
	}

	public BControl getControl() {
		return control;
	}

	public void setControl(BControl control) {
		this.control = control;
	}
	
	@Override
	public Control createDialogArea(Composite parent) {
		return super.createDialogArea(parent);
	}

}
