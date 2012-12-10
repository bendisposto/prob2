package de.prob.ui.junitview;

import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestSuite;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.google.common.collect.SetMultimap;

import de.prob.ui.Activator;

public class TestViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	private final Image suite_err = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.JUNIT_TSUITE_ERROR).createImage();
	private final Image suite_fail = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.JUNIT_TSUITE_FAIL).createImage();
	private final Image suite_ok = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.JUNIT_TSUITE_OK).createImage();
	private final Image test_err = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.JUNIT_TEST_ERR).createImage();
	private final Image test_fail = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.JUNIT_TEST_FAIL).createImage();
	private final Image test_ok = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.JUNIT_TEST_OK).createImage();
	private SetMultimap<Class<TestSuite>, Object> map;

	@Override
	public String getText(final Object element) {
		if (element instanceof Class<?>) {
			return ((Class) element).getSimpleName();
		}
		if (element instanceof TestCase) {
			return ((TestCase) element).getName();
		}
		if (element instanceof TestFailure) {
			Test failedTest = ((TestFailure) element).failedTest();
			if (failedTest instanceof TestCase) {
				return ((TestCase) failedTest).getName();
			}
		}
		return element.toString();
	}

	@Override
	public String getColumnText(final Object obj, final int index) {
		return obj.getClass().getSimpleName();
	}

	@Override
	public Image getColumnImage(final Object obj, final int index) {
		if (obj instanceof Test) {
			return test_ok;
		} else if (obj instanceof TestFailure) {
			if (((TestFailure) obj).isFailure()) {
				return test_fail;
			} else {
				return test_err;
			}
		}
		return suite_err;
	}

	@Override
	public Image getImage(final Object obj) {
		if (obj instanceof Test) {
			return test_ok;
		} else if (obj instanceof TestFailure) {
			if (((TestFailure) obj).isFailure()) {
				return test_fail;
			} else {
				return test_err;
			}
		} else if (obj instanceof Class<?>) {
			return calculateClassCoverate(obj);
		}
		return suite_err;
	}

	@SuppressWarnings("unchecked")
	private Image calculateClassCoverate(final Object obj) {
		Set<Object> set = map.get((Class<TestSuite>) obj);
		boolean isFailure = false;
		for (Object object : set) {
			if (object instanceof TestFailure) {
				if (!((TestFailure) object).isFailure()) {
					return suite_err;
				} else {
					isFailure = true;
				}
			}
		}
		if (isFailure) {
			return suite_fail;
		}
		return suite_ok;
	}

	public void update(final SetMultimap<Class<TestSuite>, Object> map) {
		this.map = map;
	}
}
