package de.prob.ui.view;

import de.prob.ui.BrowserView;
import de.prob.webconsole.servlets.visualizations.IRefreshListener;

public class ProB2View extends BrowserView implements IRefreshListener {

	public static final String ID = "de.prob.ui.views.prob2";

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
