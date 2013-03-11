/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.core.model.attribute;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class AttributeSignalStatus extends AbstractAttribute {

	public static final int UNKNOWN = 0;
	public static final int STOP = 1;
	public static final int ATTENTION = 2;
	public static final int PROCEED = 3;
	
	public AttributeSignalStatus(Object value) {
		super(value);
	}

	@Override
	public Object unmarshal(final String s) {
		return Integer.valueOf(s);
	}

	@Override
	protected PropertyDescriptor preparePropertyDescriptor() {
		return new ComboBoxPropertyDescriptor(getID(), getName(), new String[] {
				"UNKNOWN", "STOP", "ATTENTION", "PROCEED" });
	}

	@Override
	public String getName() {
		return "Status";
	}

}

