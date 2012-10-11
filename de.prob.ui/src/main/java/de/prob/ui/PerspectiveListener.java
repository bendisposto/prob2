package de.prob.ui;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener4;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;

public class PerspectiveListener implements IPerspectiveListener4 {

	@Override
	public void perspectiveActivated(final IWorkbenchPage page,
			final IPerspectiveDescriptor perspective) {

	}

	@Override
	public void perspectiveChanged(final IWorkbenchPage page,
			final IPerspectiveDescriptor perspective, final String changeId) {

	}

	@Override
	public void perspectiveOpened(final IWorkbenchPage page,
			final IPerspectiveDescriptor perspective) {
	}

	@Override
	public void perspectiveClosed(final IWorkbenchPage page,
			final IPerspectiveDescriptor perspective) {
		// OperationTableViewer.destroy();
	}

	@Override
	public void perspectiveDeactivated(final IWorkbenchPage page,
			final IPerspectiveDescriptor perspective) {
	}

	@Override
	public void perspectiveSavedAs(final IWorkbenchPage page,
			final IPerspectiveDescriptor oldPerspective,
			final IPerspectiveDescriptor newPerspective) {

	}

	@Override
	public void perspectiveChanged(final IWorkbenchPage page,
			final IPerspectiveDescriptor perspective,
			final IWorkbenchPartReference partRef, final String changeId) {

	}

	@Override
	public void perspectivePreDeactivate(final IWorkbenchPage page,
			final IPerspectiveDescriptor perspective) {

	}

}
