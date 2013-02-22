package de.bmotionstudio.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.BMotionStudio;
import de.bmotionstudio.core.editor.VisualizationViewPart;
import de.bmotionstudio.core.model.VisualizationView;
import de.bmotionstudio.core.model.control.Visualization;

public class BMotionUtil {

	public static int openSaveDialog() {
		MessageDialog dg = new MessageDialog(
				Display.getDefault().getActiveShell(),
				"You made changes to your visualization.",
				null,
				"Your visualization has beed modified. Save changes? Please note: The current visualization will be closed!",
				MessageDialog.QUESTION_WITH_CANCEL, new String[] {
						IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL,
						IDialogConstants.CANCEL_LABEL }, 0);
		return dg.open();
	}
	
	public static void closeCurrentVisualization() {
		
//		// Check if a perspective is already open
//		IPerspectiveDescriptor currentPerspective = BMotionStudio
//				.getCurrentPerspective();
//
//		// If a simulation is already open, close the corresponding perspective
//		// before opening the new one
//		if (currentPerspective != null) {
//			PerspectiveUtil.closePerspective(currentPerspective);
//			PerspectiveUtil.deletePerspective(currentPerspective);
//		}
//		
//		BMotionStudio.reset();
		
	}
	
	public static boolean openVisualization(IFile visualizationFile,
			String language) {

		// If the visualization file does not exist, stop ...
		if (visualizationFile == null || !visualizationFile.exists())
			return false;

		InputStream inputStream = null;

		try {

			inputStream = visualizationFile.getContents();

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
			IFolder imageFolder = visualizationFile.getProject().getFolder(
					"images");
			if (!imageFolder.exists())
				imageFolder.create(true, true, new NullProgressMonitor());
			String imageFolderUrl = imageFolder.getLocationURI().toString()
					.replace("file:/", "");
			BMotionStudio.setImagePath(imageFolderUrl);
			// -------------------------------------------------------------

			// TODO reimplement me!!!
//			Simulation simulation = null;

//			if (obj instanceof Visualization) {

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

//			} else if (obj instanceof Simulation) {
//				simulation = (Simulation) obj;
//			}

//			if (simulation != null) {
//				simulation.setLanguage(language);
//				BMotionStudio.setCurrentSimulation(simulation);
//				BMotionStudio.setCurrentPerspective(PerspectiveUtil
//						.openPerspective(visualizationFile));
//				BMotionStudio.setCurrentProjectFile(visualizationFile);
//				PerspectiveUtil.initViews(simulation);
//			}
			
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

		return true;

	}
	
	public static VisualizationViewPart initVisualizationViewPart(
			VisualizationView visualizationView, IFile visualizationFile,
			String secId) {

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();

		// Check if view is already open
		IViewReference viewReference = activePage.findViewReference(
				VisualizationViewPart.ID, secId);

		VisualizationViewPart visualizationViewPart = null;

		if (viewReference != null) {
			visualizationViewPart = (VisualizationViewPart) viewReference
					.getPart(true);
		}

		if (visualizationViewPart != null
				&& !visualizationViewPart.isInitialized())
			visualizationViewPart.init(visualizationView, visualizationFile);

		return visualizationViewPart;

	}
	
	public static VisualizationViewPart createVisualizationViewPart(
			VisualizationView visualizationView, IFile visualizationFile,
			String secId) throws PartInitException {

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();

		VisualizationViewPart visualizationViewPart = (VisualizationViewPart) activePage
				.showView(VisualizationViewPart.ID, secId,
						IWorkbenchPage.VIEW_VISIBLE);

		if (visualizationViewPart != null
				&& !visualizationViewPart.isInitialized())
			visualizationViewPart.init(visualizationView, visualizationFile);

		return visualizationViewPart;

	}

	public static VisualizationView getVisualizationViewFromFile(
			IFile visualizationFile) {

		InputStream inputStream = null;

		try {

			inputStream = visualizationFile.getContents();

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

			return (VisualizationView) obj;

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

		return null;

	}

	public static InputStream getInitialContentsAsInputStream()
			throws UnsupportedEncodingException {
		Visualization visualization = new Visualization();
		// TODO Make language more generic!!!!
		VisualizationView visualizationView = new VisualizationView(
				visualization, "EventB");
		return getInitialContentsAsInputStream(visualizationView);
	}

	public static InputStream getInitialContentsAsInputStream(
			VisualizationView visualizationView)
			throws UnsupportedEncodingException {
		XStream xstream = new XStream();
		BMotionEditorPlugin.setAliases(xstream);
		String content = xstream.toXML(visualizationView);
		return new ByteArrayInputStream(content.getBytes("UTF-8"));
	}
	
	public static IResource[] getVisualizationViewFiles(IProject project) {

		if (project == null)
			return null;

		List<IResource> filteredFiles = new ArrayList<IResource>();

		try {
			for (IResource r : project.members()) {
				if (r.getType() == IResource.FILE
						&& (r.getFileExtension().equals("bmso"))) {
					filteredFiles.add(r);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		IResource[] viewFiles = filteredFiles
				.toArray(new IResource[filteredFiles.size()]);
		return viewFiles;

	}
	
	private static String getUniqueVisualizationFileName(String fileName,
			IProject project, int counter) {
		String newFileName = fileName + counter;
		if (project.getFile(newFileName + ".bmso").exists()) {
			counter++;
			return getUniqueVisualizationFileName(fileName, project, counter);
		} else {
			return newFileName;
		}
	}

	public static String getUniqueVisualizationFileName(IFile modelFile) {
		String fileName = modelFile.getName().replace(
				"." + modelFile.getFileExtension(), "");
		if (modelFile.getProject().getFile(fileName + ".bmso").exists())
			return getUniqueVisualizationFileName(fileName,
					modelFile.getProject(), 1);
		return fileName;
	}
	
}
