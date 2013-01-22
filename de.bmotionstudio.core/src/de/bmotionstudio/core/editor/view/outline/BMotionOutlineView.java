package de.bmotionstudio.core.editor.view.outline;

import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.properties.IPropertySheetEntry;

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
	
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {
		
		if (type == IPropertySheetEntry.class || type == ActionRegistry.class) {
			BMotionOutlinePage outlinePage = getBMotionOutlinePage();
			if (outlinePage != null) {
				return outlinePage.getViewPart().getAdapter(type);
			}
		}
		
		return super.getAdapter(type);
	}
	
	private BMotionOutlinePage getBMotionOutlinePage() {
		IPage currentPage = getCurrentPage();
		if (currentPage != null && currentPage instanceof BMotionOutlinePage) {
			return (BMotionOutlinePage) currentPage;
		}
		return null;
	}
	
}
