/**
 * 
 */
package de.prob.worksheet.block;

import de.prob.worksheet.editor.HTMLEditor;

/**
 * @author Rene
 * 
 */
public class HTMLBlock extends DefaultBlock {
	public static final String	typeID	= "html";

	public HTMLBlock() {
		this.setEvaluatorType("");
		this.setOutput(true);
		this.setEditor(new HTMLEditor());
		this.setHasMenu(false);

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}
}
