package de.prob.ui.statisticsview;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.google.inject.Injector;

import de.prob.animator.domainobjects.OpInfo;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.IStatesCalculatedListener;
import de.prob.statespace.StateSpace;
import de.prob.statespace.StateSpaceGraph;
import de.prob.webconsole.ServletContextListener;

public class StatisticsView extends ViewPart implements IModelChangedListener,
		IStatesCalculatedListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.prob.ui.operationview.OperationView";

	private TableViewer viewer;

	Injector injector = ServletContextListener.INJECTOR;
	AnimationSelector selector;

	private StatisticsViewLabelProvider labelProvider;
	private StateSpace currentStateSpace = null;

	private StatisticsContentProvider contentProvider;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		selector = injector.getInstance(AnimationSelector.class);
		selector.registerModelChangedListener(this);

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		createColumns();
		contentProvider = new StatisticsContentProvider();
		viewer.setContentProvider(contentProvider);
		labelProvider = new StatisticsViewLabelProvider();
		viewer.setLabelProvider(labelProvider);
		viewer.setSorter(null);
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(viewer.getControl(), "de.prob.ui.viewer");

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private void createColumns() {
		TableViewerColumn column1 = new TableViewerColumn(viewer, SWT.NONE);
		column1.getColumn().setResizable(true);
		column1.getColumn().setWidth(170);

		TableViewerColumn column2 = new TableViewerColumn(viewer, SWT.NONE);
		column2.getColumn().setResizable(true);
		column2.getColumn().pack();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void modelChanged(final StateSpace s) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!viewer.getTable().isDisposed()) {
					changeS(s);
					viewer.refresh();
				}
			}
		});
	}

	private void changeS(final StateSpace s) {
		if (currentStateSpace != null) {
			currentStateSpace.deregisterStateSpaceListener(this);
		}
		currentStateSpace = s;
		currentStateSpace.registerStateSpaceListener(this);
		contentProvider.reset(currentStateSpace);
		labelProvider.setCurrentS(currentStateSpace);
	}

	@Override
	public void newTransitions(final StateSpaceGraph s, final List<OpInfo> ops) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!viewer.getTable().isDisposed()) {
					viewer.refresh();
				}
			}
		});
	}

}