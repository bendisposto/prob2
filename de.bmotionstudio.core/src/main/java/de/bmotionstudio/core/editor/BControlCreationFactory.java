/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor;

import org.eclipse.gef.requests.CreationFactory;

import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.model.control.BControl;

public class BControlCreationFactory implements CreationFactory {

	private IBControlService service;

	public BControlCreationFactory(IBControlService service) {
		this.service = service;
	}

	@Override
	public Object getNewObject() {
		return service.createControl();
	}

	@Override
	public Object getObjectType() {
		return BControl.class;
	}

}
