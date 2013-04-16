package de.prob.ui.visualization;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class VisualizationUtil {

	public static int counter = 0;

	public static VizView createVisualizationViewPart(final String sessionId,
			final String relativeUrl) throws PartInitException {

		String secId = "de.prob.ui.viz.VizView.nr" + counter++;

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();

		VizView vizView = (VizView) activePage.showView(VizView.ID, secId,
				IWorkbenchPage.VIEW_VISIBLE);

		if (vizView != null) {
			vizView.init(relativeUrl);
		}

		return vizView;

	}
}
