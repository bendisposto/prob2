package de.bmotionstudio.core.service;

import de.bmotionstudio.core.AbstractBControlService;
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.core.editor.part.SignalPart;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Signal;

public class SignalService extends AbstractBControlService implements
		IBControlService {

	@Override
	public BControl createControl() {
		return new Signal();
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new SignalPart();
	}

	@Override
	public Class<?> getControlClass() {
		return Signal.class;
	}

}
