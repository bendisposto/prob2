package de.prob.worksheet.block;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import de.prob.worksheet.IWorksheetMenuNode;
import de.prob.worksheet.editor.IWorksheetEditor;

@JsonTypeInfo(use = Id.NAME, include = As.EXTERNAL_PROPERTY, property = "objType")
@JsonSubTypes({ @Type(value = JavascriptBlock.class, name = "javascript"),
				@Type(value = HTMLBlock.class, name = "html"),
				@Type(value = HTMLErrorBlock.class, name = "HTMLErrorBlock"),
				@Type(value = DefaultBlock.class, name = "default") })
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlSeeAlso(value={JavascriptBlock.class,HTMLBlock.class,HTMLErrorBlock.class,DefaultBlock.class})
public abstract class IBlock {

	@JsonProperty(value = "id")
	@XmlID
	@XmlAttribute(name="id")
	public abstract String getId();

	@JsonProperty(value = "id")
	public abstract void setId(String id);

	@JsonProperty(value = "worksheetId")
	@XmlAttribute(name="worksheetId")
	public abstract String getWorksheetId();

	@JsonProperty(value = "worksheetId")
	public abstract void setWorksheetId(String worksheetId);

	@JsonProperty(value = "hasMenu")
	@XmlTransient
	public abstract boolean getHasMenu();

	@JsonProperty(value = "hasMenu")
	public abstract void setHasMenu(boolean hasMenu);

	@JsonProperty(value = "menu")
	@XmlTransient
	public abstract IWorksheetMenuNode[] getMenu();

	@JsonProperty(value = "menu")
	public abstract void setMenu(IWorksheetMenuNode[] menu);

	@JsonProperty(value = "editor")
	@XmlElement(name="editor")
	public abstract IWorksheetEditor getEditor();

	@JsonProperty(value = "editor")
	public abstract void setEditor(IWorksheetEditor editor);

	@JsonProperty(value = "evaluatorType")
	@XmlAttribute(name="evaluatorType")
	public abstract String getEvaluatorType();

	@JsonProperty(value = "evaluatorType")
	public abstract void setEvaluatorType(String evaluatorType);

	@JsonProperty(value = "isOutput")
	@XmlAttribute(name="isOutput")
	public abstract boolean getOutput();

	@JsonProperty(value = "isOutput")
	public abstract void setOutput(boolean output);

	@JsonProperty(value = "mark")
	@XmlAttribute(name="mark")
	public abstract boolean getMark();

	@JsonProperty(value = "mark")
	public abstract void setMark(boolean b);

	@JsonProperty(value = "outputBlockIds")
	@XmlAttribute(name="outputBlockIds")
	public abstract String[] getOutputBlockIds();

	@JsonProperty(value = "outputBlockIds")
	public abstract void setOutputBlockIds(String[] ids);

	@JsonIgnore
	public abstract void addOutputId(String id);

	@JsonIgnore
	public abstract void undo();
}