package de.prob.worksheet.editor;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "HTMLEditor")
public class HTMLEditor extends DefaultEditor {

	public HTMLEditor() {
		super();

		this.setCSSHREFs(new String[] {});
		this.setJavascriptHREFs(new String[] {});

		this.setHTMLContent("<div class=\"ui-editor-HTMLOutput\" class=\"ui-editor-border\"></div>");
		this.setGetContentScript("function(){return $(\"#\"+this.id+\"\").editor(\"getEditorObject\").html();}");
		this.setInitializationFunction("function(){return $($(\"#\"+this.id+\" .ui-editor-HTMLOutput\")[0])}");
		this.setSetContentScript("function(content){\n$(\"#\"+this.id+\"\").editor(\"getEditorObject\").empty();\nreturn $(\"#\"+this.id+\"\").editor(\"getEditorObject\").append(content)}");
		this.setDestroyScript("function(){}");

	}

}
