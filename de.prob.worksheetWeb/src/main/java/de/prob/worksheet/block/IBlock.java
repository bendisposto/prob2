package de.prob.worksheet.block;

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

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = JavascriptBlock.class, name = "javascript"),
				@Type(value = HTMLBlock.class, name = "html"),
				@Type(value = HTMLErrorBlock.class, name = "HTMLErrorBlock") })
@JsonIgnoreProperties(ignoreUnknown = true)
public interface IBlock {

	@JsonProperty(value = "id")
	public abstract String getId();

	@JsonProperty(value = "id")
	public abstract void setId(String id);

	@JsonProperty(value = "worksheetId")
	public abstract String getWorksheetId();

	@JsonProperty(value = "worksheetId")
	public abstract void setWorksheetId(String worksheetId);

	@JsonProperty(value = "hasMenu")
	public abstract boolean getHasMenu();

	@JsonProperty(value = "hasMenu")
	public abstract void setHasMenu(boolean hasMenu);

	@JsonProperty(value = "menu")
	public abstract IWorksheetMenuNode[] getMenu();

	@JsonProperty(value = "menu")
	@JsonIgnore
	public abstract void setMenu(IWorksheetMenuNode[] menu);

	@JsonProperty(value = "editor")
	public abstract IWorksheetEditor getEditor();

	@JsonProperty(value = "editor")
	public abstract void setEditor(IWorksheetEditor editor);

	@JsonProperty(value = "evaluatorType")
	public abstract String getEvaluatorType();

	@JsonProperty(value = "evaluatorType")
	public abstract void setEvaluatorType(String evaluatorType);

	@JsonProperty(value = "isOutput")
	public abstract boolean isOutput();

	@JsonProperty(value = "isOutput")
	public abstract void setOutput(boolean output);

	@JsonProperty(value = "mark")
	public abstract boolean isMark();

	@JsonProperty(value = "mark")
	public abstract void setMark(boolean b);

	@JsonProperty(value = "outputBlockIds")
	public abstract String[] getOutputBlockIds();

	@JsonProperty(value = "outputBlockIds")
	public abstract void setOutputIds(String[] ids);

	@JsonIgnore
	public abstract void addOutputId(String id);

	@JsonIgnore
	public abstract void undo();
}