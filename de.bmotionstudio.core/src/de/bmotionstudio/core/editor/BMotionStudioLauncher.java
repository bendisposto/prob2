package de.bmotionstudio.core.editor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorLauncher;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.BMotionStudio;
import de.bmotionstudio.core.model.Simulation;
import de.bmotionstudio.core.model.VisualizationView;
import de.bmotionstudio.core.model.control.Visualization;
import de.bmotionstudio.core.util.PerspectiveUtil;
import de.prob.ui.PerspectiveFactory;


public class BMotionStudioLauncher implements IEditorLauncher {

	private IFile file;

	@Override
	public void open(IPath path) {
		
		file = ResourcesPlugin.getWorkspace().getRoot()
				.getFileForLocation(path);

		Simulation simulation = null;
		
		// Check if a simulation is already open
		Simulation currentSimulation = BMotionStudio.getCurrentSimulation();
		IPerspectiveDescriptor currentPerspective = BMotionStudio
				.getCurrentPerspective();

		// TODO: Check if the simulation is dirty and ask the user for
		// saving it
		if(currentSimulation != null && currentSimulation.isDirty()) {
		}

		// If a simulation is already open, close the corresponding perspective
		if (currentPerspective != null) {
			PerspectiveUtil.closePerspective(currentPerspective);
			PerspectiveUtil.deletePerspective(currentPerspective);
		}
			
		InputStream inputStream = null;

		try {

			inputStream = file.getContents();

			XStream xstream = new XStream() {
				@Override
				protected MapperWrapper wrapMapper(final MapperWrapper next) {
					return new MapperWrapper(next) {
						@Override
						public boolean shouldSerializeMember(
								@SuppressWarnings("rawtypes") final Class definedIn,
								final String fieldName) {
							if (definedIn == Object.class)
								return false;
							return super.shouldSerializeMember(definedIn,
									fieldName);
						}
					};
				}
			};

			BMotionEditorPlugin.setAliases(xstream);
			Object obj = xstream.fromXML(inputStream);
			
			// Set the correct image path. In this case the image path is a
			// subfolder called "images" in the corresponding project
			IFolder imageFolder = file.getProject().getFolder("images");
			if (!imageFolder.exists())
				imageFolder.create(true, true, new NullProgressMonitor());
			String imageFolderUrl = imageFolder.getLocationURI().toString()
					.replace("file:/", "");
			BMotionStudio.setImagePath(imageFolderUrl);
			// -------------------------------------------------------------
			
			if (obj instanceof Visualization) {

				// TODO: We need a converter for "old" visualizations
				// Visualization visualization = (Visualization) obj;
				//
				// simulation = new Simulation();
				//
				// String secId = UUID.randomUUID().toString();
				//
				// VisualizationView visualizationView = new VisualizationView(
				// "New Visualization View", secId, visualization);
				//
				// simulation.addVisualizationView(visualizationView);

			} else if (obj instanceof Simulation) {
				simulation = (Simulation) obj;
			}

			if (simulation != null) {
				BMotionStudio.setCurrentSimulation(simulation);
				BMotionStudio.setCurrentPerspective(openPerspective(file));
				initViews(simulation);
			}

		} catch (CoreException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void initViews(Simulation simulation) {

		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IWorkbenchPartSite site = activePage.getActivePart().getSite();

		for (Map.Entry<String, VisualizationView> entry : simulation
				.getVisualizationViews().entrySet()) {

			String secId = entry.getKey();
			VisualizationView visView = entry.getValue();
			IViewReference viewReference = site.getPage().findViewReference(
					VisualizationViewPart.ID, secId);
			VisualizationViewPart visualizationViewPart = null;
			// Check if view already exists
			if (viewReference != null) {
				visualizationViewPart = (VisualizationViewPart) viewReference
						.getPart(true);
			} else {
				// If not, create a new one
				try {
					visualizationViewPart = PerspectiveUtil
							.createVisualizationViewPart(secId, visView);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}

			if (visualizationViewPart != null
					&& !visualizationViewPart.isInitialized()) {
				visualizationViewPart.init(simulation, visView);
			}

		}

		// Close all unused visualization views
		for (IViewReference viewReference : site.getPage().getViewReferences()) {
			if (viewReference.getId().equals(VisualizationViewPart.ID)) {
				if (!simulation.getVisualizationViews().containsKey(
						viewReference.getSecondaryId()))
					site.getPage().hideView(viewReference);
			}
		}

	}
	
	private IPerspectiveDescriptor openPerspective(IFile projectFile) {

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		// Try to get the corresponding perspective
		IPerspectiveRegistry perspectiveRegistry = page.getWorkbenchWindow()
				.getWorkbench().getPerspectiveRegistry();
		String perspectiveId = getPerspectiveIdFromFile(projectFile);
		IPerspectiveDescriptor perspective = perspectiveRegistry
				.findPerspectiveWithId(perspectiveId);

		// Yes --> just switch to this perspective
		if (perspective != null) {
			PerspectiveUtil.switchPerspective(perspective);
		} else {
			// Check if a corresponding perspective file exists
			IFile perspectiveFile = projectFile.getProject().getFile(
					getPerspectiveFileName(projectFile));
			if (perspectiveFile.exists()) {
				PerspectiveUtil.importPerspective(perspectiveFile,
						perspectiveId);
				perspective = perspectiveRegistry
						.findPerspectiveWithId(perspectiveId);
				PerspectiveUtil.switchPerspective(perspective);
			} else {
				// No --> create a new perspective
				IPerspectiveDescriptor originalPerspectiveDescriptor = perspectiveRegistry
						.findPerspectiveWithId(PerspectiveFactory.PROB_PERSPECTIVE);
				PerspectiveUtil
						.switchPerspective(originalPerspectiveDescriptor);
				perspective = perspectiveRegistry.clonePerspective(
						perspectiveId, perspectiveId,
						originalPerspectiveDescriptor);
				// save the perspective
				page.savePerspectiveAs(perspective);
			}

		}

		return perspective;

	}
	
	private String getPerspectiveIdFromFile(IFile file) {
		return "ProB_" + file.getName().replace(".bmso", "");
	}

	private String getPerspectiveFileName(IFile projectFile) {
		return projectFile.getName().replace(".bmso", ".bmsop");
	}

}
