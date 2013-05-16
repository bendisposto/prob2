package de.bmotionstudio.core.editor.command;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.core.model.VisualizationView;

public class LockVisualizationCommand extends Command {

	private VisualizationView visualizationView;
	private boolean newLock;
	private boolean oldLock;

	public LockVisualizationCommand(VisualizationView visualizationView,
			boolean lock) {
		this.visualizationView = visualizationView;
		this.newLock = lock;
	}

	@Override
	public void execute() {
		redo();
	}

	@Override
	public void undo() {
		this.visualizationView.setLocked(oldLock);
	}

	@Override
	public void redo() {
		this.oldLock = this.visualizationView.isLocked();
		this.visualizationView.setLocked(newLock);
	}

}
