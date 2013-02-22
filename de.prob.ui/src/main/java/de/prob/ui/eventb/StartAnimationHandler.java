package de.prob.ui.eventb;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

public class StartAnimationHandler extends AbstractHandler {

	private ISelection fSelection;

	private StartAnimationProgressBar animationProgressBar;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		fSelection = HandlerUtil.getCurrentSelection(event);

		final IEventBRoot rootElement = getRootElement();

		if (animationProgressBar != null)
			animationProgressBar.kill();
		animationProgressBar = new StartAnimationProgressBar(Display
				.getDefault().getActiveShell(), rootElement);
		animationProgressBar.initGuage();
		animationProgressBar.open();

		return null;

	}

	private IEventBRoot getRootElement() {
		IEventBRoot root = null;
		if (fSelection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) fSelection;
			if (ssel.size() == 1) {
				final Object element = ssel.getFirstElement();
				if (element instanceof IEventBRoot) {
					root = (IEventBRoot) element;
				} else if (element instanceof IFile) {
					IRodinFile rodinFile = RodinCore.valueOf((IFile) element);
					if (rodinFile != null) {
						root = (IEventBRoot) rodinFile.getRoot();
					}
				}
			}
		}
		return root;
	}

}
