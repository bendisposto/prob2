package de.prob.ui.junitview;

import junit.framework.Test;
import junit.framework.TestFailure;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.prob.ui.Activator;

public class TestViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	private final Image suite_err = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.JUNIT_TSUITE_OK).createImage();
	private final Image suite_fail = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.JUNIT_TSUITE_FAIL).createImage();
	private final Image suite_ok = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.JUNIT_TSUITE_ERROR).createImage();
	private final Image test_err = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.JUNIT_TEST_ERR).createImage();
	private final Image test_fail = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.JUNIT_TEST_FAIL).createImage();
	private final Image test_ok = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.JUNIT_TEST_OK).createImage();

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
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}
}
