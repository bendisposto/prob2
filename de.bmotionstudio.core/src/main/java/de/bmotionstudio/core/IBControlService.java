/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core;

import org.eclipse.gef.palette.ToolEntry;

import de.bmotionstudio.core.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.core.editor.part.BMSAbstractTreeEditPart;
import de.bmotionstudio.core.model.control.BControl;

/**
 * @author Lukas Ladenberger
 * 
 */
public interface IBControlService {

	public BControl createControl();

	public BMSAbstractEditPart createEditPart();

	public BMSAbstractTreeEditPart createTreeEditPart();

	public ToolEntry createToolEntry(String name, String icon,
			String sourcePluginID, IBControlService service);

	public boolean showInPalette();
	
	public boolean showInOutlineView();
	
	public Class<?> getControlClass();

}