package de.bmotionstudio.core.editor.view.outline;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import de.bmotionstudio.core.editor.VisualizationViewPart;


public class BMotionOutlineView extends ContentOutline {

	public static final String ID = "de.bmotionstudio.core.view.OutlineView";

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		IViewPart view = page.findView(VisualizationViewPart.ID);
		if (view != null)
			return view;
		return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		return part instanceof VisualizationViewPart;
	}

}
