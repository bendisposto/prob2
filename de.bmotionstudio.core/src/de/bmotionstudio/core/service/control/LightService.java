package de.bmotionstudio.core.service.control;

import de.bmotionstudio.core.AbstractBControlService;
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.core.editor.part.LightPart;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Light;

public class LightService extends AbstractBControlService implements
		IBControlService {

	@Override
	public BControl createControl() {
		return new Light();
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new LightPart();
	}

	@Override
	public boolean showInPalette() {
		return false;
	}

	@Override
	public Class<?> getControlClass() {
		return Light.class;
	}

}
