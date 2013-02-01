/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.edit;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import de.bmotionstudio.core.model.attribute.AbstractAttribute;
import de.bmotionstudio.core.model.control.BControl;

public class AttributeExpressionEdittingSupport extends EditingSupport {

	private BControl control;

	private String attribute;

	public AttributeExpressionEdittingSupport(ColumnViewer viewer,
			BControl control) {
		this(viewer, control, null);
	}

	public AttributeExpressionEdittingSupport(ColumnViewer viewer,
			BControl control, String attribute) {
		super(viewer);
		this.control = control;
		this.attribute = attribute;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		AbstractAttribute atr = ((AbstractAttribute) element);
		PropertyDescriptor desc = atr.getPropertyDescriptor();
		CellEditor propertyEditor = desc
				.createPropertyEditor((Composite) getViewer().getControl());
		return propertyEditor;
	}

	public void setControl(BControl control) {
		this.control = control;
	}

	public BControl getControl() {
		return control;
	}

	public void setAttribute(String stdAttribute) {
		this.attribute = stdAttribute;
	}

	public String getAttribute() {
		return attribute;
	}

}
