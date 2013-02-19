package de.prob.worksheet.block;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;

import de.prob.worksheet.editor.HTMLEditor;
import de.prob.worksheet.editor.IWorksheetEditor;

@XmlType(name = "Initialize")
public class InitializeStoreBlock extends DefaultBlock {
	@Inject
	public InitializeStoreBlock() {
		this.setEvaluatorType("state");
		this.setOutput(false);
		this.setEditor(new HTMLEditor());
		this.setImmediateEvaluation(true);
		this.setInputAndOutput(true);
		this.initBlockMenu("Initialize State", new String[] { "Standard" });
	}

	@Override
	@JsonIgnore
	public void setEditor(IWorksheetEditor editor) {
		// TODO Auto-generated method stub
		super.setEditor(editor);
	}

	@Override
	public boolean equals(final Object obj) {
		// TODO Do we need an overriden default equals method ????
		return super.equals(obj);
	}

	@Override
	public String getOverrideEditorContent() {
		return "getCurrentState";
	}
}
