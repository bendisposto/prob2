package de.prob.worksheet.editor.impl;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "CkEditorEditor")
public class CkEditorEditor extends DefaultEditor {
	public CkEditorEditor() {
		// addJavascriptHref("javascripts/libs/ckeditor/ckeditor.js");
		setHTMLContent("<div class='editor-instance'  contenteditable='true'></div>");
		setEscapeHtml(false);
		setNewlineToHtml(false);
		setInitializationScript("function(){"
				+ "var editorObject=CKEDITOR.replace( $('#'+this.id+' .editor-instance').attr('id'));"
				+ "editorObject.on('change',$.proxy($('#'+this.id).data('editor')._editorChanged,$('#'+this.id).data('editor')));"
				+ "editorObject.on('focus', function(e){$('.'+e.editor.id).trigger('focusin');$('.'+e.editor.id+' .cke_top').show();$('.'+e.editor.id+' .cke_bottom').show();});"
				+ "editorObject.on('blur', function(e){$('.'+e.editor.id).trigger('focusout');$('.'+e.editor.id+' .cke_top').hide();$('.'+e.editor.id+' .cke_bottom').hide();});"
				+ "editorObject.on('instanceReady',function(e){$('.'+e.editor.id+' .cke_top').hide();$('.'+e.editor.id+' .cke_bottom').hide();});"
				+ "return editorObject;}");
		setGetContentScript("function(){return $('#'+this.id).editor('getEditorObject').getData();}");
		setSetContentScript("function(content){$('#'+this.id).editor('getEditorObject').setData(content);}");
		setDestroyScript("function(){$('#'+this.id).editor('getEditorObject').destroy();}");
		setSetFocusScript("function(){$('#'+this.id).editor('getEditorObject').focus();}");
	}
}
