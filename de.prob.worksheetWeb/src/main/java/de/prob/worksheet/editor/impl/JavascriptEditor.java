package de.prob.worksheet.editor.impl;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "JavascriptEditor")
public class JavascriptEditor extends DefaultEditor {

	public JavascriptEditor() {
		setHTMLContent("<textarea class=\"ui-editor-javascript\"></textarea>");
		addCSSHref("javascripts/libs/codemirror-2.36/lib/codemirror.css");
		addCSSHref("javascripts/libs/codemirror-2.36/theme/eclipse.css");
		addJavascriptHref("javascripts/libs/codemirror-2.36/lib/codemirror.js");
		addJavascriptHref("javascripts/libs/codemirror-2.36/mode/javascript/javascript.js");

		setGetContentScript("function(){return $(\"#\"+this.id+\"\").editor(\"getEditorObject\").getValue();}");
		setInitializationScript("function(){var cm = CodeMirror.fromTextArea($(\"#\"+this.id+\" .ui-editor-javascript\")[0],{lineNumbers: true,onChange:$.proxy($(\"#\"+this.id+\"\").data().editor._editorChanged,$(\"#\"+this.id+\"\").data().editor)}); return cm;}");
		setSetContentScript("function(content){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").setValue(content);}");
		setDestroyScript("function(){if(typeof $(\"#\"+this.id+\"\").editor(\"getEditorObject\").toTextArea=='function' ||typeof $(\"#\"+this.id+\"\").editor(\"getEditorObject\").toTextArea=='object')$(\"#\"+this.id+\"\").editor(\"getEditorObject\").toTextArea();}");
		setSetFocusScript("function(){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").focus();}");
	}

}
