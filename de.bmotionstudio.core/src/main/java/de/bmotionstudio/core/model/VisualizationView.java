package de.bmotionstudio.core.model;

import org.eclipse.draw2d.PositionConstants;

import de.bmotionstudio.core.model.control.Visualization;

public class VisualizationView extends PropertyChangeSupportObject {

	private String name;

	private Visualization visualization;

	private boolean rulerVisible, snapToGeometry, gridEnabled;
	
	protected BMotionRuler leftRuler, topRuler;

	private String language;

	public VisualizationView(String name, Visualization visualization,
			String language) {
		this.name = name;
		this.visualization = visualization;
		this.language = language;
		this.rulerVisible = true;
		this.snapToGeometry = true;
		this.gridEnabled = false;
		createRulers();
	}

	public VisualizationView(Visualization visualization, String language) {
		this("New Visualization View", visualization, language);
	}

	protected Object readResolve() {
		// Populate parent
		visualization.setVisualizationView(this);
		return this;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldVal = this.name;
		this.name = name;
		firePropertyChange("name", oldVal, name);
	}

	public Visualization getVisualization() {
		return visualization;
	}

	public void setVisualization(Visualization visualization) {
		Visualization oldVal = this.visualization;
		this.visualization = visualization;
		visualization.setVisualizationView(this);
		firePropertyChange("visualization", oldVal, visualization);
	}

	public void setRulerVisible(boolean rulerVisible) {
		boolean oldVal = this.rulerVisible;
		this.rulerVisible = rulerVisible;
		firePropertyChange("rulerVisible", oldVal, rulerVisible);
	}

	public boolean isRulerVisible() {
		return rulerVisible;
	}
	
	public void setGridEnabled(boolean gridEnabled) {
		boolean oldVal = this.gridEnabled;
		this.gridEnabled = gridEnabled;
		firePropertyChange("gridEnabled", oldVal, gridEnabled);
	}
	
	public boolean isGridEnabled() {
		return gridEnabled;
	}

	public void setSnapToGeometry(boolean snapToGeometry) {
		boolean oldVal = this.snapToGeometry;
		this.snapToGeometry = snapToGeometry;
		firePropertyChange("snapToGeometry", oldVal, snapToGeometry);
	}
	
	public boolean isSnapToGeometryEnabled() {
		return snapToGeometry;
	}
	
	public BMotionRuler getRuler(int orientation) {
		BMotionRuler result = null;
		switch (orientation) {
		case PositionConstants.NORTH:
			result = topRuler;
			break;
		case PositionConstants.WEST:
			result = leftRuler;
			break;
		}
		return result;
	}

	public BMotionRuler getTopRuler() {
		return topRuler;
	}

	public void setTopRuler(BMotionRuler topRuler) {
		BMotionRuler oldVal = this.topRuler;
		this.topRuler = topRuler;
		firePropertyChange("topRuler", oldVal, topRuler);
	}

	public BMotionRuler getLeftRuler() {
		return leftRuler;
	}

	public void setLeftRuler(BMotionRuler leftRuler) {
		BMotionRuler oldVal = this.leftRuler;
		this.leftRuler = leftRuler;
		firePropertyChange("leftRuler", oldVal, leftRuler);
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		String oldVal = this.language;
		this.language = language;
		firePropertyChange("language", oldVal, language);
	}
	
	protected void createRulers() {
		if (leftRuler == null)
			leftRuler = new BMotionRuler(false);
		if (topRuler == null)
			topRuler = new BMotionRuler(true);
	}
	
}
