package de.prob.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.part.ViewPart;

import de.prob.webconsole.WebConsole;

public class GroovyConsole extends ViewPart {

	public GroovyConsole() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite shell) {
		
		shell.setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));
		SashForm sashForm = new SashForm(shell, SWT.VERTICAL | SWT.NO_SCROLL);
		Composite composite = new Composite(sashForm, SWT.NO_SCROLL);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));
		Browser consoleBrowser = new Browser(composite, SWT.NO_SCROLL);
		consoleBrowser.setUrl("http://localhost:"+WebConsole.getPort()+"/console.jsp");
		Composite composite_1 = new Composite(sashForm, SWT.NO_SCROLL);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL| SWT.VERTICAL));
		Browser outputBrowser = new Browser(composite_1, SWT.NO_SCROLL);
		outputBrowser.setUrl("http://localhost:"+WebConsole.getPort()+"/sysout.jsp");
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
