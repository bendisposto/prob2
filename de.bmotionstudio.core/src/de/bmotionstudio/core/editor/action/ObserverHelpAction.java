/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.action;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

import de.bmotionstudio.core.BMotionImage;

public class ObserverHelpAction extends Action {

	public static String ID = "de.bmotionstudio.action.observer.help";
	
	private String observerID;

	public ObserverHelpAction() {
		setText("Show help...");
		setImageDescriptor(BMotionImage
				.getImageDescriptor("icons/eclipse16/linkto_help.gif"));
		setEnabled(false);
	}

	@Override
	public void run() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(observerID);
	}

	public void setObserverID(String observerID) {
		this.observerID = observerID;
	}
	
	@Override
	public String getId() {
		return ID;
	}

}
