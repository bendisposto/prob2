package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.google.common.base.Joiner;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.OpInfo;
import de.prob.model.eventb.EBEvent;
import de.prob.model.representation.IEntity;
import de.prob.model.representation.Label;
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
			} else if (obj instanceof EBEvent) {
				final EBEvent event = (EBEvent) obj;
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
			} else if (obj instanceof EBEvent) {
				final EBEvent event = (EBEvent) obj;
				final Label parameters = event.parameters;
				final List<String> parameterNames = new ArrayList<String>();
				for (final IEntity child : parameters.getChildren()) {
					if (child instanceof EventB) {
						parameterNames.add(((EventB) child).getCode());
					}
				}
				return Joiner.on(",").join(parameterNames);
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
		if (obj instanceof EBEvent) {
			return imgDisabled;
		}
		return null;
	}
}