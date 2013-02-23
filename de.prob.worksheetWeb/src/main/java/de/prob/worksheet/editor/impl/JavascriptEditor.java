package de.prob.worksheet.editor.impl;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "JavascriptEditor")
public class JavascriptEditor extends DefaultEditor {

	public JavascriptEditor() {
		this.setHTMLContent("<textarea class=\"ui-editor-javascript\"></textarea>");
		this.addCSSHref("javascripts/libs/codemirror-2.36/lib/codemirror.css");
		this.addCSSHref("javascripts/libs/codemirror-2.36/theme/eclipse.css");
		this.addJavascriptHref("javascripts/libs/codemirror-2.36/lib/codemirror.js");
		this.addJavascriptHref("javascripts/libs/codemirror-2.36/mode/javascript/javascript.js");

		this.setGetContentScript("function(){return $(\"#\"+this.id+\"\").editor(\"getEditorObject\").getValue();}");
		this.setInitializationFunction("function(){var cm = CodeMirror.fromTextArea($(\"#\"+this.id+\" .ui-editor-javascript\")[0],{lineNumbers: true,onChange:$.proxy($(\"#\"+this.id+\"\").data().editor._editorChanged,$(\"#\"+this.id+\"\").data().editor)}); return cm;}");
		this.setSetContentScript("function(content){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").setValue(content);}");
		this.setDestroyScript("function(){if(typeof $(\"#\"+this.id+\"\").editor(\"getEditorObject\").toTextArea=='function' ||typeof $(\"#\"+this.id+\"\").editor(\"getEditorObject\").toTextArea=='object')$(\"#\"+this.id+\"\").editor(\"getEditorObject\").toTextArea();}");
		this.setSetFocusScript("function(){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").focus();}");
	}

}
