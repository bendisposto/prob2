package de.prob.ui.animationsview;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.prob.model.eventb.Context;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.Machine;
import de.prob.statespace.History;
import de.prob.ui.Activator;

class AnimationViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	private final Image imgEnabled = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.IMG_SELECTED).createImage();
	private History currentHistory;

	@Override
	public String getColumnText(final Object obj, final int index) {
		if (index == 0) {
			if (obj instanceof History) {
				final History history = (History) obj;
				final AbstractElement component = history.getModel()
						.getMainComponent();

				if (component instanceof Machine) {
					final Machine m = (Machine) component;
					return m.getName();
				}
				if (component instanceof Context) {
					final Context c = (Context) component;
					return c.getName();
				}
			}
		}

		if (index == 1) {
			if (obj instanceof History) {
				final History history = (History) obj;
				if (!history.getCurrent().getSrc().getId().equals("root")) {
					return history.getCurrent().getOp().toString();
				}
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
		if (index == 0 && obj.equals(currentHistory)) {
			return imgEnabled;
		}
		return null;
	}

	@Override
	public Image getImage(final Object obj) {
		return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}

	public void setCurrentHistory(final History currentHistory) {
		this.currentHistory = currentHistory;
	}
}