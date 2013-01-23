package de.prob.ui.stateview;

import java.util.Set;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.google.inject.Injector;

import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Invariant;
import de.prob.model.representation.Machine;
import de.prob.model.representation.Variable;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.IHistoryChangeListener;
import de.prob.statespace.StateSpace;
import de.prob.webconsole.ServletContextListener;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class StateView extends ViewPart implements IHistoryChangeListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.prob.ui.operationview.OperationView";

	private History currentHistory;
	private AbstractElement currentModel;

	Injector injector = ServletContextListener.INJECTOR;

	private Composite pageComposite;

	private TreeViewer viewer;
	private StateContentProvider contentProvider;
	private StateViewLabelProvider labelProvider;

	/**
	 * The constructor.
	 */
	public StateView() {
		final AnimationSelector selector = injector
				.getInstance(AnimationSelector.class);
		selector.registerHistoryChangeListener(this);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		pageComposite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(2, true);
		pageComposite.setLayout(layout);
		contentProvider = new StateContentProvider();
		labelProvider = new StateViewLabelProvider();
		createVariableTree();

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(viewer.getControl(), "de.prob.ui.viewer");

		getSite().setSelectionProvider(viewer);
	}

	private void createVariableTree() {
		final GridData treeViewerLayout = new GridData();
		treeViewerLayout.grabExcessHorizontalSpace = true;
		treeViewerLayout.grabExcessVerticalSpace = true;
		treeViewerLayout.horizontalAlignment = SWT.FILL;
		treeViewerLayout.verticalAlignment = SWT.FILL;
		treeViewerLayout.horizontalSpan = 2;

		viewer = new TreeViewer(pageComposite);
		viewer.getTree().setLayoutData(treeViewerLayout);
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().setLinesVisible(true);
		viewer.setAutoExpandLevel(2);

		final TreeViewerColumn col1 = new TreeViewerColumn(viewer, SWT.LEFT);
		col1.getColumn().setText("Name");
		col1.getColumn().setResizable(true);
		col1.getColumn().setWidth(200);

		final TreeViewerColumn col2 = new TreeViewerColumn(viewer, SWT.RIGHT);
		col2.getColumn().setText("Current Value");
		col2.getColumn().setResizable(true);
		col2.getColumn().setWidth(150);

		final TreeViewerColumn col3 = new TreeViewerColumn(viewer, SWT.RIGHT);
		col3.getColumn().setText("Previous Value");
		col3.getColumn().setResizable(true);
		col3.getColumn().setWidth(150);

		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(labelProvider);
		viewer.setInput(getViewSite());
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
				if (history == null) {
					updateModelInfo(null);
				} else {
					currentHistory = history;
					contentProvider.setCurrentHistory(currentHistory);
					labelProvider.setInput(currentHistory);

					final AbstractElement model = history.getModel();
					if (model != currentModel) {
						updateModelInfo(model);
					}
				}

				viewer.refresh();
			}
		});
	}

	private void updateModelInfo(final AbstractElement model) {
		currentModel = model;
		StateSpace s = currentHistory.getStatespace();

		Set<Machine> machines = model.getChildrenOfType(Machine.class);
		for (Machine machine : machines) {
			for (Variable variable : machine.getChildrenOfType(Variable.class)) {
				s.subscribe(this, variable.getExpression());
			}
			for (Invariant invariant : machine
					.getChildrenOfType(Invariant.class)) {
				s.subscribe(this, invariant.getPredicate());
			}
		}

		if (!viewer.getTree().isDisposed()) {
			viewer.setInput(model);
		}
	}
}