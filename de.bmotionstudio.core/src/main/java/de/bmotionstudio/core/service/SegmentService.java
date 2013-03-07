/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.service;

import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.ToolEntry;

import de.bmotionstudio.core.AbstractBControlService;
import de.bmotionstudio.core.BMotionImage;
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.editor.BControlCreationFactory;
import de.bmotionstudio.core.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.core.editor.part.SegmentPart;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Segment;

/**
 * @author Lukas Ladenberger
 * 
 */
public class SegmentService extends AbstractBControlService implements
		IBControlService {

	@Override
	public ToolEntry createToolEntry(String name, String icon,
			String sourcePluginID, IBControlService service) {
		return new ConnectionCreationToolEntry(name, "Create " + name,
				new BControlCreationFactory(this),
				BMotionImage.getImageDescriptor(sourcePluginID, icon),
				BMotionImage.getImageDescriptor(sourcePluginID, icon));
	}

	@Override
	public BControl createControl() {
		return new Segment();
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new SegmentPart();
	}

	@Override
	public Class<?> getControlClass() {
		return Segment.class;
	}

}
