/**
 * 
 */
package de.prob.worksheet.block;

import de.prob.worksheet.editor.HTMLErrorEditor;

/**
 * @author Rene
 * 
 */
public class HTMLErrorBlock extends DefaultBlock {
	public static final String	typeID	= "errorHtml";
	private boolean haltAll=false;
	
	public HTMLErrorBlock() {
		this.setEvaluatorType("");
		this.setOutput(true);
		this.setEditor(new HTMLErrorEditor());
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

	/**
	 * @return the haltAll
	 */
	public boolean getHaltAll() {
		return this.haltAll;
	}

	/**
	 * @param haltAll the haltAll to set
	 */
	public void setHaltAll(boolean haltAll) {
		this.haltAll = haltAll;
	}
	
}
