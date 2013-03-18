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
import de.prob.worksheet.WorksheetObjectMapper;
import de.prob.worksheet.block.IBlockData;
import de.prob.worksheet.block.IBlockEvaluate;
import de.prob.worksheet.block.IBlockUI;
import de.prob.worksheet.document.impl.WorksheetMenuNode;
import de.prob.worksheet.editor.impl.CodeMirrorJSEditor;
import de.prob.worksheet.editor.impl.DefaultEditor;

/**
 * @author Rene
 * 
 */
@JsonTypeInfo(use = Id.NAME, include = As.EXTERNAL_PROPERTY, property = "objType")
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlSeeAlso(value = { EventBBlock.class, HTMLBlock.class, HTMLErrorBlock.class,
		InitializeStoreBlock.class, StoreValuesBlock.class,
		DocumentationBlock.class, TreeBlock.class, AnalyzeAstBlock.class })
public abstract class DefaultBlock implements IBlockData, IBlockUI,
		IBlockEvaluate {
	public static final Logger logger = LoggerFactory
			.getLogger(DefaultBlock.class);
	public static final String typeID = "default";

	private String id;
	private boolean hasMenu;
	private final ArrayList<WorksheetMenuNode> menu;
	private DefaultEditor editor;
	private String evaluatorType;
	private boolean output;
	private boolean mark = true;
	private final ArrayList<String> outputBlockIds;
	private boolean immediateEvaluation;
	private boolean inputAndOutput;
	private boolean toUnicode;
	private boolean neitherInNorOutput;

	public DefaultBlock() {
		DefaultBlock.logger.trace("in:");
		menu = new ArrayList<WorksheetMenuNode>();
		outputBlockIds = new ArrayList<String>();
		editor = new CodeMirrorJSEditor();
		hasMenu = true;
		output = false;
		mark = true;
		evaluatorType = "state";
		immediateEvaluation = false;
		inputAndOutput = false;
		DefaultBlock.logger.trace("return:");
		setToUnicode(false);
		setNeitherInNorOutput(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.UIBlockData#getOverrideEditorContent()
	 */
	@Override
	@JsonIgnore
	@XmlTransient
	public String getOverrideEditorContent() {
		DefaultBlock.logger.trace("get: OverrideEditorContent=null");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.IBlockUI#isImmediateEvaluation()
	 */
	@Override
	@JsonIgnore
	@XmlTransient
	public boolean isImmediateEvaluation() {
		DefaultBlock.logger.trace("get: immediateEvaluation={}",
				immediateEvaluation);
		return immediateEvaluation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.block.IBlockEvaluate#setImmediateEvaluation(boolean)
	 */
	@Override
	@JsonIgnore
	public void setImmediateEvaluation(boolean immediateEvaluation) {
		DefaultBlock.logger.trace("set: immediateEvaluation={}",
				immediateEvaluation);
		this.immediateEvaluation = immediateEvaluation;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.UIBlockData#getId()
	 */
	@Override
	@JsonProperty(value = "id")
	@XmlID
	@XmlAttribute(name = "id")
	public String getId() {
		DefaultBlock.logger.trace("get: id={}", id);
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.UIBlockData#setId(java.lang.String)
	 */
	@Override
	@JsonProperty(value = "id")
	public void setId(String id) {
		DefaultBlock.logger.trace("set: id={}", id);
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.IBlockUI#getHasMenu()
	 */
	@Override
	@JsonProperty(value = "hasMenu")
	@XmlTransient
	public boolean getHasMenu() {
		DefaultBlock.logger.trace("get: hasMenu={}", hasMenu);
		return hasMenu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.IBlockUI#setHasMenu(boolean)
	 */
	@Override
	@JsonProperty(value = "hasMenu")
	public void setHasMenu(boolean hasMenu) {
		DefaultBlock.logger.trace("set: hasMenu={}", hasMenu);
		this.hasMenu = hasMenu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.IBlockUI#getMenu()
	 */
	@Override
	@JsonProperty(value = "menu")
	@XmlTransient
	public WorksheetMenuNode[] getMenu() {
		DefaultBlock.logger.trace("get: menu={}", menu);
		return menu.toArray(new WorksheetMenuNode[menu.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.block.IBlockUI#setMenu(de.prob.worksheet.WorksheetMenuNode
	 * [])
	 */
	@Override
	@JsonProperty(value = "menu")
	public void setMenu(WorksheetMenuNode[] menu) {
		DefaultBlock.logger.trace("set: menu={}", menu);
		this.menu.clear();
		this.menu.addAll(Arrays.asList(menu));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.UIBlockData#getEditor()
	 */
	@Override
	@JsonProperty(value = "editor")
	@XmlElement(name = "editor")
	public DefaultEditor getEditor() {
		DefaultBlock.logger.trace("get: editor={}", editor);
		return editor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.block.UIBlockData#setEditor(de.prob.worksheet.editor
	 * .IWorksheetEditor)
	 */
	@Override
	@JsonProperty(value = "editor")
	public void setEditor(DefaultEditor editor) {
		DefaultBlock.logger.trace("set: editor={}", editor);
		this.editor = editor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.UIBlockData#getEvaluatorType()
	 */
	@Override
	@JsonProperty(value = "evaluatorType")
	@XmlAttribute(name = "evaluatorType")
	public String getEvaluatorType() {
		DefaultBlock.logger.trace("get: type={}", evaluatorType);
		return evaluatorType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.block.UIBlockData#setEvaluatorType(java.lang.String)
	 */
	@Override
	@JsonProperty(value = "evaluatorType")
	public void setEvaluatorType(String evaluatorType) {
		DefaultBlock.logger.trace("set: type={}", evaluatorType);
		this.evaluatorType = evaluatorType;
	}

	@Override
	@JsonProperty(value = "isOutput")
	@XmlAttribute(name = "isOutput")
	public boolean isOutput() {
		DefaultBlock.logger.trace("get: isOutput={}", output);
		return output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.UIBlockData#setOutput(boolean)
	 */
	@Override
	@JsonProperty(value = "isOutput")
	public void setOutput(boolean output) {
		DefaultBlock.logger.trace("set: isOutput={}", output);
		if (output)
			mark = false;
		this.output = output;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.IBlockUI#getMark()
	 */
	@Override
	@JsonProperty(value = "mark")
	@XmlTransient
	public boolean getMark() {
		DefaultBlock.logger.trace("get: mark={}", mark);
		return mark;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.IBlockUI#setMark(boolean)
	 */
	@Override
	@JsonProperty(value = "mark")
	public void setMark(boolean marked) {
		DefaultBlock.logger.trace("set: mark={}", marked);
		mark = marked;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.UIBlockData#getOutputBlockIds()
	 */
	@Override
	@JsonProperty(value = "outputBlockIds")
	@XmlAttribute(name = "outputBlockIds")
	public String[] getOutputBlockIds() {
		DefaultBlock.logger.trace("get: outputBlockIds={}", outputBlockIds);
		return outputBlockIds.toArray(new String[outputBlockIds.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.prob.worksheet.block.UIBlockData#setOutputBlockIds(java.lang.String[])
	 */
	@Override
	@JsonProperty(value = "outputBlockIds")
	public void setOutputBlockIds(String[] ids) {
		DefaultBlock.logger.trace("set: OutputBlockIds={}", ids);
		outputBlockIds.clear();
		if (ids != null) {
			outputBlockIds.addAll(Arrays.asList(ids));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.UIBlockData#addOutputId(java.lang.String)
	 */
	@Override
	@JsonIgnore
	public void addOutputId(String id) {
		DefaultBlock.logger.trace("add: id={}", id);
		outputBlockIds.add(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.IBlockUI#isInputAndOutput()
	 */
	@Override
	@JsonIgnore
	public boolean isInputAndOutput() {
		DefaultBlock.logger.trace("get: InputAndOutput={}", inputAndOutput);
		return inputAndOutput;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.block.IBlockUI#setInputAndOutput(boolean)
	 */
	@Override
	@JsonIgnore
	@XmlTransient
	public void setInputAndOutput(boolean inputAndOuput) {
		DefaultBlock.logger.trace("set: inputAndOutput={}", inputAndOuput);
		inputAndOutput = inputAndOuput;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.editor.IEditorUI#isToUnicode()
	 */
	@Override
	@JsonProperty("toUnicode")
	@XmlTransient
	public boolean isToUnicode() {
		return toUnicode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.prob.worksheet.editor.IEditorUI#setToUnicode(boolean)
	 */
	@Override
	@JsonProperty("toUnicode")
	public void setToUnicode(boolean toUnicode) {
		this.toUnicode = toUnicode;
	}

	@Override
	@XmlTransient
	public boolean isNeitherInNorOutput() {
		return neitherInNorOutput;
	}

	@Override
	public void setNeitherInNorOutput(boolean neitherInNorOutput) {
		if (neitherInNorOutput)
			mark = false;
		this.neitherInNorOutput = neitherInNorOutput;
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
		return id.equals(((DefaultBlock) obj).getId());

	}

	public void initBlockMenu(String type, String[] excludes) {
		DefaultBlock.logger.trace("in: type={}, excludes={}", type, excludes);
		final ArrayList<WorksheetMenuNode> menu = new ArrayList<WorksheetMenuNode>();
		String[] blockTypes = getInputBlockTypes();
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
			setMenu(menu.toArray(new WorksheetMenuNode[menu.size()]));
			setHasMenu(true);
		} else {
			setHasMenu(false);
		}
		DefaultBlock.logger.trace("return:");
	}

	private String[] getInputBlockTypes() {
		DefaultBlock.logger.trace("in:");
		WorksheetObjectMapper mapper = ServletContextListener.INJECTOR
				.getInstance(WorksheetObjectMapper.class);
		DefaultBlock.logger.trace("return: inputBlockNames={}",
				mapper.getInputBlockNames());
		return mapper.getInputBlockNames();
	}
}