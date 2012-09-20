package de.prob.ui.animationsview;


import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.google.inject.Injector;

import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.IHistoryChangeListener;
import de.prob.webconsole.ServletContextListener;

public class AnimationsView extends ViewPart implements IHistoryChangeListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.prob.ui.operationview.OperationView";

	private TableViewer viewer;
	
	Injector injector = ServletContextListener.INJECTOR;
	AnimationSelector selector; 
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * The constructor.
	 */
	public AnimationsView() {
		selector = injector.getInstance(AnimationSelector.class);
		selector.registerHistoryChangeListener(this);
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new AnimationsContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(null);
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "de.prob.ui.viewer");
		hookDoubleClickAction();
		
		Table table = viewer.getTable();
		table.setLinesVisible(true);
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new AVDoubleClickListener());
	}
	
	@Override
	public void historyChange(final History history, AbstractModel model) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				viewer.setInput(selector);
			}
		});
	}
	
	public History getSelection() {
		if (viewer.getSelection() != null
				&& viewer.getSelection() instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) viewer
					.getSelection();
			if (ssel.getFirstElement() instanceof History)
				return (History) ssel.getFirstElement();
			else
				System.out.println("Selection is: "+ssel.getFirstElement().getClass());
		}
		return null;
	}
	
	private class AVDoubleClickListener implements IDoubleClickListener {

		public void doubleClick(final DoubleClickEvent event) {
			if (getSelection() != null) {
				selector.changeCurrentHistory(getSelection());
			} 
		}
	}
}