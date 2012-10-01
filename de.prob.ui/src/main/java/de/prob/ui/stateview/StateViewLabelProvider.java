package de.prob.ui.stateview;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.prob.model.representation.AbstractElement;

class StateViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	public String getColumnText(Object obj, int index) {
		if(index == 0) {
			if(obj instanceof AbstractElement) {
				return ((AbstractElement) obj).getName();
			}
			if(obj instanceof Variable) {
				return ((Variable) obj).getName();
			}
			if(obj instanceof String) {
				return (String) obj;
			} else {
				return obj.getClass().toString();
			}
		}
		
		if(index == 1) {
			if(obj instanceof Variable) {
				return ((Variable) obj).getCurrentValue();
			}
		}
		
		if(index == 2) {
			if(obj instanceof Variable) {
				return ((Variable) obj).getPreviousValue();
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