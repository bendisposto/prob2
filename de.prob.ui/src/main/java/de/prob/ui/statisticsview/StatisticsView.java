package de.prob.ui.statisticsview;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.google.inject.Injector;

import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.IHistoryChangeListener;
import de.prob.statespace.IStateSpaceChangeListener;
import de.prob.statespace.StateSpace;
import de.prob.webconsole.ServletContextListener;

public class StatisticsView extends ViewPart implements IHistoryChangeListener,
		IStateSpaceChangeListener {

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
		selector.registerHistoryChangeListener(this);

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
		column1.getColumn().pack();

		TableViewerColumn column2 = new TableViewerColumn(viewer, SWT.NONE);
		column2.getColumn().setResizable(true);
		column2.getColumn().pack();
	}

	/**
	 * Recalculate size of all columns
	 */
	private void packTableColumns() {
		for (final TableColumn column : viewer.getTable().getColumns()) {
			column.pack();
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void historyChange(final History history) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!viewer.getTable().isDisposed()) {
					if (history != null && notSameStateSpace(history)) {
						changeS(history);
					}
					packTableColumns();
					viewer.refresh();
				}
			}
		});
	}

	private void changeS(final History history) {
		if (currentStateSpace != null) {
			currentStateSpace.deregisterStateSpaceListener(this);
		}
		currentStateSpace = history.getS();
		currentStateSpace.registerStateSpaceListener(this);
		contentProvider.reset(currentStateSpace);
		labelProvider.setCurrentS(currentStateSpace);
	}

	private boolean notSameStateSpace(final History history) {
		if (history.getS() != currentStateSpace) {
			return true;
		}
		return false;
	}

	@Override
	public void newTransition(final String opName, final boolean isDestStateNew) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!viewer.getTable().isDisposed()) {
					contentProvider.addOp(opName);
					packTableColumns();
					viewer.refresh();
				}
			}
		});
	}
}