package de.prob.worksheet.block.impl;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;

import de.prob.worksheet.editor.IEditorData;
import de.prob.worksheet.editor.impl.HTMLEditor;

@XmlType(name = "StateValues")
public class StoreValuesBlock extends DefaultBlock {
	@Inject
	public StoreValuesBlock() {
		this.setEvaluatorType("state");
		this.setOutput(false);
		this.setEditor(new HTMLEditor());
		this.setImmediateEvaluation(true);
		this.setInputAndOutput(true);
		this.initBlockMenu("State Values", new String[] { "Standard" });
	}

	@Override
	@JsonIgnore
	public void setEditor(IEditorData editor) {
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
		return "getStoreValues";
	}
}
