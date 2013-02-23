package de.prob.worksheet.editor.impl;

import java.util.ArrayList;
import java.util.Arrays;

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

import de.prob.worksheet.editor.IEditorData;
import de.prob.worksheet.editor.IEditorUI;

@JsonTypeInfo(use = Id.NAME, include = As.EXTERNAL_PROPERTY, property = "objType")
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlSeeAlso(value = { HTMLEditor.class,
		HTMLErrorEditor.class, JavascriptEditor.class })
public class DefaultEditor implements IEditorData, IEditorUI {
	private String HTMLContent;
	private String EditorContent = "";
	private String InitFunction;
	private String getContentScript;
	private String destroyScript;
	private ArrayList<String> CSSHREFs;
	private ArrayList<String> JavascriptHREFs;
	private String id;
	private String setContentScript;
	private String setFocusScript;
	private boolean newlineToHtml;
	private boolean escapeHtml;
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#getHTMLContent()
	 */
	
	public DefaultEditor() {
		this.CSSHREFs = new ArrayList<String>();
		this.JavascriptHREFs = new ArrayList<String>();
		this.setHTMLContent("<textarea class=\"editor-object ui-editor-padding\"></textarea>");
		this.setInitializationFunction("function(){"
				+ "var obj = $($(\"#\"+this.id).find(\".editor-object\").first());"
				+ "obj.change($.proxy($(\"#\"+this.id).data(\"editor\")._editorChanged,$(\"#\"+this.id).data(\"editor\")));"
				+ "return obj;}");
		this.setDestroyScript(null);
		this.setSetContentScript("function(content) {"
				+ "var obj = $($(\"#\"+this.id).find(\".editor-object\").first());"
				+ "obj.val(content);}");
		this.setSetContentScript("function() {"
				+ "var obj = $($(\"#\"+this.id).find(\".editor-object\").first());"
				+ "return obj.val();}");
		this.setSetFocusScript("function(){$($(\"#\"+this.id).find(\".editor-object\").focus();}");
		this.setNewlineToHtml(false);
		this.setEscapeHtml(false);
	}
	
	@Override
	@JsonProperty(value = "html")
	@XmlTransient
	public String getHTMLContent() {
		return this.HTMLContent;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorData#getEditorContent()
	 */
	@Override
	@JsonProperty(value = "content")
	@XmlValue
	public String getEditorContent() {
		return this.EditorContent;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#getInitializationFunction()
	 */
	@Override
	@JsonProperty(value = "init")
	@XmlTransient
	public String getInitializationFunction() {
		return this.InitFunction;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#getGetContentScript()
	 */
	@Override
	@JsonProperty(value = "getContent")
	@XmlTransient
	public String getGetContentScript() {
		return this.getContentScript;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#getSetContentScript()
	 */
	@Override
	@JsonProperty(value = "setContent")
	@XmlTransient
	public String getSetContentScript() {
		return this.setContentScript;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#getSetFocusScript()
	 */
	@Override
	@JsonProperty(value = "setFocus")
	@XmlTransient
	public String getSetFocusScript() {
		return this.setFocusScript;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#getDestroyScript()
	 */
	@Override
	@JsonProperty(value = "destroy")
	@XmlTransient
	public String getDestroyScript() {
		return this.destroyScript;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#getCSSHREFs()
	 */
	@Override
	@JsonProperty(value = "cssURLs")
	@XmlTransient
	public String[] getCSSHREFs() {
		return this.CSSHREFs.toArray(new String[this.CSSHREFs.size()]);
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#getJavascriptHREFs()
	 */
	@Override
	@JsonProperty(value = "jsURLs")
	@XmlTransient
	public String[] getJavascriptHREFs() {
		return this.JavascriptHREFs.toArray(new String[this.JavascriptHREFs
				.size()]);
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#setHTMLContent(java.lang.String)
	 */
	@Override
	@JsonProperty(value = "html")
	public void setHTMLContent(final String htmlContent) {
		this.HTMLContent = htmlContent;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorData#setEditorContent(java.lang.String)
	 */
	@Override
	@JsonProperty(value = "content")
	public void setEditorContent(final String editorContent) {
		this.EditorContent = editorContent;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#setInitializationFunction(java.lang.String)
	 */
	@Override
	@JsonIgnore
	public void setInitializationFunction(final String initFunction) {
		this.InitFunction = initFunction;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#setGetContentScript(java.lang.String)
	 */
	@Override
	@JsonIgnore
	public void setGetContentScript(final String script) {
		this.getContentScript = script;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#setSetFocusScript(java.lang.String)
	 */
	@Override
	@JsonIgnore
	public void setSetFocusScript(String script) {
		this.setFocusScript = script;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#setDestroyScript(java.lang.String)
	 */
	@Override
	@JsonIgnore
	public void setDestroyScript(final String script) {
		this.destroyScript = script;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#setSetContentScript(java.lang.String)
	 */
	@Override
	@JsonIgnore
	public void setSetContentScript(final String script) {
		this.setContentScript = script;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#setCSSHREFs(java.lang.String[])
	 */
	@Override
	@JsonProperty(value = "cssURLs")
	public void setCSSHREFs(final String[] cssHref) {
		this.CSSHREFs = new ArrayList<String>(Arrays.asList(cssHref));
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#setJavascriptHREFs(java.lang.String[])
	 */
	@Override
	@JsonProperty(value = "jsURLs")
	public void setJavascriptHREFs(final String[] javascriptHrefs) {
		this.JavascriptHREFs = new ArrayList<String>(
				Arrays.asList(javascriptHrefs));
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#getId()
	 */
	@Override
	@JsonProperty(value = "id")
	@XmlAttribute(name = "id")
	@XmlID
	public String getId() {
		return this.id;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#setId(java.lang.String)
	 */
	@Override
	@JsonProperty(value = "id")
	public void setId(final String id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#addJavascriptHref(java.lang.String)
	 */
	@Override
	public void addJavascriptHref(final String href) {
		this.JavascriptHREFs.add(href);
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#addCSSHref(java.lang.String)
	 */
	@Override
	public void addCSSHref(final String href) {
		this.CSSHREFs.add(href);
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#setNewlineToHtml(boolean)
	 */
	@Override
	@XmlTransient
	public void setNewlineToHtml(boolean newlineToHtml) {
		this.newlineToHtml = newlineToHtml;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#isNewlineToHtml()
	 */
	@Override
	@JsonProperty(value = "newlineToHtml")
	public boolean isNewlineToHtml() {
		return newlineToHtml;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#setEscapeHtml(boolean)
	 */
	@Override
	@XmlTransient
	public void setEscapeHtml(boolean escapeHtml) {
		this.escapeHtml = escapeHtml;
	}
	/* (non-Javadoc)
	 * @see de.prob.worksheet.editor.IEditorUI#isEscapeHtml()
	 */
	@Override
	@JsonProperty(value = "escapeHtml")
	public boolean isEscapeHtml() {
		return escapeHtml;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DefaultEditor))
			return false;
		return this.id.equals(((DefaultEditor) obj).getId());
	}

}
