/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.wizard.observer;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.observer.Observer;

/**
 * 
 * The BMotion Studio provides an easy way to handle Observers. For this,
 * Observers can have a corresponding wizard. The user can open it by calling
 * the context menu of a Control.
 * 
 * @author Lukas Ladenberger
 * 
 */
public abstract class ObserverWizard extends TitleAreaDialog {

	private Observer observer;
	
	private BControl control;

	public ObserverWizard(Shell shell, BControl control, Observer observer) {
		super(shell);
		this.control = control;
		this.observer = observer;
	}

	public Observer getObserver() {
		return this.observer;
	}

	public abstract Point getSize();
	
//	@Override
//	public String getName() {
//		return observer.getName();
//	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(newShell, observer.getID());
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
