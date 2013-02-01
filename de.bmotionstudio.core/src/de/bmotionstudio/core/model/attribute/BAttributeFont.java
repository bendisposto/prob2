/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.model.attribute;

import org.eclipse.ui.views.properties.PropertyDescriptor;

import de.bmotionstudio.core.editor.property.FontPropertyDescriptor;

public class BAttributeFont extends AbstractAttribute {

	public BAttributeFont(Object value) {
		super(value);
	}

	public PropertyDescriptor preparePropertyDescriptor() {
		return new FontPropertyDescriptor(getID(), getName());
	}

	@Override
	public String getName() {
		return "Font";
	}

}
