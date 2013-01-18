/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.edit;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.util.BMotionWizardUtil;

public class AttributeExpressionEdittingSupport extends EditingSupport {

	private CellEditor cellEditor;

	private BControl control;

	private String stdAttribute;

	public AttributeExpressionEdittingSupport(ColumnViewer viewer,
			BControl control) {
		this(viewer, control, null);
	}

	public AttributeExpressionEdittingSupport(ColumnViewer viewer,
			BControl control, String stdAttribute) {
		super(viewer);
		this.control = control;
		this.stdAttribute = stdAttribute;
	}

	@Override
	protected boolean canEdit(Object element) {
		return BMotionWizardUtil.isEditElement(getViewer());
	}

	@Override
	protected Object getValue(Object element) {
		//TODO: Reimplement me!!!
//		ObserverEvalObject evalObject = (ObserverEvalObject) element;
//		return evalObject.getValue();
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		//TODO: Reimplement me!!!
//		if (value == null)
//			return;
//		((ObserverEvalObject) element).setValue(value);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {

		//TODO: Reimplement me!!!
//		ObserverEvalObject obj = ((ObserverEvalObject) element);
//
//		if (obj.isExpressionMode()) {
//			if (cellEditor == null) {
//				cellEditor = new TextCellEditor((Composite) getViewer()
//						.getControl());
//			}
//			return cellEditor;
//		} else {
//
//			String atrID = stdAttribute;
//
//			if (atrID == null)
//				atrID = obj.getAttribute();
//
//			if (atrID != null) {
//				if (atrID.length() > 0) {
//					AbstractAttribute atr = getControl().getAttributes().get(
//							atrID);
//					PropertyDescriptor desc = atr.getPropertyDescriptor();
//					return desc.createPropertyEditor((Composite) getViewer()
//							.getControl());
//				}
//			}
//
//		}

		return null;

	}

	public void setControl(BControl control) {
		this.control = control;
	}

	public BControl getControl() {
		return control;
	}

	public void setStdAttribute(String stdAttribute) {
		this.stdAttribute = stdAttribute;
	}

	public String getStdAttribute() {
		return stdAttribute;
	}

}
