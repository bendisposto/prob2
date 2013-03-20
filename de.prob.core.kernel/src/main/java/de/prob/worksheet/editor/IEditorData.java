package de.prob.worksheet.editor;

import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface IEditorData {

	@JsonProperty(value = "content")
	@XmlValue
	public abstract String getEditorContent();

	@JsonProperty(value = "content")
	public abstract void setEditorContent(String editorContent);

}