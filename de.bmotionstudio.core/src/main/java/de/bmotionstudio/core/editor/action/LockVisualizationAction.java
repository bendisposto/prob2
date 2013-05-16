/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.action;

import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.ui.IWorkbenchPart;

import de.bmotionstudio.core.ActionConstants;
import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.editor.VisualizationViewPart;
import de.bmotionstudio.core.editor.command.LockVisualizationCommand;
import de.bmotionstudio.core.model.VisualizationView;

public class LockVisualizationAction extends WorkbenchPartAction {

	private VisualizationViewPart visPart;

	public LockVisualizationAction(IWorkbenchPart part) {
		super(part);
		visPart = (VisualizationViewPart) part;
		setId(ActionConstants.ACTION_LOCK_VISUALIZATION);
		updateImage();
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	@Override
	public void run() {
		VisualizationView v = visPart.getVisualizationView();
		execute(new LockVisualizationCommand(v, !v.isLocked()));
	}

	private void updateImage() {
		if (visPart.getVisualizationView().isLocked())
			setImageDescriptor(BMotionImage
					.getImageDescriptor("icons/eclipse16/deadlock_view.gif"));
		else
			setImageDescriptor(BMotionImage
					.getImageDescriptor("icons/eclipse16/deadlock_view2.gif"));
	}

	@Override
	protected void refresh() {
		updateImage();
		super.refresh();
	}

}
