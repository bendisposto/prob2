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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.editor.handler.VisualizationViewDialog.DummyObject;
import de.bmotionstudio.core.model.VisualizationView;
import de.bmotionstudio.core.model.control.Visualization;
import de.bmotionstudio.core.util.BMotionUtil;
import de.prob.ui.ProBConfiguration;
import de.prob.ui.util.PerspectiveUtil;

public class AddVisualizationViewHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {

		// TODO This handler should only be enabled if a simulation or
		// perspective respectively is open!!!

		IPerspectiveDescriptor perspective = ProBConfiguration
				.getCurrentPerspective();

		try {

			if (perspective != null) {

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
								"New Visualization View", visualization, "EventB");
	
						// Save content
						IProject project = currentModelFile.getProject();
						IFile file = project.getFile(fileName + "."
								+ BMotionEditorPlugin.FILEEXT_STUDIO);
						file.create(
								BMotionUtil
										.getInitialContentsAsInputStream(visualizationView),
								false, new NullProgressMonitor());
						file.refreshLocal(IResource.DEPTH_ZERO, null);

						BMotionUtil.initVisualizationViewPart(
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
	
}
