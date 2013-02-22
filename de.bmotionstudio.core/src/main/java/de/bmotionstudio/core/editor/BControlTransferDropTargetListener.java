/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.requests.CreationFactory;

import de.bmotionstudio.core.IBControlService;

public class BControlTransferDropTargetListener extends
		TemplateTransferDropTargetListener {

	private CreationFactory factory = null;

	public BControlTransferDropTargetListener(EditPartViewer viewer) {
		super(viewer);
	}

	@Override
	protected CreationFactory getFactory(Object template) {

		if (template != null && template instanceof IBControlService) {
			IBControlService service = (IBControlService) template;
			factory = new BControlCreationFactory(service);
		}

		return factory;

	}

}
