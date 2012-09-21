package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eventb.emf.core.machine.Parameter;
import org.eventb.emf.core.machine.impl.EventImpl;

import com.google.common.base.Joiner;

import de.prob.animator.domainobjects.OpInfo;
import de.prob.model.eventb.Event;
import de.prob.ui.Activator;

class OperationViewLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	private final Image imgEnabled = Activator.getDefault()
			.getImageRegistry().getDescriptor(Activator.IMG_ENABLED)
			.createImage();
	private final Image imgDisabled = Activator.getDefault()
			.getImageRegistry().getDescriptor(Activator.IMG_DISABLED)
			.createImage();
	
	
	
	public String getColumnText(Object obj, int index) {
		if(index == 0) {
			if(obj instanceof OpInfo) {
				OpInfo op = (OpInfo) obj;
				return op.name;
			} else if(obj instanceof Event) {
				Event event = (Event) obj;
				return event.getName();
			} else {
				return obj.getClass().toString();
			}
		}
		
		if(index == 1) {
			if(obj instanceof OpInfo) {
				OpInfo op = (OpInfo) obj;
				return Joiner.on(",").join(op.params);
			} else if(obj instanceof Event) {
				Event event = (Event) obj;
				return Joiner.on(",").join(event.getParams());
			} else {
				return obj.getClass().toString();
			}
		}
		return "";
	}
	public Image getColumnImage(Object obj, int index) {
		if( index == 1 )
			return null;
		return getImage(obj);
	}
	
	public Image getImage(Object obj) {
		if(obj instanceof OpInfo)
			return imgEnabled;
		if(obj instanceof Event)
			return imgDisabled;
		return null;
	}
}