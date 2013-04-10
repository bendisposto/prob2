package de.prob.worksheet.block;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.prob.worksheet.editor.IEditorData;
import de.prob.worksheet.editor.impl.DefaultEditor;

public interface IBlockData {

	@JsonProperty(value = "id")
	@XmlID
	@XmlAttribute(name = "id")
	public abstract String getId();

	@JsonProperty(value = "id")
	public abstract void setId(String id);

	@JsonProperty(value = "editor")
	@XmlElement(name = "editor")
	public abstract IEditorData getEditor();

	@JsonProperty(value = "editor")
	public abstract void setEditor(DefaultEditor editor);

	@JsonProperty(value = "evaluatorType")
	@XmlAttribute(name = "evaluatorType")
	public abstract String getEvaluatorType();

	@JsonProperty(value = "evaluatorType")
	public abstract void setEvaluatorType(String evaluatorType);

	@JsonProperty(value = "isOutput")
	@XmlAttribute(name = "isOutput")
	public abstract boolean isOutput();

	@JsonProperty(value = "isOutput")
	public abstract void setOutput(boolean output);

	@JsonProperty(value = "outputBlockIds")
	@XmlAttribute(name = "outputBlockIds")
	public abstract String[] getOutputBlockIds();

	@JsonProperty(value = "outputBlockIds")
	public abstract void setOutputBlockIds(String[] ids);

	@JsonIgnore
	public abstract void addOutputId(String id);

}