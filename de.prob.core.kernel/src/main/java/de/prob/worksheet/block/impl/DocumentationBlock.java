package de.prob.worksheet.block.impl;

import javax.xml.bind.annotation.XmlType;

import de.prob.worksheet.editor.impl.CkEditorEditor;

@XmlType(name = "DocumentationBlock")
public class DocumentationBlock extends DefaultBlock {
	public DocumentationBlock() {
		setEvaluatorType("none");
		setEditor(new CkEditorEditor());
		setHasMenu(false);
		setOutput(true);
		setNeitherInNorOutput(true);
	}
}
