package de.prob.worksheet.block;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.inject.Inject;

import de.prob.worksheet.IWorksheetMenuNode;
import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.WorksheetMenuNode;
import de.prob.worksheet.WorksheetObjectMapper;
import de.prob.worksheet.editor.HTMLEditor;
import de.prob.worksheet.editor.IWorksheetEditor;

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
	public void setEditor(IWorksheetEditor editor) {
		// TODO Auto-generated method stub
		super.setEditor(editor);
	}

	private void initBlockMenu(String type, String[] excludes) {
		final ArrayList<IWorksheetMenuNode> menu = new ArrayList<IWorksheetMenuNode>();
		Arrays.sort(excludes);
		String[] blockTypes = this.getInputBlockTypes();

		final WorksheetMenuNode typeMenu = new WorksheetMenuNode(type, "", "");
		for (final String typeName : blockTypes) {

			if (Arrays.binarySearch(excludes, type) >= 0
					|| typeName.equals(typeName))
				continue;
			final WorksheetMenuNode node = new WorksheetMenuNode(typeName, "",
					"");
			node.setClick("function(){$(this).closest('.ui-block').block('switchBlock','"
					+ typeName + "');}");
			typeMenu.addChild(node);
		}
		if (typeMenu.getChildren().length != 0) {
			menu.add(typeMenu);
			this.setMenu(menu.toArray(new IWorksheetMenuNode[menu.size()]));
		} else {
			this.setHasMenu(false);
		}
	}

	private String[] getInputBlockTypes() {
		WorksheetObjectMapper mapper = ServletContextListener.INJECTOR
				.getInstance(WorksheetObjectMapper.class);
		return mapper.getInputBlockNames();
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
