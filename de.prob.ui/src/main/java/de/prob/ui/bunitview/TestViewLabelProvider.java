package de.prob.ui.bunitview;

import java.util.Set;

import junit.framework.AssertionFailedError;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.spockframework.runtime.SpockComparisonFailure;

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
	private SetMultimap<String, Object> map;

	@Override
	public String getText(final Object element) {
		if (element instanceof String) {
			return (String) element;
		}
		if (element instanceof Description) {
			return ((Description) element).getMethodName();
		}
		if (element instanceof Failure) {
			return ((Failure) element).getDescription().getMethodName();
		}
		return element.toString();
	}

	@Override
	public String getColumnText(final Object obj, final int index) {
		return obj.getClass().getSimpleName();
	}

	@Override
	public Image getColumnImage(final Object obj, final int index) {
		if (obj instanceof Description) {
			return test_ok;
		} else if (obj instanceof Failure) {
			if (isFailure((Failure) obj)) {
				return test_fail;
			} else {
				return test_err;
			}
		}
		return suite_err;
	}

	@Override
	public Image getImage(final Object obj) {
		if (obj instanceof Description) {
			return test_ok;
		} else if (obj instanceof Failure) {
			if (isFailure((Failure) obj)) {
				return test_fail;
			} else {
				return test_err;
			}
		} else if (obj instanceof String) {
			return calculateClassCoverate(obj);
		}
		return suite_err;
	}

	private Image calculateClassCoverate(final Object obj) {
		Set<Object> set = map.get((String) obj);
		boolean isFailure = false;
		for (Object object : set) {
			if (object instanceof Failure) {
				if (!(isFailure((Failure) object))) {
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

	/**
	 * Checks to see if a Failure is because an assertion failed. Otherwise, an
	 * error occured. AssertionFailedErrors are thrown when a JUnit test fails
	 * and SpockComparisonFailures are thrown when Spock tests fail
	 * 
	 * @param f
	 * @return
	 */
	private boolean isFailure(final Failure f) {
		if (f.getException() instanceof AssertionFailedError
				|| f.getException() instanceof SpockComparisonFailure) {
			return true;
		}
		return false;
	}

	public void update(final SetMultimap<String, Object> map) {
		this.map = map;
	}
}
