package de.prob.worksheet.block.impl;

import javax.xml.bind.annotation.XmlType;

import de.prob.worksheet.editor.impl.JitEditor;

@XmlType(name = "TreeBlock")
public class TreeBlock extends DefaultBlock {
	public TreeBlock() {
		setEvaluatorType("state");
		setOutput(true);
		setToUnicode(false);
		setEditor(new JitEditor());
		setHasMenu(false);
	}
}
