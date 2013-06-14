/**
 * 
 */
package de.prob.worksheet.block.impl;

import javax.xml.bind.annotation.XmlType;

import com.google.inject.Inject;

import de.prob.worksheet.editor.impl.HTMLDiv;

/**
 * @author Rene
 * 
 */

@XmlType(name = "HTMLBlock")
public class HTMLBlock extends DefaultBlock {
	public static final String typeID = "HTML";
	public static final String PRINT_NAME = "HTML";

	@Inject
	public HTMLBlock() {
		setEvaluatorType("");
		setOutput(true);
		setEditor(new HTMLDiv());
		setHasMenu(false);
		setToUnicode(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		// TODO Do we need an overriden default equals method ????
		return super.equals(obj);
	}
}
