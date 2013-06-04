/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.handler;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.inject.Injector;

import de.bmotionstudio.core.editor.handler.VisualizationViewDialog.DummyObject;
import de.bmotionstudio.core.util.BMotionUtil;
import de.bmotionstudio.core.util.PerspectiveUtil;
import de.prob.model.representation.AbstractModel;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.StateSpace;
import de.prob.webconsole.ServletContextListener;

public class AddVisualizationViewHandler extends AbstractHandler implements
		IPerspectiveListener, IModelChangedListener {

	Injector injector = ServletContextListener.INJECTOR;

	final AnimationSelector selector = injector
			.getInstance(AnimationSelector.class);

	private AbstractModel currentModel;
	
	public AddVisualizationViewHandler() {
		updateEnablement();
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null) {
			activeWorkbenchWindow.addPerspectiveListener(this);
			selector.registerModelChangedListener(this);
		}
	}

	void updateEnablement() {
		boolean isEnabled = false;
		Trace currentHistory = selector.getCurrentTrace();
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null) {
			IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
			if (page != null) {
				IPerspectiveDescriptor currentPerspective = page
						.getPerspective();
				if (currentPerspective != null
						&& currentPerspective.getId().startsWith("ProB_")
						&& currentHistory != null)
					isEnabled = true;
			}
		}
		setBaseEnabled(isEnabled);
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {

		try {

			if (PerspectiveUtil.isProBPerspective()) {

				IPerspectiveDescriptor perspective = PerspectiveUtil
						.getCurrentPerspective();

				PerspectiveUtil.switchPerspective(perspective);
				
				VisualizationViewDialog dialog = new VisualizationViewDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getShell(), this.currentModel);
				if (dialog.open() == Dialog.OK) {

					Object selection = dialog.getSelection();
					if (selection instanceof DummyObject
							&& this.currentModel != null) {
						
						DummyObject obj = (DummyObject) selection;
						// Create a new visualization view
						File visualizationViewFile = BMotionUtil
								.createNewVisualizationViewFile(
										this.currentModel.getModelFile(),
										obj.getLanguage());
						BMotionUtil
								.createVisualizationViewPart(visualizationViewFile);
					} else if (selection instanceof File) {
						// Use an existing visualization view
						File existingVisualization = (File) selection;
						BMotionUtil
								.createVisualizationViewPart(existingVisualization);
					}

				}

			} else {
				// TODO: Throw some exception!?!
			}

		} catch (PartInitException e1) {
			e1.printStackTrace();
		}

		return null;

	}

	@Override
	public void perspectiveActivated(IWorkbenchPage page,
			IPerspectiveDescriptor perspective) {
		updateEnablement();	
	}

	@Override
	public void perspectiveChanged(IWorkbenchPage page,
			IPerspectiveDescriptor perspective, String changeId) {
		updateEnablement();
	}

	@Override
	public void modelChanged(StateSpace s) {
		this.currentModel = s.getModel();
		updateEnablement();
	}
	
	@Override
	public void dispose() {
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null) {
			activeWorkbenchWindow.removePerspectiveListener(this);
		}
		super.dispose();
	}

}
