package de.prob.worksheet.editor.impl;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "JavascriptEditor")
public class CodeMirrorTextEditor extends DefaultEditor {

	public CodeMirrorTextEditor() {
		setHTMLContent("<textarea class=\"ui-editor-text\"></textarea>");
		addCSSHref("javascripts/libs/codemirror-2.36/lib/codemirror.css");
		addCSSHref("javascripts/libs/codemirror-2.36/theme/eclipse.css");
		addJavascriptHref("javascripts/libs/codemirror-2.36/lib/codemirror.js");

		setGetContentScript("function(){return $(\"#\"+this.id+\"\").editor(\"getEditorObject\").getValue();}");
		setInitializationScript("function(){var cm = CodeMirror.fromTextArea($(\"#\"+this.id+\" .ui-editor-text\")[0],{mode:'null',lineNumbers: false,onChange:$.proxy($(\"#\"+this.id+\"\").data().editor._editorChanged,$(\"#\"+this.id+\"\").data().editor)}); return cm;}");
		setSetContentScript("function(content){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").setValue(content);}");
		setDestroyScript("function(){if(typeof $(\"#\"+this.id+\"\").editor(\"getEditorObject\").toTextArea=='function' ||typeof $(\"#\"+this.id+\"\").editor(\"getEditorObject\").toTextArea=='object')$(\"#\"+this.id+\"\").editor(\"getEditorObject\").toTextArea();}");
		setSetFocusScript("function(){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").focus();}");
	}

}
