package de.prob.ui.visualization;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class VisualizationUtil {
	
	public static int counter = 0;

	public static VizView createVisualizationViewPart(
			String htmlFile) throws PartInitException {

		String secId = "de.prob.ui.viz.VizView.Nr"+counter;
		counter++;

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();

		VizView vizView = (VizView) activePage
				.showView(VizView.ID, secId,
						IWorkbenchPage.VIEW_VISIBLE);

		if (vizView != null)
			vizView.init(htmlFile);

		return vizView;

	}
}
