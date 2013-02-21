/**
 * 
 */
package de.prob.worksheet.block;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public static final Logger logger = LoggerFactory
			.getLogger(DefaultBlock.class);
	public static final String typeID = "default";

	private String id;
	private String worksheetId;
	private boolean hasMenu;
	private final ArrayList<WorksheetMenuNode> menu;
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
		logger.trace("in:");
		this.menu = new ArrayList<WorksheetMenuNode>();
		this.outputBlockIds = new ArrayList<String>();
		this.editor = new JavascriptEditor();
		this.hasMenu = true;
		this.output = false;
		this.mark = false;
		this.evaluatorType = "state";
		this.immediateEvaluation = false;
		this.inputAndOutput = false;
		logger.trace("return:");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#getId()
	 */
	@Override
	public String getId() {
		logger.trace("get: id={}", this.id);
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#setId(java.lang.String)
	 */
	@Override
	public void setId(final String id) {
		logger.trace("set: id={}", id);
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#getWorksheetId()
	 */
	@Override
	public String getWorksheetId() {
		logger.trace("get: id={}", this.worksheetId);
		return this.worksheetId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#setWorksheetId(java.lang.String)
	 */
	@Override
	public void setWorksheetId(final String worksheetId) {
		logger.trace("set: id={}", worksheetId);
		this.worksheetId = worksheetId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#isHasMenu()
	 */
	@Override
	public boolean getHasMenu() {
		logger.trace("get: hasMenu={}", hasMenu);
		return this.hasMenu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#setHasMenu(boolean)
	 */
	@Override
	public void setHasMenu(final boolean hasMenu) {
		logger.trace("set: hasMenu={}", hasMenu);
		this.hasMenu = hasMenu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#getChildren()
	 */
	@Override
	public WorksheetMenuNode[] getMenu() {
		logger.trace("get: menu={}", this.menu);
		return this.menu.toArray(new WorksheetMenuNode[this.menu.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.IWorksheetBlock#setChildren(de.prob.worksheet.
	 * WorksheetMenuNode[])
	 */
	@Override
	public void setMenu(final WorksheetMenuNode[] menu) {
		logger.trace("set: menu={}", menu);
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
		logger.trace("get: editor={}", this.editor);
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
		logger.trace("set: editor={}", editor);
		this.editor = editor;
	}

	@Override
	public String getEvaluatorType() {
		logger.trace("get: type={}", this.evaluatorType);
		return this.evaluatorType;
	}

	@Override
	public void setEvaluatorType(final String evaluatorType) {
		logger.trace("set: type={}", evaluatorType);
		this.evaluatorType = evaluatorType;
	}

	@Override
	public boolean isOutput() {
		logger.trace("get: isOutput={}", this.output);
		return this.output;
	}

	@Override
	public void setOutput(final boolean output) {
		logger.trace("set: isOutput={}", output);
		this.output = output;
	}

	@Override
	public boolean getMark() {
		logger.trace("get: mark={}", this.mark);
		return this.mark;
	}

	@Override
	public void setMark(final boolean marked) {
		logger.trace("set: mark={}", marked);
		this.mark = marked;
	}

	@Override
	public void addOutputId(final String id) {
		logger.trace("add: id={}", id);
		this.outputBlockIds.add(id);
	}

	@Override
	public String[] getOutputBlockIds() {
		logger.trace("get: outputBlockIds={}", this.outputBlockIds);
		return this.outputBlockIds.toArray(new String[this.outputBlockIds
				.size()]);
	}

	@Override
	public void setOutputBlockIds(final String[] ids) {
		logger.trace("set: OutputBlockIds={}", ids);
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
		logger.trace("get: immediateEvaluation={}", this.immediateEvaluation);
		return this.immediateEvaluation;
	}

	@Override
	public void setImmediateEvaluation(boolean immediateEvaluation) {
		logger.trace("set: immediateEvaluation={}", immediateEvaluation);
		this.immediateEvaluation = immediateEvaluation;

	}

	@Override
	public boolean isInputAndOutput() {
		logger.trace("get: InputAndOutput={}", this.inputAndOutput);
		return this.inputAndOutput;
	}

	@Override
	public void setInputAndOutput(boolean inputAndOuput) {
		logger.trace("set: inputAndOutput={}", inputAndOuput);
		this.inputAndOutput = inputAndOuput;
	}

	@Override
	public String getOverrideEditorContent() {
		logger.trace("get: OverrideEditorContent=null");
		return null;
	}

	@Override
	public String toString() {
		return "ID=" + this.id + " Type=" + this.getClass().getName();
	}

	public void initBlockMenu(String type, String[] excludes) {
		logger.trace("in: type={}, excludes={}", type, excludes);
		final ArrayList<WorksheetMenuNode> menu = new ArrayList<WorksheetMenuNode>();
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
			node.setClick("function(){$(this).block('switchBlock','" + typeName
					+ "');}");
			node.setChar(typeName.charAt(0));
			typeMenu.addChild(node);
		}
		if (typeMenu.getChildren().length != 0) {
			menu.add(typeMenu);
			this.setMenu(menu.toArray(new WorksheetMenuNode[menu.size()]));
			this.setHasMenu(true);
		} else {
			this.setHasMenu(false);
		}
		logger.trace("return:");
	}

	private String[] getInputBlockTypes() {
		logger.trace("in:");
		WorksheetObjectMapper mapper = ServletContextListener.INJECTOR
				.getInstance(WorksheetObjectMapper.class);
		logger.trace("return: inputBlockNames={}", mapper.getInputBlockNames());
		return mapper.getInputBlockNames();
	}
}
