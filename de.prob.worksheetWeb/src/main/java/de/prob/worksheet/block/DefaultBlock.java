/**
 * 
 */
package de.prob.worksheet.block;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlType;

import de.prob.worksheet.IWorksheetMenuNode;
import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.WorksheetMenuNode;
import de.prob.worksheet.WorksheetObjectMapper;
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
	private boolean immediateEvaluation;
	private boolean inputAndOutput;

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
		this.immediateEvaluation = false;
		this.inputAndOutput = false;
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

	@Override
	public boolean isImmediateEvaluation() {
		return this.immediateEvaluation;
	}

	@Override
	public void setImmediateEvaluation(boolean immediateEvaluation) {
		this.immediateEvaluation = immediateEvaluation;

	}

	@Override
	public boolean isInputAndOutput() {
		return this.inputAndOutput;
	}

	@Override
	public void setInputAndOutput(boolean inputAndOuput) {
		this.inputAndOutput = inputAndOuput;
	}

	@Override
	public String getOverrideEditorContent() {
		return null;
	}

	@Override
	public String toString() {
		return "ID=" + this.id + " Type=" + this.getClass().getName();
	}

	public void initBlockMenu(String type, String[] excludes) {
		final ArrayList<IWorksheetMenuNode> menu = new ArrayList<IWorksheetMenuNode>();
		String[] blockTypes = this.getInputBlockTypes();
		final WorksheetMenuNode typeMenu = new WorksheetMenuNode(type, "", "");
		typeMenu.setTitle(true);
		Arrays.sort(excludes);

		for (final String typeName : blockTypes) {
			if (Arrays.binarySearch(excludes, typeName) >= 0
					|| typeName.equals(type))
				continue;
			final WorksheetMenuNode node = new WorksheetMenuNode(typeName, "",
					"");
			node.setClick("function(){$(this).closest('.ui-block').block('switchBlock','"
					+ typeName + "');}");
			node.setChar(typeName.charAt(0));
			typeMenu.addChild(node);
		}
		if (typeMenu.getChildren().length != 0) {
			menu.add(typeMenu);
			this.setMenu(menu.toArray(new IWorksheetMenuNode[menu.size()]));
			this.setHasMenu(true);
		} else {
			this.setHasMenu(false);
		}
	}

	private String[] getInputBlockTypes() {
		WorksheetObjectMapper mapper = ServletContextListener.INJECTOR
				.getInstance(WorksheetObjectMapper.class);
		return mapper.getInputBlockNames();
	}
}
