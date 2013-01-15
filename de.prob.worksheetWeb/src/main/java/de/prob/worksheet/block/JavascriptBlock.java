package de.prob.worksheet.block;

import java.util.ArrayList;
import java.util.Arrays;

import de.prob.worksheet.IWorksheetMenuNode;
import de.prob.worksheet.WorksheetMenuNode;

public class JavascriptBlock extends DefaultBlock {

	public JavascriptBlock() {
		// FIXME change to back to type Javascript if test is done;
		this.setEvaluatorType("state");

		final ArrayList<IWorksheetMenuNode> menu = new ArrayList<IWorksheetMenuNode>();
		final IWorksheetMenuNode action = new WorksheetMenuNode("Action", "", "");
		final IWorksheetMenuNode evalThis = new WorksheetMenuNode("Evaluate (this)", "", "ui-icon-play");
		evalThis.setClick("function(){$(this).closest(\".ui-worksheet\").worksheet(\"evaluate\",$(this).closest(\".ui-block\").block(\"option\",\"id\"));}");
		action.addChild(evalThis);
		menu.add(action);
		// TODO getEditorTypes dynamically
		final String[] types = { "JavaScript", "Python" };
		final ArrayList<String> editorTypes = new ArrayList<String>(Arrays.asList(types));

		editorTypes.remove("JavaScript");
		final WorksheetMenuNode typeMenu = new WorksheetMenuNode("JavaScript", "", "");
		for (final String typeName : editorTypes) {
			final WorksheetMenuNode type = new WorksheetMenuNode(typeName, "", "");
			type.setClick("function(){alert('" + typeName + "');}");
			typeMenu.addChild(type);
		}
		menu.add(typeMenu);
		this.setMenu(menu.toArray(new IWorksheetMenuNode[menu.size()]));
	}

	@Override
	public boolean equals(final Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

}
