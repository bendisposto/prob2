package de.prob.ui.stateview;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

class StateViewLabelProvider extends LabelProvider implements ITableLabelProvider {
	public String getColumnText(Object obj, int index) {
		if(index == 0) {
			if(obj instanceof Variable) {
				return ((Variable) obj).getName();
			}
		}
		
		if(index == 1) {
			if(obj instanceof Variable) {
				return ((Variable) obj).getValue();
			}
		}
		return "";
	}
	public Image getColumnImage(Object obj, int index) {
		return null;
	}
	public Image getImage(Object obj) {
		return PlatformUI.getWorkbench().
				getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}
}