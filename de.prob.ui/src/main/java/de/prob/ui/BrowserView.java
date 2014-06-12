package de.prob.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;
import de.prob.webconsole.servlets.visualizations.IRefreshListener;

public abstract class BrowserView extends ViewPart implements IRefreshListener {

	private final int port;
	private Composite canvas;
	private Object browser;

	public BrowserView() {
		port = WebConsole.getPort();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent) {

		String force = System.getProperty("enforceJavaFX");

		if (force != null && "true".equals(force.toLowerCase().trim())) {
			createJavaFXBrowser(parent);
			return;
		} else
			createSWTBrowser(parent);
	}

	private void createSWTBrowser(Composite parent) {
		Browser b = new Browser(parent, SWT.NONE);
		this.browser = b;
		load(getUrl());
		canvas = b;
	}

	private void createJavaFXBrowser(Composite parent) {
		javafx.embed.swt.FXCanvas c = new javafx.embed.swt.FXCanvas(parent,
				SWT.NONE);
		javafx.scene.web.WebView webview = new javafx.scene.web.WebView();
		javafx.scene.Scene scene = new javafx.scene.Scene(webview);
		c.setScene(scene);
		javafx.scene.web.WebEngine engine = webview.getEngine();
		engine.setJavaScriptEnabled(true);
		this.browser = engine;
		load(getUrl());
		canvas = c;
	}

	public void refresh() {
		if (browser instanceof Browser) {
			((Browser) browser).refresh();
			return;
		}
		if (browser instanceof javafx.scene.web.WebEngine) {
			((javafx.scene.web.WebEngine) browser).reload();
		}
	}

	public void load(String url) {
		if (url != null) {
			if (browser instanceof Browser) {
				((Browser) browser).setUrl("http://localhost:" + port + "/"
						+ url);
				return;
			} else if (browser instanceof javafx.scene.web.WebEngine) {
				((javafx.scene.web.WebEngine) browser).load("http://localhost:"
						+ port + "/" + url);
			}
		}
	}

	protected abstract String getUrl();

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		canvas.setFocus();
	}

}