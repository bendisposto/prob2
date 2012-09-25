package de.prob.ui.animationsview;


import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
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
		createColumns();
		viewer.setContentProvider(new AnimationsContentProvider());
		viewer.setLabelProvider(new AnimationViewLabelProvider(selector));
		viewer.setSorter(null);
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "de.prob.ui.viewer");
		hookDoubleClickAction();
		
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}
	
	private void createColumns() {
		TableViewerColumn column1 = new TableViewerColumn(viewer, SWT.NONE);
		column1.getColumn().setText("Model Name");
		column1.getColumn().setResizable(true);
		column1.getColumn().pack();

		TableViewerColumn column2 = new TableViewerColumn(viewer, SWT.NONE);
		column2.getColumn().setText("Last Executed Operation");
		column2.getColumn().setResizable(true);
		column2.getColumn().pack();
		
		TableViewerColumn column3 = new TableViewerColumn(viewer, SWT.NONE);
		column3.getColumn().setText("Number of Executed Operations");
		column3.getColumn().setResizable(true);
		column3.getColumn().pack();
	}

	private void packTableColumns() {
		for (TableColumn column : viewer.getTable().getColumns()) {
			column.pack();
		}
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
				packTableColumns();
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