package de.prob.ui.junitview;

import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestListener;
import junit.framework.TestSuite;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.PageBook;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import de.prob.testing.ITestsAddedListener;
import de.prob.testing.ProBTestRunner;
import de.prob.testing.TestRegistry;
import de.prob.webconsole.ServletContextListener;

public class TestViewer implements TestListener, ITestsAddedListener {

	private final JUnitView fJUnitView;
	private PageBook fViewerbook;
	private TreeViewer viewer;
	private TestViewLabelProvider labelProvider;
	private final SetMultimap<Class<TestSuite>, Object> tests = LinkedHashMultimap
			.create();
	private TestViewContentProvider contentProvider;
	private final ProBTestRunner runner;

	public TestViewer(final Composite parent, final JUnitView junitview) {
		fJUnitView = junitview;
		createTestViewers(parent);

		runner = ServletContextListener.INJECTOR
				.getInstance(ProBTestRunner.class);
		runner.addTestListener(this);

		TestRegistry instance = ServletContextListener.INJECTOR
				.getInstance(TestRegistry.class);
		instance.registerListener(this);
	}

	private void createTestViewers(final Composite parent) {
		fViewerbook = new PageBook(parent, SWT.NULL);
		viewer = new TreeViewer(fViewerbook, SWT.V_SCROLL | SWT.SINGLE);

		labelProvider = new TestViewLabelProvider();
		contentProvider = new TestViewContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(labelProvider);

		fViewerbook.showPage(viewer.getTree());
	}

	public Control getTestViewerControl() {
		return fViewerbook;
	}

	@Override
	public void newTests(final List<String> tests) {
		this.tests.clear();
		runner.runTests(tests);
	}

	@Override
	public void addError(final Test test, final Throwable t) {
		tests.remove(test.getClass(), test);
		tests.put((Class<TestSuite>) test.getClass(), new TestFailure(test, t));
	}

	@Override
	public void addFailure(final Test test, final AssertionFailedError t) {
		tests.remove(test.getClass(), test);
		tests.put((Class<TestSuite>) test.getClass(), new TestFailure(test, t));
	}

	@Override
	public void endTest(final Test test) {
		update();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void startTest(final Test test) {
		Class<? extends Test> class1 = test.getClass();
		tests.put((Class<TestSuite>) class1, test);
	}

	public void update() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				contentProvider.update(Multimaps.synchronizedSetMultimap(tests));
				viewer.setInput(tests.asMap().keySet());
				viewer.refresh();
			}
		});
	}
}
