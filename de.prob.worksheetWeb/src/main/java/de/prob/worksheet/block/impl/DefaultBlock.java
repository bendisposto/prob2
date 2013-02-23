package de.prob.worksheet.block.impl;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.WorksheetMenuNode;
import de.prob.worksheet.WorksheetObjectMapper;
import de.prob.worksheet.block.IBlockData;
import de.prob.worksheet.block.IBlockEvaluate;
import de.prob.worksheet.block.IBlockUI;
import de.prob.worksheet.editor.IEditorData;
import de.prob.worksheet.editor.impl.JavascriptEditor;

/**
 * @author Rene
 *
 */
@JsonTypeInfo(use = Id.NAME, include = As.EXTERNAL_PROPERTY, property = "objType")
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlSeeAlso(value = { JavascriptBlock.class, HTMLBlock.class,
		HTMLErrorBlock.class, InitializeStoreBlock.class,
		StoreValuesBlock.class })
public class DefaultBlock implements IBlockData, IBlockUI, IBlockEvaluate{
	public static final Logger logger = LoggerFactory
			.getLogger(DefaultBlock.class);
	public static final String typeID = "default";

	private String id;
	private String worksheetId;
	private boolean hasMenu;
	private final ArrayList<WorksheetMenuNode> menu;
	private IEditorData editor;
	private String evaluatorType;
	private boolean output;
	private boolean mark;
	private final ArrayList<String> outputBlockIds;
	private boolean immediateEvaluation;
	private boolean inputAndOutput;
	
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
	
	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.UIBlockData#getOverrideEditorContent()
	 */
	@Override
	@JsonIgnore
	@XmlTransient
	public String getOverrideEditorContent(){
		logger.trace("get: OverrideEditorContent=null");
		return null;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.IBlockUI#isImmediateEvaluation()
	 */
	@Override
	@JsonIgnore
	@XmlTransient
	public boolean isImmediateEvaluation(){
		logger.trace("get: immediateEvaluation={}", this.immediateEvaluation);
		return this.immediateEvaluation;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.IBlockEvaluate#setImmediateEvaluation(boolean)
	 */
	@JsonIgnore
	public void setImmediateEvaluation(boolean immediateEvaluation){
		logger.trace("set: immediateEvaluation={}", immediateEvaluation);
		this.immediateEvaluation = immediateEvaluation;

	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.UIBlockData#getId()
	 */
	@Override
	@JsonProperty(value = "id")
	@XmlID
	@XmlAttribute(name = "id")
	public String getId() {
		logger.trace("get: id={}", this.id);
		return this.id;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.UIBlockData#setId(java.lang.String)
	 */
	@Override
	@JsonProperty(value = "id")
	public void setId(String id){
		logger.trace("set: id={}", id);
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.IBlockUI#getWorksheetId()
	 */
	@Override
	@JsonProperty(value = "worksheetId")
	@XmlAttribute(name = "worksheetId")
	public String getWorksheetId(){
		logger.trace("get: id={}", this.worksheetId);
		return this.worksheetId;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.IBlockUI#setWorksheetId(java.lang.String)
	 */
	@Override
	@JsonProperty(value = "worksheetId")
	public void setWorksheetId(String worksheetId){
		logger.trace("set: id={}", worksheetId);
		this.worksheetId = worksheetId;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.IBlockUI#getHasMenu()
	 */
	@Override
	@JsonProperty(value = "hasMenu")
	@XmlTransient
	public boolean getHasMenu(){
		logger.trace("get: hasMenu={}", hasMenu);
		return this.hasMenu;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.IBlockUI#setHasMenu(boolean)
	 */
	@JsonProperty(value = "hasMenu")
	public void setHasMenu(boolean hasMenu){
		logger.trace("set: hasMenu={}", hasMenu);
		this.hasMenu = hasMenu;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.IBlockUI#getMenu()
	 */
	@Override
	@JsonProperty(value = "menu")
	@XmlTransient
	public WorksheetMenuNode[] getMenu(){
		logger.trace("get: menu={}", this.menu);
		return this.menu.toArray(new WorksheetMenuNode[this.menu.size()]);
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.IBlockUI#setMenu(de.prob.worksheet.WorksheetMenuNode[])
	 */
	@Override
	@JsonProperty(value = "menu")
	public void setMenu(WorksheetMenuNode[] menu){
		logger.trace("set: menu={}", menu);
		this.menu.clear();
		this.menu.addAll(Arrays.asList(menu));
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.UIBlockData#getEditor()
	 */
	@Override
	@JsonProperty(value = "editor")
	@XmlElement(name = "editor")
	public IEditorData getEditor(){
		logger.trace("get: editor={}", this.editor);
		return this.editor;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.UIBlockData#setEditor(de.prob.worksheet.editor.IWorksheetEditor)
	 */
	@Override
	@JsonProperty(value = "editor")
	public void setEditor(IEditorData editor){
		logger.trace("set: editor={}", editor);
		this.editor = editor;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.UIBlockData#getEvaluatorType()
	 */
	@Override
	@JsonProperty(value = "evaluatorType")
	@XmlAttribute(name = "evaluatorType")
	public String getEvaluatorType() {
		logger.trace("get: type={}", this.evaluatorType);
		return this.evaluatorType;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.UIBlockData#setEvaluatorType(java.lang.String)
	 */
	@Override
	@JsonProperty(value = "evaluatorType")
	public void setEvaluatorType(String evaluatorType){
		logger.trace("set: type={}", evaluatorType);
		this.evaluatorType = evaluatorType;
	}

	@JsonProperty(value = "isOutput")
	@XmlAttribute(name = "isOutput")
	public boolean isOutput(){
		logger.trace("get: isOutput={}", this.output);
		return this.output;
	}


	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.UIBlockData#setOutput(boolean)
	 */
	@Override
	@JsonProperty(value = "isOutput")
	public void setOutput(boolean output){
		logger.trace("set: isOutput={}", output);
		this.output = output;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.IBlockUI#getMark()
	 */
	@Override
	@JsonProperty(value = "mark")
	@XmlAttribute(name = "mark")
	public boolean getMark(){
		logger.trace("get: mark={}", this.mark);
		return this.mark;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.IBlockUI#setMark(boolean)
	 */
	@Override
	@JsonProperty(value = "mark")
	public void setMark(boolean marked){
		logger.trace("set: mark={}", marked);
		this.mark = marked;
	}


	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.UIBlockData#getOutputBlockIds()
	 */
	@Override
	@JsonProperty(value = "outputBlockIds")
	@XmlAttribute(name = "outputBlockIds")
	public String[] getOutputBlockIds(){
		logger.trace("get: outputBlockIds={}", this.outputBlockIds);
		return this.outputBlockIds.toArray(new String[this.outputBlockIds
				.size()]);
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.UIBlockData#setOutputBlockIds(java.lang.String[])
	 */
	@Override
	@JsonProperty(value = "outputBlockIds")
	public void setOutputBlockIds(String[] ids){
		logger.trace("set: OutputBlockIds={}", ids);
		this.outputBlockIds.clear();
		if (ids != null) {
			this.outputBlockIds.addAll(Arrays.asList(ids));
		}
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.UIBlockData#addOutputId(java.lang.String)
	 */
	@Override
	@JsonIgnore
	public void addOutputId(String id) {
		logger.trace("add: id={}", id);
		this.outputBlockIds.add(id);
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.IBlockUI#isInputAndOutput()
	 */
	@Override
	@JsonIgnore
	public boolean isInputAndOutput(){
		logger.trace("get: InputAndOutput={}", this.inputAndOutput);
		return this.inputAndOutput;
	}

	/* (non-Javadoc)
	 * @see de.prob.worksheet.block.IBlockUI#setInputAndOutput(boolean)
	 */
	@Override
	@JsonIgnore
	@XmlTransient
	public void setInputAndOutput(boolean inputAndOuput){
		logger.trace("set: inputAndOutput={}", inputAndOuput);
		this.inputAndOutput = inputAndOuput;
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