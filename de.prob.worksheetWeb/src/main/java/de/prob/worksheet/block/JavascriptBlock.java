package de.prob.worksheet.block;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlType;

import com.google.inject.Inject;

import de.prob.worksheet.IWorksheetMenuNode;
import de.prob.worksheet.ServletContextListener;
import de.prob.worksheet.WorksheetMenuNode;
import de.prob.worksheet.WorksheetObjectMapper;

@XmlType(name = "JavascriptBlock")
public class JavascriptBlock extends DefaultBlock {

	@Inject
	public JavascriptBlock() {
		this.setEvaluatorType("state");
		this.initBlockMenu();
	}

	private void initBlockMenu() {
		final ArrayList<IWorksheetMenuNode> menu = new ArrayList<IWorksheetMenuNode>();

		String[] blockTypes = this.getInputBlockTypes();

		final WorksheetMenuNode typeMenu = new WorksheetMenuNode("Javascript",
				"", "");
		for (final String typeName : blockTypes) {
			if (typeName.equals("Javascript") || typeName.equals("Standard"))
				continue;
			final WorksheetMenuNode type = new WorksheetMenuNode(typeName, "",
					"");
			type.setClick("function(){$(this).closest('.ui-block').block('switchBlock','"
					+ typeName + "');}");
			typeMenu.addChild(type);
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

}
