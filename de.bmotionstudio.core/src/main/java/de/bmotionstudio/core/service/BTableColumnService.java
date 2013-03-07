package de.bmotionstudio.core.service;

import de.bmotionstudio.core.AbstractBControlService;
import de.bmotionstudio.core.IBControlService;
import de.bmotionstudio.core.editor.part.BControlTreeEditPart;
import de.bmotionstudio.core.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.core.editor.part.BMSAbstractTreeEditPart;
import de.bmotionstudio.core.editor.part.TableColumnPart;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.TableColumn;

public class BTableColumnService extends AbstractBControlService implements
		IBControlService {

	@Override
	public BControl createControl() {
		return new TableColumn();
	}

	@Override
	public BMSAbstractEditPart createEditPart() {
		return new TableColumnPart();
	}

	@Override
	public boolean showInPalette() {
		return false;
	}

	@Override
	public BMSAbstractTreeEditPart createTreeEditPart() {
		return new BControlTreeEditPart() {
			@Override
			protected void createEditPolicies() {
			}
		};
	}

	@Override
	public Class<?> getControlClass() {
		return TableColumn.class;
	}

}
