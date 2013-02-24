package de.prob.worksheet.editor.impl;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "HTMLErrorEditor")
public class HTMLDivError extends DefaultEditor {

	public HTMLDivError() {
		super();

		setCSSHREFs(new String[] {});
		setJavascriptHREFs(new String[] {});

		setHTMLContent("<div class=\"ui-editor-HTMLErrorOutput ui-editor-border ui-editor-padding\"></div>");
		setGetContentScript("function(){return $(\"#\"+this.id+\"\").editor(\"getEditorObject\").html();}");
		setInitializationScript("function(){return $($(\"#\"+this.id+\" .ui-editor-HTMLErrorOutput\")[0])}");
		setSetContentScript("function(content){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").empty();return $(\"#\"+this.id+\"\").editor(\"getEditorObject\").append(content)}");
		setDestroyScript("function(){}");
		setSetFocusScript("function(){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").focus();}");
		setNewlineToHtml(true);
		setEscapeHtml(true);
	}

}
