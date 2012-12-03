package de.prob.ui.junitview;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.PageBook;

import de.prob.ui.stateview.StateContentProvider;

public class TestViewer {

	private final JUnitView fJUnitView;
	private PageBook fViewerbook;
	private TreeViewer fTreeViewer;

	public TestViewer(final Composite parent, final JUnitView junitview) {
		fJUnitView = junitview;

		createTestViewers(parent);
	}

	private void createTestViewers(final Composite parent) {
		fViewerbook = new PageBook(parent, SWT.NULL);
		fTreeViewer = new TreeViewer(fViewerbook, SWT.V_SCROLL | SWT.SINGLE);

		TestViewLabelProvider labelProvider = new TestViewLabelProvider();
		StateContentProvider contentProvider = new StateContentProvider();
		fTreeViewer.setContentProvider(contentProvider);
		fTreeViewer.setLabelProvider(labelProvider);

		fViewerbook.showPage(fTreeViewer.getTree());
	}

	public Control getTestViewerControl() {
		return fViewerbook;
	}

	class TestViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		@Override
		public String getColumnText(final Object obj, final int index) {
			return getText(obj);
		}

		@Override
		public Image getColumnImage(final Object obj, final int index) {
			return null;
		}

		@Override
		public Image getImage(final Object obj) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	class TestViewContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput,
				final Object newInput) {
		}

		@Override
		public Object[] getElements(final Object inputElement) {
			return new Object[] { inputElement };
		}

	}
}
