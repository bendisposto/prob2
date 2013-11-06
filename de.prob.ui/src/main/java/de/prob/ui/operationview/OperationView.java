package de.prob.ui.operationview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.ISourceProviderService;

import de.prob.statespace.AnimationSelector;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.StateSpace;
import de.prob.ui.services.ModelLoadedProvider;
import de.prob.webconsole.ServletContextListener;
import de.prob.webconsole.WebConsole;

public class OperationView extends ViewPart implements IModelChangedListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.prob.ui.operationview.OperationView";

	private final int port;
	private Browser browser;

	private AnimationSelector selector;

	public OperationView() {
		this.selector = ServletContextListener.INJECTOR
				.getInstance(AnimationSelector.class);
		selector.registerModelChangedListener(this);
		port = WebConsole.getPort();

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		browser.setUrl("http://localhost:" + port + "/sessions/Events");
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		browser.setFocus();
	}

	@Override
	public void modelChanged(StateSpace s) {
		IWorkbenchPartSite site = getSite();
		final ISourceProviderService service = (ISourceProviderService) site
				.getService(ISourceProviderService.class);
		final ModelLoadedProvider sourceProvider = (ModelLoadedProvider) service
				.getSourceProvider(ModelLoadedProvider.SERVICE);
		sourceProvider.setEnabled(s != null);

	}

}