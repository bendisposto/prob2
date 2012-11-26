package de.prob.ui.animationsview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.inject.Injector;

import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.webconsole.ServletContextListener;

public class RemoveHistoryHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		History h = null;
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
			return null;

		IStructuredSelection sel = (IStructuredSelection) selection;
		Object x = sel.getFirstElement();
		if (!(x instanceof History))
			return null;

		h = (History) x;

		Injector injector = ServletContextListener.INJECTOR;

		AnimationSelector animationSelector = injector
				.getInstance(AnimationSelector.class);

		if (h != null) {
			animationSelector.remove(h);
			animationSelector.refresh();
		}
		return null;
	}

}
