package de.prob.ui.stateview;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.prob.model.representation.AbstractElement;

class StateViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	@Override
	public String getColumnText(final Object obj, final int index) {
		if (index == 0) {
			if (obj instanceof AbstractElement) {
				return ((AbstractElement) obj).toString();
			}
			if (obj instanceof Variable) {
				return ((Variable) obj).getName();
			}
			if (obj instanceof String) {
				return (String) obj;
			} else {
				return obj.getClass().toString();
			}
		}

		if (index == 1) {
			if (obj instanceof Variable) {
				return ((Variable) obj).getCurrentValue();
			}
		}

		if (index == 2) {
			if (obj instanceof Variable) {
				return ((Variable) obj).getPreviousValue();
			}
		}
		return "";
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