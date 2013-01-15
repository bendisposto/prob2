package de.prob.worksheet.editor;

public class HTMLErrorEditor extends DefaultEditor {

	public HTMLErrorEditor() {
		super();

		this.setCSSHREFs(new String[] {});
		this.setJavascriptHREFs(new String[] {});

		this.setHTMLContent("<div class=\"ui-editor-HTMLErrorOutput\" ></div>");
		this.setGetContentScript("function(){return $(\"#\"+this.id+\"\").editor(\"getEditorObject\").innerHTML();}");
		this.setInitializationFunction("function(){return $($(\"#\"+this.id+\" .ui-editor-HTMLErrorOutput\")[0])}");
		this.setSetContentScript("function(content){$(\"#\"+this.id+\"\").editor(\"getEditorObject\").empty();return $(\"#\"+this.id+\"\").editor(\"getEditorObject\").append(content)}");
		this.setDestroyScript("function(){}");

	}

}
