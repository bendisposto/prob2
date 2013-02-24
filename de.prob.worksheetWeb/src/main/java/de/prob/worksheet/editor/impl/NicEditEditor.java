package de.prob.worksheet.editor.impl;

public class NicEditEditor extends DefaultEditor {
	public NicEditEditor() {
		addJavascriptHref("javascripts/libs/nicEdit/nicEdit.js");
		setHTMLContent("<textarea class='editor-instance'></textarea>");
		setEscapeHtml(false);
		setNewlineToHtml(false);

		setInitializationScript("function(){"
				+ "var editorObject=new nicEditor({iconsPath : 'javascripts/libs/nicEdit/nicEditorIcons.gif'});"
				+ "var instanceId=$('#'+this.id+' .editor-instance').attr('id');"
				+ "if((typeof instanceId)!='undefined')"
				+ "editorObject.panelInstance(instanceId);"
				+ "return editorObject;}");
		setGetContentScript("function(){"
				+ "var editorObject=$('#'+this.id).editor('getEditorObject');"
				+ "var instanceId=$('#'+this.id+' .editor-instance').attr('id');"
				+ "if((typeof instanceId)!='undefined'){"
				+ "return editorObject.instanceById(instanceId).getContent();"
				+ "}return '';}");
		setSetContentScript("function(content){"
				+ "var editorObject=$('#'+this.id).editor('getEditorObject');"
				+ "var instanceId=$('#'+this.id+' .editor-instance').attr('id');"
				+ "if((typeof instanceId)!='undefined'){"
				+ "editorObject.instanceById(instanceId).setContent(content);"
				+ "}return;}");
		setDestroyScript("function(){"
				+ "var instanceId=$('#'+this.id+' .editor-instance').attr('id');"
				+ "if((typeof instanceId)!='undefined')"
				+ "$('#'+this.id).editor('getEditorObject').removeInstance(instanceId)"
				+ "}");
		setSetFocusScript("function(){"
				+ "var editorObject=$('#'+this.id).editor('getEditorObject');"
				+ "var instanceId=$('#'+this.id+' .editor-instance').attr('id');"
				+ "if((typeof instanceId)!='undefined'){"
				+ "editorObject.instanceById(instanceId).selected({ctrl:true});"
				+ "}return;}");
	}
}
