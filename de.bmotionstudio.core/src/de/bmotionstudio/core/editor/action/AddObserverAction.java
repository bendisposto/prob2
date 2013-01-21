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
import de.bmotionstudio.core.editor.command.AddObserverCommand;
import de.bmotionstudio.core.editor.wizard.observer.ObserverWizard;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.observer.Observer;

public class AddObserverAction extends SelectionAction {

	private String className;

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
						.getObserverExtension(className);
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
					String title = "Observer: " + newObserver.getName()
							+ " Control: " + actionControl.getID();
					wizard.getShell().setText(title);
					// wizard.setWindowTitle("BMotion Studio Observer Wizard");
					// wizard.setTitle(title);
					// wizard.setMessage(observer.getDescription());
					// wizard.setTitleImage(BMotionStudioImage
					// .getImage(BMotionStudioImage.IMG_LOGO_BMOTION64));
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

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
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
