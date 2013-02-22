/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.service;

import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.bmotionstudio.core.AbstractBControlService;
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.editor.BControlCreationFactory;
import de.bmotionstudio.core.editor.part.BConnectionEditPart;
import de.bmotionstudio.core.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.core.model.control.BConnection;
import de.bmotionstudio.core.model.control.BControl;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BConnectionService extends AbstractBControlService implements
		IBControlService {

	@Override
	public ToolEntry createToolEntry(String name, String icon,
			String sourcePluginID, IBControlService service) {
		return new ConnectionCreationToolEntry(name, "Create " + name,
				new BControlCreationFactory(this),
				AbstractUIPlugin
						.imageDescriptorFromPlugin(sourcePluginID, icon),
				AbstractUIPlugin
						.imageDescriptorFromPlugin(sourcePluginID, icon));
	}

	@Override
	public BControl createControl() {
		return new BConnection();
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new BConnectionEditPart();
	}

	@Override
	public boolean showInPalette() {
		return false;
	}

	@Override
	public Class<?> getControlClass() {
		return BConnection.class;
	}

}
