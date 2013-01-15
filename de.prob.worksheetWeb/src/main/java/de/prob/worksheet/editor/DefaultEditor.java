/**
 * 
 */
package de.prob.worksheet.editor;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Rene
 * 
 */
public class DefaultEditor implements IWorksheetEditor {
	private String				HTMLContent;
	private String				EditorContent	= "";
	private String				InitFunction;
	private String				getContentScript;
	private String				destroyScript;
	private ArrayList<String>	CSSHREFs;
	private ArrayList<String>	JavascriptHREFs;
	private String				id;

	private String				setContentScript;

	public DefaultEditor() {
		this.CSSHREFs = new ArrayList<String>();
		this.JavascriptHREFs = new ArrayList<String>();

		this.setHTMLContent("<textarea class=\"ui-editor-javascript\"></textarea>");
		this.addCSSHref("javascripts/libs/codemirror-2.36/lib/codemirror.css");
		this.addCSSHref("javascripts/libs/codemirror-2.36/theme/eclipse.css");
		this.addJavascriptHref("javascripts/libs/codemirror-2.36/lib/codemirror.js");
		this.addJavascriptHref("javascripts/libs/codemirror-2.36/mode/javascript/javascript.js");

		this.setGetContentScript("function(){return $(\"#\"+this.id+\"\").editor(\"getEditorObject\").getValue();}");
		this.setInitializationFunction("function(){var cm = CodeMirror.fromTextArea($(\"#\"+this.id+\" .ui-editor-javascript\")[0],{lineNumbers: true,onChange:$.proxy($(\"#\"+this.id+\"\").editor().data().editor._editorChanged,$(\"#\"+this.id+\"\").editor().data().editor)}); return cm;}");
		this.setSetContentScript("function(content){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").setValue(content);}");
		this.setDestroyScript("function(){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").toTextArea();}");
	}

	@Override
	public String getSetContentScript() {
		return this.setContentScript;
	}

	@Override
	public void setSetContentScript(final String script) {
		this.setContentScript = script;
	}

	public String getId() {
		return this.id;
	}

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
		return this.JavascriptHREFs.toArray(new String[this.JavascriptHREFs.size()]);
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
		this.JavascriptHREFs = new ArrayList<String>(Arrays.asList(javascriptHrefs));
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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DefaultEditor)) return false;
		return this.id.equals(((DefaultEditor) obj).getId());
	}
}
