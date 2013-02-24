package de.prob.worksheet.block.impl;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;

import de.prob.worksheet.editor.impl.DefaultEditor;
import de.prob.worksheet.editor.impl.HTMLEditor;

@XmlType(name = "Initialize")
public class InitializeStoreBlock extends DefaultBlock {
	@Inject
	public InitializeStoreBlock() {
		setEvaluatorType("state");
		setOutput(false);
		setEditor(new HTMLEditor());
		setImmediateEvaluation(true);
		setInputAndOutput(true);
		initBlockMenu("Initialize State", new String[] { "Standard" });
		setToUnicode(true);
	}

	@Override
	@JsonIgnore
	public void setEditor(DefaultEditor editor) {
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
