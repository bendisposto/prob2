/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor.handler;

import java.io.UnsupportedEncodingException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.inject.Injector;

import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.editor.handler.VisualizationViewDialog.DummyObject;
import de.bmotionstudio.core.model.VisualizationView;
import de.bmotionstudio.core.model.control.Visualization;
import de.bmotionstudio.core.util.BMotionUtil;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.IModelChangedListener;
import de.prob.statespace.StateSpace;
import de.prob.ui.PerspectiveFactory;
import de.prob.ui.ProBConfiguration;
import de.prob.ui.util.PerspectiveUtil;
import de.prob.webconsole.ServletContextListener;

public class AddVisualizationViewHandler extends AbstractHandler implements
		IPerspectiveListener, IModelChangedListener {

	Injector injector = ServletContextListener.INJECTOR;

	final AnimationSelector selector = injector
			.getInstance(AnimationSelector.class);

	public AddVisualizationViewHandler() {
		updateEnablement();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.addPerspectiveListener(this);
		selector.registerModelChangedListener(this);
	}

	void updateEnablement() {

		boolean isEnabled = false;
		
		History currentHistory = selector.getCurrentHistory();

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		IPerspectiveDescriptor currentPerspective = page.getPerspective();

		if (currentPerspective != null
				&& currentPerspective.getId().equals(
						PerspectiveFactory.PROB_PERSPECTIVE)
				&& currentHistory != null)
			isEnabled = true;

		setBaseEnabled(isEnabled);
		
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {

		// TODO This handler should only be enabled if a simulation or
		// perspective respectively is open!!!

		try {

			if (PerspectiveUtil.isProBPerspective()) {

				IPerspectiveDescriptor perspective = PerspectiveUtil
						.getCurrentPerspective();

				PerspectiveUtil.switchPerspective(perspective);

				VisualizationViewDialog dialog = new VisualizationViewDialog(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getShell());
				if (dialog.open() == Dialog.OK) {

					Object selection = dialog.getSelection();
					if (selection instanceof DummyObject) {

						// Create a new visualization view
						IFile currentModelFile = ProBConfiguration
								.getCurrentModelFile();
						String fileName = BMotionUtil
								.getUniqueVisualizationFileName(currentModelFile);
						Visualization visualization = new Visualization();
						// TODO Make language more generic!!!!
						VisualizationView visualizationView = new VisualizationView(
								"New Visualization View", visualization,
								"EventB");

						// Save content
						IProject project = currentModelFile.getProject();
						IFile file = project.getFile(fileName + "."
								+ BMotionEditorPlugin.FILEEXT_STUDIO);
						file.create(
								BMotionUtil
										.getInitialContentsAsInputStream(visualizationView),
								false, new NullProgressMonitor());
						file.refreshLocal(IResource.DEPTH_ZERO, null);

						BMotionUtil.createVisualizationViewPart(
								visualizationView, file, fileName);

					} else if (selection instanceof IResource) {

						// Use an existing visualization view
						IResource existingVisualization = (IResource) selection;
						IFile visualizationFile = existingVisualization
								.getProject().getFile(
										existingVisualization.getName());
						VisualizationView visualizationView = BMotionUtil
								.getVisualizationViewFromFile(visualizationFile);

						String viewId = existingVisualization
								.getName()
								.replace(
										"."
												+ existingVisualization
														.getFileExtension(),
										"");
						BMotionUtil.createVisualizationViewPart(
								visualizationView, visualizationFile, viewId);

					}

				}

			} else {
				// TODO: Throw some exception!?!
			}

		} catch (PartInitException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
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
		updateEnablement();		
	}
	
	@Override
	public void dispose() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.removePerspectiveListener(this);
		selector.unregisterModelChangedListener(this);
		super.dispose();
	}

}
