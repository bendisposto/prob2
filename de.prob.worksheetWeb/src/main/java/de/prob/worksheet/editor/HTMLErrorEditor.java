package de.prob.worksheet.editor;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "HTMLErrorEditor")
public class HTMLErrorEditor extends DefaultEditor {

	public HTMLErrorEditor() {
		super();

		this.setCSSHREFs(new String[] {});
		this.setJavascriptHREFs(new String[] {});

		this.setHTMLContent("<div class=\"ui-editor-HTMLErrorOutput\" class=\"ui-editor-border\"> ></div>");
		this.setGetContentScript("function(){return $(\"#\"+this.id+\"\").editor(\"getEditorObject\").html();}");
		this.setInitializationFunction("function(){return $($(\"#\"+this.id+\" .ui-editor-HTMLErrorOutput\")[0])}");
		this.setSetContentScript("function(content){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").empty();return $(\"#\"+this.id+\"\").editor(\"getEditorObject\").append(content)}");
		this.setDestroyScript("function(){}");
		this.setSetFocusScript("function(){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").focus();}");

	}

}
