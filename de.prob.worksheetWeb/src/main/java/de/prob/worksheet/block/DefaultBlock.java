/**
 * 
 */
package de.prob.worksheet.block;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlType;

import de.prob.worksheet.IWorksheetMenuNode;
import de.prob.worksheet.WorksheetMenuNode;
import de.prob.worksheet.editor.IWorksheetEditor;
import de.prob.worksheet.editor.JavascriptEditor;

/**
 * @author Rene
 * 
 */

@XmlType(name = "DefaultBlock")
public class DefaultBlock extends IBlock {

	public static final String typeID = "default";

	private String id;
	private String worksheetId;
	private boolean hasMenu;
	private final ArrayList<IWorksheetMenuNode> menu;
	private IWorksheetEditor editor;
	private String evaluatorType;
	private boolean output;
	private boolean mark;
	private final ArrayList<String> outputBlockIds;

	/**
	 * 
	 */
	public DefaultBlock() {
		this.menu = new ArrayList<IWorksheetMenuNode>();
		this.outputBlockIds = new ArrayList<String>();
		this.editor = new JavascriptEditor();
		this.hasMenu = true;
		this.output = false;
		this.mark = false;
		this.evaluatorType = "state";

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#getId()
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#setId(java.lang.String)
	 */
	@Override
	public void setId(final String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#getWorksheetId()
	 */
	@Override
	public String getWorksheetId() {
		return this.worksheetId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#setWorksheetId(java.lang.String)
	 */
	@Override
	public void setWorksheetId(final String worksheetId) {
		this.worksheetId = worksheetId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#isHasMenu()
	 */
	@Override
	public boolean getHasMenu() {
		return this.hasMenu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#setHasMenu(boolean)
	 */
	@Override
	public void setHasMenu(final boolean hasMenu) {
		this.hasMenu = hasMenu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#getChildren()
	 */
	@Override
	public IWorksheetMenuNode[] getMenu() {
		return this.menu.toArray(new WorksheetMenuNode[this.menu.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#setChildren(de.prob.worksheet.
	 * WorksheetMenuNode[])
	 */
	@Override
	public void setMenu(final IWorksheetMenuNode[] menu) {
		this.menu.clear();
		this.menu.addAll(Arrays.asList(menu));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#getEditor()
	 */
	@Override
	public IWorksheetEditor getEditor() {
		return this.editor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.IWorksheetBlock#setEditor(de.prob.worksheet.editor.
	 * WorksheetEditor)
	 */
	@Override
	public void setEditor(final IWorksheetEditor editor) {
		this.editor = editor;
	}

	@Override
	public String getEvaluatorType() {
		return this.evaluatorType;
	}

	@Override
	public void setEvaluatorType(final String evaluatorType) {
		this.evaluatorType = evaluatorType;
	}

	@Override
	public boolean getOutput() {
		return this.output;
	}

	@Override
	public void setOutput(final boolean output) {
		this.output = output;
	}

	@Override
	public void undo() {
		System.out.println("Implement undo");
	}

	@Override
	public boolean getMark() {
		return this.mark;
	}

	@Override
	public void setMark(final boolean marked) {
		this.mark = marked;
	}

	@Override
	public void addOutputId(final String id) {
		this.outputBlockIds.add(id);
	}

	@Override
	public String[] getOutputBlockIds() {
		return this.outputBlockIds.toArray(new String[this.outputBlockIds
				.size()]);
	}

	@Override
	public void setOutputBlockIds(final String[] ids) {
		this.outputBlockIds.clear();
		if (ids != null) {
			this.outputBlockIds.addAll(Arrays.asList(ids));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DefaultBlock))
			return false;
		return this.id.equals(((DefaultBlock) obj).getId());

	}

}
