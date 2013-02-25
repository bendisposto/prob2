package de.prob.worksheet.block.impl;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;

import de.prob.worksheet.editor.impl.DefaultEditor;
import de.prob.worksheet.editor.impl.HTMLDiv;

@XmlType(name = "getStateFromAnimation")
public class InitializeStoreBlock extends DefaultBlock {
	public static final String PRINT_NAME = "Get state from animation";

	@Inject
	public InitializeStoreBlock() {
		setEvaluatorType("state");
		setOutput(false);
		setEditor(new HTMLDiv());
		setImmediateEvaluation(true);
		setInputAndOutput(true);
		initBlockMenu(InitializeStoreBlock.PRINT_NAME,
				new String[] { "Standard" });
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
