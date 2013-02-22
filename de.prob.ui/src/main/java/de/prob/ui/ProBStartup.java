package de.prob.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

import de.prob.common.util.PerspectiveUtil;

public class ProBStartup implements IStartup {

	@Override
	public void earlyStartup() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				IPerspectiveRegistry perspectiveRegistry = PlatformUI
						.getWorkbench().getPerspectiveRegistry();
				IPerspectiveDescriptor[] perspectives = perspectiveRegistry
						.getPerspectives();
				for (IPerspectiveDescriptor p : perspectives) {
					if (p.getId().replace("<", "").startsWith("ProB_")) {
						PerspectiveUtil.closePerspective(p);
						PerspectiveUtil.deletePerspective(p);
					}
				}
			}
		});
	}

}
