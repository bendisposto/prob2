package de.prob.ui.animationsview;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;

class AnimationViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	private final AnimationSelector selector;

	public AnimationViewLabelProvider(final AnimationSelector selector) {
		this.selector = selector;
	}

	@Override
	public String getColumnText(final Object obj, final int index) {
		if (index == 0) {
			if (obj instanceof History) {
				final AbstractModel model = selector.getModel((History) obj);
				if (model instanceof EventBModel) {
					final EventBModel ebmodel = (EventBModel) model;
					return ebmodel.getMainComponentName();
				}
				if (model instanceof ClassicalBModel) {
					final ClassicalBModel cbmodel = (ClassicalBModel) model;
					return cbmodel.getMainMachine().getName();
				}
			}
		}

		if (index == 1) {
			if (obj instanceof History) {
				final History history = (History) obj;
				if (!history.getCurrent().getSrc().getId().equals("root"))
					return history.getCurrent().getOp().toString();
			}
		}

		if (index == 2) {
			if (obj instanceof History) {
				final History history = (History) obj;
				return history.getCurrent().getOpList().size() + "";
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