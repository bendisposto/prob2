package de.prob.ui.animationsview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.statespace.History;

public class RemoveHistoryHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		History h = null;
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}

		IStructuredSelection sel = (IStructuredSelection) selection;
		Object x = sel.getFirstElement();
		if (!(x instanceof History)) {
			return null;
		}

		h = (History) x;

		h.notifyHistoryRemoval();
		return null;
	}

}
