package de.bmotionstudio.core;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.bmotionstudio.core.util.PerspectiveUtil;
import de.prob.ui.PerspectiveFactory;

public class ProBStartup implements IStartup {

	@Override
	public void earlyStartup() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {

				boolean deleted = false;

				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow activeWorkbenchWindow = workbench
						.getActiveWorkbenchWindow();
				IPerspectiveDescriptor currentPerspective = null;

				if (activeWorkbenchWindow != null) {
					IWorkbenchPage activePage = activeWorkbenchWindow
							.getActivePage();
					if (activePage != null)
						currentPerspective = activePage.getPerspective();
				}

				IPerspectiveRegistry perspectiveRegistry = workbench
						.getPerspectiveRegistry();
				IPerspectiveDescriptor[] perspectives = perspectiveRegistry
						.getPerspectives();
				for (IPerspectiveDescriptor p : perspectives) {
					if (p.getLabel().replaceAll("<", "").startsWith("ProB_")) {
						PerspectiveUtil.closePerspective(p);
						PerspectiveUtil.deletePerspective(p);
						deleted = true;
					}
				}
				if (deleted
						&& currentPerspective != null
						&& currentPerspective.getLabel().replaceAll("<", "")
								.startsWith("ProB_"))
					PerspectiveUtil
							.switchPerspective(PerspectiveFactory.PROB_PERSPECTIVE);

			}
		});
	}

}
