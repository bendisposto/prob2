package de.prob.ui.stateview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.model.representation.IEval;
import de.prob.ui.visualization.OpenFormula;

public class CreateFormulaVisualizationHandler extends AbstractHandler
		implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}

		IStructuredSelection sel = (IStructuredSelection) selection;
		Object x = sel.getFirstElement();
		if (x instanceof IEval) {
			System.out.println(x);
			OpenFormula open = new OpenFormula();
			open.run(((IEval) x).getEvaluate());
		} else {
			System.out.println(x.getClass());
		}
		return null;
	}

}
