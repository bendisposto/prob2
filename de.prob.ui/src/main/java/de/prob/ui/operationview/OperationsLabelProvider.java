package de.prob.ui.operationview;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Returns the labels for the TableViewer. The first column gets the name of the
 * operation, the second column a preview of the first operation with a button,
 * opening the OperationSelectionDialog
 */
class OperationsLabelProvider extends LabelProvider implements
		ITableLabelProvider {

//	private final Image imgDisabled = ProbUiPlugin.getDefault()
//			.getImageRegistry().getDescriptor(ProbUiPlugin.IMG_DISABLED)
//			.createImage();
//	private final Image imgTimeout = ProbUiPlugin.getDefault()
//			.getImageRegistry().getDescriptor(ProbUiPlugin.IMG_TIMEOUT)
//			.createImage();
//	private final Image imgEnabled = ProbUiPlugin.getDefault()
//			.getImageRegistry().getDescriptor(ProbUiPlugin.IMG_ENABLED)
//			.createImage();

	/**
	 * @param operationTableViewer
	 */
	public OperationsLabelProvider() {
	}
	
	public Image getColumnImage(final Object element, final int columnIndex) {
//		if (columnIndex == 0) {
//			if (element instanceof String) {
//				boolean timeout = true;
//				if (timeout)
//					return imgTimeout;
//				else
//					return imgDisabled;
//			} else if (element instanceof ArrayList<?>)
//				return imgEnabled;
//			if( element instanceof OpInfo) {
//				return imgEnabled;
//			}
//		}
		return null;
	}

	public String getColumnText(final Object element, final int columnIndex) {
		return element.toString();
	}
}