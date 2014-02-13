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

public abstract class BrowserView extends ViewPart {

	private final int port;
	private Browser browser;
	private FXCanvas canvas;

	public BrowserView() {
		port = WebConsole.getPort();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent) {

		canvas = new FXCanvas(parent, SWT.NONE);

		WebView browser = new WebView();
		Scene scene = new Scene(browser);
		canvas.setScene(scene);
		WebEngine engine = browser.getEngine();
		engine.setJavaScriptEnabled(true);
		engine.load("http://localhost:" + port + "/sessions/" + getUrl());
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