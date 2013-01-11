package de.prob.ui.ticket;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class SubmitBugreportHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);

		// ISourceProviderService service = (ISourceProviderService) window
		// .getWorkbench().getService(ISourceProviderService.class);
		// ModelLoadedProvider sourceProvider = (ModelLoadedProvider) service
		// .getSourceProvider(ModelLoadedProvider.SERVICE);

		BugReportWizard wizard = new BugReportWizard();
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();
		return null;

		// IWebBrowser browser;
		// try {
		// browser = PlatformUI.getWorkbench().getBrowserSupport()
		// .createBrowser("jira");
		// browser.openURL(new URL("http://jira.cobra.cs.uni-duesseldorf.de/"));
		// } catch (PartInitException e) {
		// e.printStackTrace();
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// }
		// return null;
	}

}
