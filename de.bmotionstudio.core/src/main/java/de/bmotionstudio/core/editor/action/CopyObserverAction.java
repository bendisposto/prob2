/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import de.bmotionstudio.core.editor.command.CopyObserverCommand;
import de.bmotionstudio.core.model.observer.Observer;

public class CopyObserverAction extends SelectionAction {

	private List<Observer> list = new ArrayList<Observer>();
	
	public CopyObserverAction(IWorkbenchPart part) {
		super(part);
		// force calculateEnabled() to be called in every context
		setLazyEnablementCalculation(true);
	}

	@Override
	protected void init() {
		super.init();
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();
		setText("Copy");
		setId(ActionFactory.COPY.getId());
		setHoverImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setDisabledImageDescriptor(sharedImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		setEnabled(false);
	}

	private Command createCopyCommand() {
		if (list == null || list.isEmpty())
			return null;
		CopyObserverCommand cmd = new CopyObserverCommand(list);
		return cmd;
	}

	@Override
	protected boolean calculateEnabled() {
		Command cmd = createCopyCommand();
		if (cmd == null)
			return false;
		return cmd.canExecute();
	}

	@Override
	public void run() {
		Command cmd = createCopyCommand();
		if (cmd != null && cmd.canExecute()) {
			cmd.execute();
		}
	}
	
	public void setList(List<Observer> list) {
		this.list = list;
	}
	
}
