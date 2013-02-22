package de.prob.ui.bunitview;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.inject.Injector;

import de.prob.testing.ITestsAddedListener;
import de.prob.testing.ProBTestListener;
import de.prob.testing.ProBTestRunner;
import de.prob.testing.TestRegistry;
import de.prob.ui.Activator;
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

public class BUnitView extends ViewPart implements ITestsAddedListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.prob.ui.junitview.JUnitView";

	Injector injector = ServletContextListener.INJECTOR;

	private CounterPanel fCounterPanel;
	private BUnitProgressBar fProgressBar;
	private Composite fCounterComposite;
	private SashForm fSashForm;
	private TestViewer fTestViewer;
	private ProBTestRunner runner;
	private JUnitTestListener listener;
	private FailureTrace fFailureTrace;

	private int testCount = 0;
	private int failureCount = 0;
	private int runCount = 0;
	private int ignoredCount = 0;

	private final SetMultimap<String, Object> tests = LinkedHashMultimap
			.create();

	class ViewLabelProvider extends LabelProvider implements
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

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent) {

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		parent.setLayout(gridLayout);

		fCounterComposite = createProgressCountPanel(parent);
		fCounterComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		SashForm sashForm = createSashForm(parent);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		runner = ServletContextListener.INJECTOR
				.getInstance(ProBTestRunner.class);
		listener = new JUnitTestListener();
		runner.addTestListener(listener);

		TestRegistry instance = ServletContextListener.INJECTOR
				.getInstance(TestRegistry.class);
		instance.registerListener(this);

	}

	protected Composite createProgressCountPanel(final Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 1;

		fCounterPanel = new CounterPanel(composite);
		fCounterPanel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		fProgressBar = new BUnitProgressBar(composite);
		fProgressBar.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		return composite;
	}

	private SashForm createSashForm(final Composite parent) {
		fSashForm = new SashForm(parent, SWT.VERTICAL);

		ViewForm top = new ViewForm(fSashForm, SWT.NONE);

		Composite empty = new Composite(top, SWT.NONE);
		empty.setLayout(new Layout() {
			@Override
			protected Point computeSize(final Composite composite,
					final int wHint, final int hHint, final boolean flushCache) {
				return new Point(1, 1); // (0, 0) does not work with
										// super-intelligent ViewForm
			}

			@Override
			protected void layout(final Composite composite,
					final boolean flushCache) {
			}
		});
		top.setTopLeft(empty); // makes ViewForm draw the horizontal separator
								// line ...
		fTestViewer = new TestViewer(top, this);
		top.setContent(fTestViewer.getTestViewerControl());

		ViewForm bottom = new ViewForm(fSashForm, SWT.NONE);

		CLabel label = new CLabel(bottom, SWT.NONE);
		label.setText("Failure Trace");
		label.setImage(Activator.getDefault().getImageRegistry()
				.get(Activator.JUNIT_STACK));
		bottom.setTopLeft(label);
		fFailureTrace = new FailureTrace(bottom, this);
		bottom.setContent(fFailureTrace.getComposite());

		fTestViewer.getViewer().addSelectionChangedListener(
				new TestSelectionListener());

		fSashForm.setWeights(new int[] { 50, 50 });
		return fSashForm;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void newTests(final List<String> tests) {
		resetAll();
		runner.runTests(tests);
	}

	private void resetAll() {
		tests.clear();
		testCount = 0;
		failureCount = 0;
		runCount = 0;
		ignoredCount = 0;
	}

	private class JUnitTestListener extends ProBTestListener {

		@Override
		public void testFailure(final Failure failure) throws Exception {
			tests.remove(failure.getDescription().getClassName(),
					failure.getDescription());
			tests.put(failure.getDescription().getClassName(), failure);
			failureCount++;

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					fCounterPanel.setFailureValue(failureCount);
				}
			});
		}

		@Override
		public void testAssumptionFailure(final Failure failure) {
			tests.remove(failure.getDescription().getClassName(),
					failure.getDescription());
			tests.put(failure.getDescription().getClassName(), failure);
			failureCount++;

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					fCounterPanel.setFailureValue(failureCount);
				}
			});
		}

		@Override
		public void testIgnored(final Description description) throws Exception {
			ignoredCount++;
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					fProgressBar.step(failureCount);
				}
			});
		}

		@Override
		public void testFinished(final Description description)
				throws Exception {
			runCount++;

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					fTestViewer.update(Multimaps.synchronizedSetMultimap(tests));
				}
			});
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					fCounterPanel.setRunValue(runCount, ignoredCount);
				}
			});
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					fProgressBar.step(failureCount);
				}
			});
		}

		@Override
		public void testStarted(final Description description) throws Exception {
			String className = description.getClassName();
			tests.put(className, description);
		}

		@Override
		public void totalNumberOfTests(final int number) {
			testCount = number;

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					fCounterPanel.reset();
					fCounterPanel.setTotal(testCount);
				}
			});

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					fProgressBar.reset();
					fProgressBar.setMaximum(testCount);
				}
			});
		}
	}

	class TestSelectionListener implements ISelectionChangedListener {

		@Override
		public void selectionChanged(final SelectionChangedEvent event) {
			Failure selectedTest = getSelectedTest();
			fFailureTrace.showFailure(selectedTest);
		}
	}

	public Failure getSelectedTest() {
		TreeViewer viewer = fTestViewer.getViewer();
		if (viewer.getSelection() != null
				&& viewer.getSelection() instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) viewer
					.getSelection();
			if (ssel.getFirstElement() instanceof Failure) {
				return (Failure) ssel.getFirstElement();
			}
		}
		return null;
	}

}