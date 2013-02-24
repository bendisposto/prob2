package de.prob.worksheet.block.impl;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;

import de.prob.worksheet.editor.impl.DefaultEditor;
import de.prob.worksheet.editor.impl.HTMLDiv;

@XmlType(name = "StateValues")
public class StoreValuesBlock extends DefaultBlock {
	@Inject
	public StoreValuesBlock() {
		setEvaluatorType("state");
		setOutput(false);
		setEditor(new HTMLDiv());
		setImmediateEvaluation(true);
		setInputAndOutput(true);
		initBlockMenu("State Values", new String[] { "Standard" });
		setToUnicode(true);
	}

	@Override
	@JsonIgnore
	public void setEditor(DefaultEditor editor) {
		super.setEditor(editor);
	}

	@Override
	public boolean equals(final Object obj) {
		// TODO Do we need an overriden default equals method ????
		return super.equals(obj);
	}

	@Override
	public String getOverrideEditorContent() {
		return "getStoreValues";
	}
}
