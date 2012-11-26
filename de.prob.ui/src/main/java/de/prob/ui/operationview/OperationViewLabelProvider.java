package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.google.common.base.Joiner;

import de.prob.animator.domainobjects.OpInfo;
import de.prob.model.eventb.Event;
import de.prob.model.eventb.EventParameter;
import de.prob.model.representation.BEvent;
import de.prob.ui.Activator;

class OperationViewLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	private final Image imgEnabled = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.IMG_ENABLED).createImage();
	private final Image imgDisabled = Activator.getDefault().getImageRegistry()
			.getDescriptor(Activator.IMG_DISABLED).createImage();

	@Override
	@SuppressWarnings("unchecked")
	public String getColumnText(final Object obj, final int index) {
		if (index == 0) {
			if (obj instanceof ArrayList<?>) {
				final List<OpInfo> opList = (ArrayList<OpInfo>) obj;
				if (opList.size() > 1) {
					return opList.get(0).name + "(x" + opList.size() + ")";
				} else {
					return opList.get(0).name;
				}
			} else if (obj instanceof OpInfo) {
				final OpInfo op = (OpInfo) obj;
				return op.name;
			} else if (obj instanceof Event) {
				final Event event = (Event) obj;
				return event.getName();
			} else {
				return obj.getClass().toString();
			}
		}

		if (index == 1) {
			if (obj instanceof ArrayList<?>) {
				final List<OpInfo> opList = (ArrayList<OpInfo>) obj;
				return Joiner.on(",").join(opList.get(0).params);
			} else if (obj instanceof OpInfo) {
				final OpInfo op = (OpInfo) obj;
				return Joiner.on(",").join(op.params);
			} else if (obj instanceof Event) {
				final Event event = (Event) obj;
				Set<EventParameter> params = event
						.getChildrenOfType(EventParameter.class);
				List<String> paramNames = new ArrayList<String>();
				for (EventParameter param : params) {
					paramNames.add(param.getName());
				}
				return Joiner.on(",").join(paramNames);
			} else {
				return obj.getClass().toString();
			}
		}
		return "";
	}

	@Override
	public Image getColumnImage(final Object obj, final int index) {
		if (index == 1) {
			return null;
		}
		return getImage(obj);
	}

	@Override
	public Image getImage(final Object obj) {
		if (obj instanceof ArrayList) {
			return imgEnabled;
		}
		if (obj instanceof BEvent) {
			return imgDisabled;
		}
		return null;
	}
}