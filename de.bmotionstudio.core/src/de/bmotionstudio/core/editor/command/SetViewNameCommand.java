package de.bmotionstudio.core.editor.command;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.core.model.VisualizationView;

public class SetViewNameCommand extends Command {

	private VisualizationView visualizationView;
	private String name;
	private String oldName;

	public SetViewNameCommand(VisualizationView visualizationView,
			String name) {
		this.visualizationView = visualizationView;
		this.name = name;
	}

	@Override
	public void execute() {
		redo();
	}

	@Override
	public void undo() {
		this.visualizationView.setName(oldName);
	}

	@Override
	public void redo() {
		this.oldName = this.visualizationView.getName();
		this.visualizationView.setName(name);
	}

}
