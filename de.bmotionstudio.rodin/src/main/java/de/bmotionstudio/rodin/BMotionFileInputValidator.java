/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.rodin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.IInputValidator;

import de.bmotionstudio.core.BMotionEditorPlugin;

public class BMotionFileInputValidator implements IInputValidator {

	private IProject prj;

	public BMotionFileInputValidator(IProject prj) {
		this.prj = prj;
	}

	public String isValid(String newText) {
		IResource res = prj.getFile(newText + "."
				+ BMotionEditorPlugin.FILEEXT_STUDIO);
		if (res != null && res.exists())
			return "The BMotion Studio filename must be unique in a project.";
		return null;
	}

}
