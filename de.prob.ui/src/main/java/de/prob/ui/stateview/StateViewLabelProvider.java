package de.prob.ui.stateview;

import java.util.Map;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.IEval;
import de.prob.statespace.History;
import de.prob.statespace.StateId;

class StateViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	public History currentHistory;

	@Override
	public String getColumnText(final Object obj, final int index) {
		if (index == 0) {
			return obj.toString();
		}

		if (index == 1 && obj instanceof IEval) {
			return getValue(((IEval) obj).getEvaluate(),
					currentHistory.getCurrentState());
		}

		if (index == 2 && obj instanceof IEval) {
			return getValue(((IEval) obj).getEvaluate(), currentHistory
					.getCurrent().getSrc());
		}
		return "";
	}

	private String getValue(final IEvalElement o, final StateId state) {
		Map<IEvalElement, EvaluationResult> values = currentHistory
				.getStatespace().valuesAt(state);
		EvaluationResult result = values.get(o);
		return result != null ? result.value : "";
	}

	@Override
	public Image getColumnImage(final Object obj, final int index) {
		return null;
	}

	@Override
	public Image getImage(final Object obj) {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}

	public void setInput(final History currentHistory2) {
		currentHistory = currentHistory2;

	}

}