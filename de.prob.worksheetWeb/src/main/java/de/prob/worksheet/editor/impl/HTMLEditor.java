package de.prob.worksheet.editor.impl;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "HTMLEditor")
public class HTMLEditor extends DefaultEditor {

	public HTMLEditor() {
		super();
		setCSSHREFs(new String[] {});
		setJavascriptHREFs(new String[] {});

		setHTMLContent("<div class=\"ui-editor-HTMLOutput ui-editor-border ui-editor-padding\"></div>");
		setGetContentScript("function(){return $(\"#\"+this.id+\"\").editor(\"getEditorObject\").html();}");
		setInitializationScript("function(){return $($(\"#\"+this.id+\" .ui-editor-HTMLOutput\")[0])}");
		setSetContentScript("function(content){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").empty();\nreturn $(\"#\"+this.id+\"\").editor(\"getEditorObject\").append(content)}");
		setDestroyScript(null);
		setSetFocusScript("function(){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").focus();}");
		setNewlineToHtml(true);
		setEscapeHtml(true);
	}
}
