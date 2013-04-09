/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.attribute;

import org.eclipse.ui.views.properties.PropertyDescriptor;

public class AttributeSourceConnections extends AbstractAttribute {

	public AttributeSourceConnections(Object value) {
		super(value);
	}

	@Override
	public PropertyDescriptor preparePropertyDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return "Source Connections";
	}

}
