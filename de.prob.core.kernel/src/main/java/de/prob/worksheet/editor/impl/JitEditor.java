package de.prob.worksheet.editor.impl;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "JitEditor")
public class JitEditor extends DefaultEditor {
	public JitEditor() {
		setHTMLContent("<div class='editor-instance tree'></div>");
		setEscapeHtml(false);
		setNewlineToHtml(false);
		addJavascriptHref("javascripts/libs/Jit/jit.js");
		addJavascriptHref("javascripts/libs/Jit/editor.js");
		setInitializationScript("function(){return init($('#'+this.id).find('.editor-instance').attr('id'));}");
		/*
		 * + "var id=$('#'+this.id).find('.editor-instance').attr('id');" +
		 * "var editorObject= new $jit.ST({'injectInto': id,orientation: 'top',constrained:false,duration:0, Node:{overridable:true,autoHeight:true,autoWidth:true}, onCreateLabel: function(label, node){"
		 * + "var style = label.style;" + "label.id = node.id;" +
		 * "label.innerHTML = node.name;" + "style.color = '#333';" +
		 * "style.textAlign = 'center';}});" + "return editorObject;}");
		 */
		setSetContentScript("function(content){content=$.parseJSON(content);var obj=$('#'+this.id).editor('getEditorObject');data($('#'+this.id),obj,content);}");
		setGetContentScript("function(){return $('#'+this.id).editor('getEditorObject').toJSON();}");
		setSetFocusScript("function(){}");
		setDestroyScript("function(){}");
	}
}