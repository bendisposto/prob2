package de.prob.ui.stateview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.prob.model.representation.IEval;
import de.prob.ui.visualization.OpenFormula;

public class CreateFormulaVisualizationHandler extends AbstractHandler
		implements IHandler {

	private final Logger logger = LoggerFactory
			.getLogger(CreateFormulaVisualizationHandler.class);

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}

		IStructuredSelection sel = (IStructuredSelection) selection;
		Object x = sel.getFirstElement();
		if (x instanceof IEval) {
			logger.trace("Handler execution on {}", x);
			OpenFormula open = new OpenFormula();
			open.run(((IEval) x).getEvaluate());
		} else {
			logger.warn("Selection is not an IEval. Class is {}", x.getClass());
		}
		return null;
	}

}
