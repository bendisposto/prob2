/**
 * 
 */
package de.prob.worksheet.editor;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Rene
 * 
 */
@XmlType(name = "DefaultEditor")
public class DefaultEditor extends IWorksheetEditor {
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
	}

	@Override
	public String getSetContentScript() {
		return this.setContentScript;
	}

	@Override
	public void setSetContentScript(final String script) {
		this.setContentScript = script;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(final String id) {
		this.id = id;
	}

	@Override
	public String getHTMLContent() {
		return this.HTMLContent;
	}

	@Override
	public String getEditorContent() {
		return this.EditorContent;
	}

	@Override
	public String getInitializationFunction() {
		return this.InitFunction;
	}

	@Override
	public String[] getCSSHREFs() {
		return this.CSSHREFs.toArray(new String[this.CSSHREFs.size()]);
	}

	@Override
	public String[] getJavascriptHREFs() {
		return this.JavascriptHREFs.toArray(new String[this.JavascriptHREFs
				.size()]);
	}

	@Override
	public void setHTMLContent(final String htmlContent) {
		this.HTMLContent = htmlContent;
	}

	@Override
	public void setEditorContent(final String editorContent) {
		this.EditorContent = editorContent;
	}

	@Override
	public void setInitializationFunction(final String initFunction) {
		this.InitFunction = initFunction;
	}

	@Override
	public void setCSSHREFs(final String[] cssHref) {
		this.CSSHREFs = new ArrayList<String>(Arrays.asList(cssHref));
	}

	@Override
	public void setJavascriptHREFs(final String[] javascriptHrefs) {
		this.JavascriptHREFs = new ArrayList<String>(
				Arrays.asList(javascriptHrefs));
	}

	@Override
	public void addJavascriptHref(final String href) {
		this.JavascriptHREFs.add(href);
	}

	@Override
	public void addCSSHref(final String href) {
		this.CSSHREFs.add(href);
	}

	@Override
	public String getGetContentScript() {
		return this.getContentScript;
	}

	@Override
	public void setGetContentScript(final String script) {
		this.getContentScript = script;
	}

	@Override
	public String getDestroyScript() {
		return this.destroyScript;
	}

	@Override
	public void setDestroyScript(final String script) {
		this.destroyScript = script;
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

	@Override
	public String getSetFocusScript() {
		return this.setFocusScript;
	}

	@Override
	@JsonIgnore
	public void setSetFocusScript(String script) {
		this.setFocusScript = script;
	}
}
