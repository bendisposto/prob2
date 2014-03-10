package de.prob.ui.view;

import java.util.UUID;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public class ProB2ViewUtil {

	public static ProB2View createProB2ViewPart(final String relativeUrl,
			final String id) throws PartInitException {

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();

		ProB2View vizView = (ProB2View) activePage.showView(id, relativeUrl,
				IWorkbenchPage.VIEW_VISIBLE);

		if (vizView != null) {
			vizView.init(relativeUrl);
		}

		return vizView;
	}

	public static ProB2View createProB2ViewPart(final String relativeUrl)
			throws PartInitException {
		return createProB2ViewPart(relativeUrl, ProB2View.ID);
	}

	public static String createSessionId() {
		return UUID.randomUUID().toString();
	}

}
