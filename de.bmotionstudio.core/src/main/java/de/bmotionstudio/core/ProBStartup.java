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
				IPerspectiveRegistry perspectiveRegistry = workbench
						.getPerspectiveRegistry();
				IPerspectiveDescriptor[] perspectives = perspectiveRegistry
						.getPerspectives();
				for (IPerspectiveDescriptor p : perspectives) {
					if (p.getId().replaceAll("<", "").startsWith("ProB_")) {
						PerspectiveUtil.closePerspective(p);
						PerspectiveUtil.deletePerspective(p);
						deleted = true;
					}
				}
				IWorkbenchWindow activeWorkbenchWindow = workbench
						.getActiveWorkbenchWindow();
				if (activeWorkbenchWindow != null) {
					IWorkbenchPage activePage = activeWorkbenchWindow
							.getActivePage();
					if (activePage != null) {
						IPerspectiveDescriptor currentPerspective = activePage
								.getPerspective();
						if (deleted
								&& currentPerspective.getId()
										.replaceAll("<", "")
										.startsWith("ProB_"))
							PerspectiveUtil
									.switchPerspective(PerspectiveFactory.PROB_PERSPECTIVE);
					}
				}
			}
		});
	}

}
