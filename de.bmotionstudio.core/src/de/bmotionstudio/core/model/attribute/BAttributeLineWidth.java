package de.bmotionstudio.core.model.attribute;

import org.eclipse.ui.views.properties.PropertyDescriptor;

import de.bmotionstudio.core.editor.property.IntegerPropertyDescriptor;
import de.bmotionstudio.core.model.control.BControl;

public class BAttributeLineWidth extends AbstractAttribute {

	public PropertyDescriptor preparePropertyDescriptor() {
		IntegerPropertyDescriptor descriptor = new IntegerPropertyDescriptor(
				getID(), getName());
		return descriptor;
	}

	public BAttributeLineWidth(Object value) {
		super(value);
	}

	@Override
	public String validateValue(Object value, BControl control) {
		if (!(String.valueOf(value)).trim().matches("\\d*")) {
			return "Value must be a number";
		}
		if ((String.valueOf(value)).trim().length() == 0) {
			return "Value must not be empty string";
		}
		return null;
	}

	@Override
	public String getName() {
		return "Line-Width";
	}

}
