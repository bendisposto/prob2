/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.bmotionstudio.core.editor.BControlCreationFactory;
import de.bmotionstudio.core.editor.part.BControlTreeEditPart;
import de.bmotionstudio.core.editor.part.BMSAbstractTreeEditPart;

/**
 * @author Lukas Ladenberger
 * 
 */
public abstract class AbstractBControlService {

	public ToolEntry createToolEntry(String name, String icon,
			String sourcePluginID, IBControlService service) {
		return new CombinedTemplateCreationEntry(name, "Create " + name,
				service, new BControlCreationFactory(service),
				AbstractUIPlugin
						.imageDescriptorFromPlugin(sourcePluginID, icon),
				AbstractUIPlugin
						.imageDescriptorFromPlugin(sourcePluginID, icon));
	}

	public boolean showInPalette() {
		return true;
	}

	public boolean showInOutlineView() {
		return true;
	}

	public BMSAbstractTreeEditPart createTreeEditPart() {
		return new BControlTreeEditPart();
	}

}
