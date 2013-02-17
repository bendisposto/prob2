/**
 * 
 */
package de.prob.worksheet.block;

import javax.xml.bind.annotation.XmlType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.prob.worksheet.editor.HTMLEditor;

/**
 * @author Rene
 * 
 */

@XmlType(name = "HTMLBlock")
public class HTMLBlock extends DefaultBlock {
	public static final String typeID = "HTML";

	@Inject
	public HTMLBlock() {
		this.setEvaluatorType("");
		this.setOutput(true);
		this.setEditor(new HTMLEditor());
		this.setHasMenu(false);

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
