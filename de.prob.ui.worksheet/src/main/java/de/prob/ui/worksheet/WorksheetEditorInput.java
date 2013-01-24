package de.prob.ui.worksheet;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class WorksheetEditorInput implements IEditorInput {

	private static int createdInputs=0;
	private int myId;
	public WorksheetEditorInput() {
		//TODO add method for setting the name ids correctly (allways 0..x )
		createdInputs++;
		myId=createdInputs;
	}
	
	@Override
	public Object getAdapter(Class adapter) {
		//TODO find out if IAdapter needs to be filled
		return null;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		//TODO add ImageDescriptor
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Worksheet ("+myId+")";
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return "Worksheet Editor (not stored in a file)";
	}

}
