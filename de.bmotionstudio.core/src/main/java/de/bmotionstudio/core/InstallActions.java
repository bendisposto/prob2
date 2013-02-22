/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core;

import org.eclipse.ui.part.WorkbenchPart;

import de.bmotionstudio.core.editor.action.BringToBottomAction;
import de.bmotionstudio.core.editor.action.BringToBottomStepAction;
import de.bmotionstudio.core.editor.action.BringToTopAction;
import de.bmotionstudio.core.editor.action.BringToTopStepAction;
import de.bmotionstudio.core.editor.action.FitImageAction;
import de.bmotionstudio.core.editor.action.RenameAction;

public class InstallActions extends AbstractInstallActions implements
		IInstallActions {

	public void installActions(final WorkbenchPart part) {
		installAction(RenameAction.ID, new RenameAction(part));
		installAction(FitImageAction.ID, new FitImageAction(part));
		installAction(BringToTopAction.ID, new BringToTopAction(part));
		installAction(BringToBottomAction.ID, new BringToBottomAction(part));
		installAction(BringToTopStepAction.ID, new BringToTopStepAction(part));
		installAction(BringToBottomStepAction.ID, new BringToBottomStepAction(
				part));
	}

}
