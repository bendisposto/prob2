/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.service;

import de.bmotionstudio.core.AbstractBControlService;
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.core.editor.part.SwitchPart;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Switch;

public class SwitchService extends AbstractBControlService implements
		IBControlService {

	@Override
	public BControl createControl() {
		return new Switch();
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new SwitchPart();
	}

	@Override
	public Class<?> getControlClass() {
		return Switch.class;
	}

}
