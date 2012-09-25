package de.prob.ui.animationsview;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.HistoryElement;

class AnimationViewLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	private AnimationSelector selector;
	public AnimationViewLabelProvider(AnimationSelector selector) {
		this.selector = selector;
	}
	
	public String getColumnText(Object obj, int index) {
		if(index == 0) {
			if(obj instanceof History) {
				AbstractModel model = selector.getModel((History) obj);
				if(model instanceof EventBModel) {
					EventBModel ebmodel = (EventBModel) model;
					return ebmodel.getMainComponentName();
				}
			}
		}
		
		if(index == 1) {
			if(obj instanceof History) {
				History history = (History) obj;
				if(!history.getCurrent().getSrc().getId().equals("root"))
					return history.getCurrent().getOp().toString();
			}
		}
		
		if(index == 2) {
			if(obj instanceof History) {
				History history = (History) obj;
				HistoryElement current = history.getCurrent();
				int count = 0;
				while(current.getOp() != null) {
					count++;
					current = current.getPrevious();
				}
				return count+"";
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