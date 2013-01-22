/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.action;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;

import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.editor.command.AddObserverCommand;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.observer.Observer;

public class AddObserverAction extends SelectionAction {

	private String id;

	public AddObserverAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(true);
	}

	@Override
	protected void init() {
		setEnabled(false);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	@Override
	public void run() {
		
		BControl actionControl = getSelectedControl();

		if (actionControl != null) {

			try {

				IConfigurationElement observerExtension = BMotionEditorPlugin
						.getObserverExtension(getObserverId());
				Observer newObserver = (Observer) observerExtension
						.createExecutableExtension("class");
				
				ObserverWizard wizard = newObserver.getWizard(Display
						.getDefault().getActiveShell(), actionControl);

				if (wizard != null) {
					wizard.create();
					Point size = wizard.getSize();
					if (size == null)
						size = new Point(500, 300);
					wizard.getShell().setSize(size);
					wizard.getShell().setText("Create New Observer");
					wizard.setMessage(newObserver.getDescription());
					wizard.setTitle(newObserver.getType());
					wizard.setTitleImage(BMotionImage
							.getImage(BMotionImage.IMG_LOGO_BMOTION64));
					int open = wizard.open();
					if (open == StatusDialog.OK) {
						AddObserverCommand addObserverCommand = new AddObserverCommand(
								newObserver, actionControl);
						execute(addObserverCommand);
					}
				} else {
					// TODO: Reimplement me!!!
					// Logger.notifyUserWithoutBugreport("The Observer \""
					// + observer.getName()
					// + "\" does not support a wizard.");
				}

			} catch (CoreException e) {
				// TODO Handle expection!!!
				e.printStackTrace();
			}

		}

	}

	public void setObserverId(String id) {
		this.id = id;
	}

	public String getObserverId() {
		return id;
	}

	protected BControl getSelectedControl() {

		List<?> objects = getSelectedObjects();

		if (objects.isEmpty())
			return null;

		if ((objects.get(0) instanceof EditPart)) {
			EditPart part = (EditPart) objects.get(0);
			BControl control = null;
			if (part.getModel() instanceof BControl)
				control = (BControl) part.getModel();
			return control;
		}

		return null;

	}

}
