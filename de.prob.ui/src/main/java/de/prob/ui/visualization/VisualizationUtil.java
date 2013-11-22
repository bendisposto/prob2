package de.prob.ui.visualization;

import java.util.UUID;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class VisualizationUtil {

	public static int counter = 0;

	public static VizView createVisualizationViewPart(final String relativeUrl,
			final String id) throws PartInitException {

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();

		VizView vizView = (VizView) activePage.showView(id, relativeUrl,
				IWorkbenchPage.VIEW_VISIBLE);

		if (vizView != null) {
			vizView.init(relativeUrl);
		}

		return vizView;
	}
	
	public static VizView createVisualizationViewPart(final String relativeUrl)
			throws PartInitException {
		return createVisualizationViewPart(relativeUrl, VizView.ID);
	}

	public static String createSessionId() {
		return UUID.randomUUID().toString();
	}
}
