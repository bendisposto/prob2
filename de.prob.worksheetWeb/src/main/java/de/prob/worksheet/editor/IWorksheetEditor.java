package de.prob.worksheet.editor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.EXTERNAL_PROPERTY, property = "objType")
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlSeeAlso(value = { DefaultEditor.class, HTMLEditor.class,
		HTMLErrorEditor.class, JavascriptEditor.class })
public abstract class IWorksheetEditor {

	@JsonProperty(value = "html")
	@XmlTransient
	public abstract String getHTMLContent();

	@JsonProperty(value = "content")
	@XmlValue
	public abstract String getEditorContent();

	@JsonProperty(value = "init")
	@XmlTransient
	public abstract String getInitializationFunction();

	@JsonProperty(value = "getContent")
	@XmlTransient
	public abstract String getGetContentScript();

	@JsonProperty(value = "setContent")
	@XmlTransient
	public abstract String getSetContentScript();

	@JsonProperty(value = "setFocus")
	@XmlTransient
	public abstract String getSetFocusScript();

	@JsonProperty(value = "destroy")
	@XmlTransient
	public abstract String getDestroyScript();

	@JsonProperty(value = "cssURLs")
	@XmlTransient
	public abstract String[] getCSSHREFs();

	@JsonProperty(value = "jsURLs")
	@XmlTransient
	public abstract String[] getJavascriptHREFs();

	@JsonProperty(value = "html")
	public abstract void setHTMLContent(String htmlContent);

	@JsonProperty(value = "content")
	public abstract void setEditorContent(String editorContent);

	/**
	 * Initialization function for your internal js Editor.
	 * 
	 * Your js Editors content changed event must be bound to the _editorChanged
	 * function. (e.g.
	 * obj.change($.proxy($("#"+this.id).data("editor")._editorChanged
	 * ,$("#"+this.id).data("editor")));)for text areas
	 * 
	 * Important the first call to _editorChanged is ignored
	 * 
	 * @param initFunction
	 */
	@JsonIgnore
	public abstract void setInitializationFunction(String initFunction);

	@JsonIgnore
	public abstract void setGetContentScript(String script);

	@JsonIgnore
	public abstract void setSetFocusScript(String script);

	@JsonIgnore
	public abstract void setDestroyScript(String script);

	@JsonIgnore
	public abstract void setSetContentScript(String script);

	@JsonProperty(value = "cssURLs")
	public abstract void setCSSHREFs(String[] cssHref);

	@JsonProperty(value = "jsURLs")
	public abstract void setJavascriptHREFs(String[] javascriptHrefs);

	@JsonProperty(value = "id")
	@XmlAttribute(name = "id")
	@XmlID
	public abstract String getId();

	@JsonProperty(value = "id")
	public abstract void setId(final String id);

	public abstract void addJavascriptHref(String href);

	public abstract void addCSSHref(String href);
}
