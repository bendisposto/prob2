package de.prob.ui.stateview;


import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.google.inject.Injector;

import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.IHistoryChangeListener;
import de.prob.webconsole.ServletContextListener;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class StateView extends ViewPart implements IHistoryChangeListener{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.prob.ui.operationview.OperationView";

	private TableViewer viewer;
	private History currentHistory;
	
	Injector injector = ServletContextListener.INJECTOR;
	 
	/**
	 * The constructor.
	 */
	public StateView() {
		AnimationSelector selector = injector.getInstance(AnimationSelector.class);
		selector.registerHistoryChangeListener(this);
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		createColumns();
		viewer.setContentProvider(new StateContentProvider());
		viewer.setLabelProvider(new StateViewLabelProvider());
		viewer.setSorter(null);
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "de.prob.ui.viewer");
		
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}
	
	private void createColumns() {
		TableViewerColumn column1 = new TableViewerColumn(viewer, SWT.NONE);
		column1.getColumn().setText("Name");
		column1.getColumn().setResizable(true);
		column1.getColumn().pack();

		TableViewerColumn column2 = new TableViewerColumn(viewer, SWT.NONE);
		column2.getColumn().setText("Value");
		column2.getColumn().setResizable(true);
		column2.getColumn().pack();
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
	
	@Override
	public void historyChange(final History history, AbstractModel model) {
		currentHistory = history;
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				viewer.setInput(history);
				packTableColumns();
			}
		});
	}
}