package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.google.common.base.Joiner;

import de.prob.animator.domainobjects.OpInfo;
import de.prob.model.eventb.EBEvent;
import de.prob.ui.Activator;

class OperationViewLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	private final Image imgEnabled = Activator.getDefault()
			.getImageRegistry().getDescriptor(Activator.IMG_ENABLED)
			.createImage();
	private final Image imgDisabled = Activator.getDefault()
			.getImageRegistry().getDescriptor(Activator.IMG_DISABLED)
			.createImage();
	
	
	
	@SuppressWarnings("unchecked")
	public String getColumnText(Object obj, int index) {
		if(index == 0) {
			if(obj instanceof ArrayList<?>) {
				List<OpInfo> opList = (ArrayList<OpInfo>) obj;
				if(opList.size() > 1) {
					return opList.get(0).name + "(x"+ opList.size() + ")";
				} else {
					return opList.get(0).name;
				}
			} else if(obj instanceof OpInfo) {
				OpInfo op = (OpInfo) obj;
				return op.name;
			} else if(obj instanceof EBEvent) {
				EBEvent event = (EBEvent) obj;
				return event.getName();
			} else {
				return obj.getClass().toString();
			}
		}
		
		if(index == 1) {
			if(obj instanceof ArrayList<?>) {
				List<OpInfo> opList = (ArrayList<OpInfo>) obj;
				return Joiner.on(",").join(opList.get(0).params);
			} else if(obj instanceof OpInfo) {
				OpInfo op = (OpInfo) obj;
				return Joiner.on(",").join(op.params);
			} else if(obj instanceof EBEvent) {
				EBEvent event = (EBEvent) obj;
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
		if(obj instanceof ArrayList)
			return imgEnabled;
		if(obj instanceof EBEvent)
			return imgDisabled;
		return null;
	}
}