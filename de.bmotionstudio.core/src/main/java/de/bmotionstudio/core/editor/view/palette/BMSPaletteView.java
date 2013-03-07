package de.bmotionstudio.core.editor.view.palette;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.views.palette.PaletteView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;

import de.bmotionstudio.core.editor.VisualizationViewPart;

public class BMSPaletteView extends PaletteView {

	private PaletteViewer paletteViewer;

	private PaletteRoot paletteRoot;
	
	public static String ID = "de.bmotionstudio.core.view.PaletteView";

	private PageRec visPageRec;
	
	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		IViewPart view = page.findView(VisualizationViewPart.ID);
		if (view != null)
			return view;
		return null;
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		partActivated(part);
		if (part instanceof VisualizationViewPart)
			hookVisualizationViewPart((VisualizationViewPart) part);
		super.partOpened(part);
	}
	
	private void hookVisualizationViewPart(VisualizationViewPart part) {
		EditDomain domain = part.getEditDomain();
		if (domain != null) {
			domain.setPaletteViewer(paletteViewer);
			domain.setPaletteRoot(paletteRoot);
		}
	}
	
	private void unhookVisualizationViewPart(VisualizationViewPart part) {
		EditDomain domain = part.getEditDomain();
		if (domain != null) {
			domain.setPaletteViewer(null);
			domain.setPaletteRoot(null);
		}
	}
	
	@Override
	public void partActivated(IWorkbenchPart part) {
		super.partActivated(part);
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		if (part instanceof VisualizationViewPart) {
			if (visPageRec == null) {
				BMSPaletteViewPage page = new BMSPaletteViewPage();
				initPage(page);
				page.createControl(getPageBook());
				visPageRec = new PageRec(part, page);
			}	
			return visPageRec;
		}
		return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		return part instanceof VisualizationViewPart;
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part instanceof VisualizationViewPart)
			unhookVisualizationViewPart((VisualizationViewPart)part);
	}
	
	private class BMSPaletteViewPage extends Page {

		private Composite container;

		protected void createPaletteViewer(Composite parent) {
			paletteViewer = new PaletteViewer();
			paletteViewer.createControl(parent);
			paletteViewer.getControl().setBackground(ColorConstants.green);
			paletteRoot = new EditorPaletteFactory().createPalette();
			paletteViewer.setPaletteRoot(paletteRoot);
			paletteViewer
					.addDragSourceListener(new TemplateTransferDragSourceListener(
							paletteViewer));
		}

		@Override
		public void createControl(Composite parent) {
			container = new Composite(parent, SWT.NONE);
			container.setLayout(new FillLayout());
			createPaletteViewer(container);
		}

		@Override
		public Control getControl() {
			return container;
		}

		@Override
		public void setFocus() {
		}

	}

}
