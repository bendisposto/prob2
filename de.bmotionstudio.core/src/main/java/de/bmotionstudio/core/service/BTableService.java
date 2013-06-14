package de.bmotionstudio.core.service;

import de.bmotionstudio.core.AbstractBControlService;
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.core.editor.part.TablePart;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Table;

public class BTableService extends AbstractBControlService implements
		IBControlService {

	@Override
	public BControl createControl() {
		return new Table();
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new TablePart();
	}

	@Override
	public Class<?> getControlClass() {
		return Table.class;
	}

}
