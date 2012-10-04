package de.prob.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class GroovyConsole extends ViewPart {

	public GroovyConsole() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {

		final Browser b = new Browser(parent, SWT.NONE);
		b.setUrl("http://localhost:8080/console.jsp");

		// IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench()
		// .getBrowserSupport();
		// browserSupport.createBrowser(IWorkbenchBrowserSupport.AS_VIEW,
		// "prob", "prob2", "prob3").openURL(
		// new URL("http://localhost:"+WebConsole.getPort()));
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
