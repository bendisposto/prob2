package de.prob.worksheet.block.impl;

import de.prob.worksheet.editor.impl.JitEditor;

public class TreeBlock extends DefaultBlock {
	public TreeBlock() {
		setEvaluatorType("state");
		setOutput(true);
		setToUnicode(false);
		setEditor(new JitEditor());
		setHasMenu(false);
	}
}
