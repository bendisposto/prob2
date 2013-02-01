package de.bmotionstudio.core.model;

import java.util.HashMap;
import java.util.Map;

public class Simulation extends PropertyChangeSupportObject {

	private Map<String, VisualizationView> views;

	private transient boolean dirty, running;

	private String model, language;

	public Simulation(String model, String language) {
		this.model = model;
		this.language = language;
		this.views = new HashMap<String, VisualizationView>();
	}

	public Map<String, VisualizationView> getVisualizationViews() {
		return views;
	}

	public boolean isDirty() {
		return dirty;
	}

	public boolean isRunning() {
		return running;
	}

	public void setDirty(boolean dirty) {
		boolean oldVal = this.dirty;
		this.dirty = dirty;
		firePropertyChange("dirty", oldVal, dirty);
	}

	public void setRunning(boolean running) {
		boolean oldVal = this.running;
		this.running = running;
		firePropertyChange("running", oldVal, running);
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void addVisualizationView(VisualizationView visualizationView) {
		getVisualizationViews().put(visualizationView.getViewId(),
				visualizationView);
	}

	// TODO: Reimplement me!!!
	// public void start() {
	//
	// Animator animator = Animator.getAnimator();
	// animation = new Animation(animator, this);
	// IEventBRoot modelRoot = getCorrespondingFile(getProjectFile(), model);
	// try {
	// LoadEventBModelCommand.load(animator, modelRoot);
	// setRunning(true);
	// } catch (ProBException e) {
	// e.printStackTrace();
	// }
	//
	// }

	// public void stop() {
	// if (animation != null)
	// animation.unregister();
	// setRunning(false);
	// }

	// private IEventBRoot getCorrespondingFile(IFile file, String
	// machineFileName) {
	// IRodinProject rProject = RodinCore.valueOf(file.getProject());
	// IRodinFile rFile = rProject.getRodinFile(machineFileName);
	// IEventBRoot eventbRoot = (IEventBRoot) rFile.getRoot();
	// return eventbRoot;
	// }

}
