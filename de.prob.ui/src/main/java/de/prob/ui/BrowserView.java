package de.prob.ui;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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

		try {
			FXCanvas c = new FXCanvas(parent, SWT.NONE);
			WebView webview = new WebView();
			Scene scene = new Scene(webview);
			c.setScene(scene);
			WebEngine engine = webview.getEngine();
			engine.setJavaScriptEnabled(true);
			engine.load("http://localhost:" + port + "/sessions/" + getUrl());
			this.browser = engine;
			canvas = c;
		} catch (Throwable t) {
			Browser b = new Browser(parent, SWT.NONE);
			b.setUrl("http://localhost:" + port + "/sessions/" + getUrl());
			browser = b;
			canvas = b;
		}
	}

	public void refresh() {
		if (browser instanceof Browser) {
			((Browser) browser).refresh();
		}
		if (browser instanceof WebEngine) {
			((WebEngine) browser).reload();
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