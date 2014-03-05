package de.prob.ui.visualization;

import de.prob.ui.BrowserView;
import de.prob.webconsole.servlets.visualizations.IRefreshListener;

public class VizView extends BrowserView implements IRefreshListener {

	public static final String ID = "de.prob.ui.viz.VizView";

	private String url;

	public void init(final String url) {
		this.url = url;
		load(url);
	}

	@Override
	protected String getUrl() {
		return url;
	}
	
}
