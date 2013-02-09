/**
 * 
 */
package de.prob.worksheet.block;

import javax.xml.bind.annotation.XmlType;

import de.prob.worksheet.editor.HTMLErrorEditor;

/**
 * @author Rene
 * 
 */

@XmlType(name = "HTMLErrorBlock")
public class HTMLErrorBlock extends DefaultBlock {
	public static final String typeID = "errorHtml";

	private boolean haltAll = false;

	public HTMLErrorBlock() {
		this.setEvaluatorType("");
		this.setOutput(true);
		this.setEditor(new HTMLErrorEditor());
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

	/**
	 * @return the haltAll
	 */
	public boolean getHaltAll() {
		return this.haltAll;
	}

	/**
	 * @param haltAll
	 *            the haltAll to set
	 */
	public void setHaltAll(boolean haltAll) {
		this.haltAll = haltAll;
	}

}
