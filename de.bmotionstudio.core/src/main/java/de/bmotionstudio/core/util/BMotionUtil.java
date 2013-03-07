package de.bmotionstudio.core.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
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

	public static boolean openVisualization(File visualizationFile) {

		// If the visualization file does not exist, stop ...
		if (visualizationFile == null || !visualizationFile.exists())
			return false;

		try {
			BMotionUtil.createVisualizationViewPart(visualizationFile);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		// Set the correct image path. In this case the image path is a
		// subfolder called "images" in the corresponding project
		// IFolder imageFolder = visualizationFile.getProject().getFolder(
		// "images");
		// if (!imageFolder.exists())
		// imageFolder.create(true, true, new NullProgressMonitor());
		// String imageFolderUrl = imageFolder.getLocationURI().toString()
		// .replace("file:/", "");
		// BMotionStudio.setImagePath(imageFolderUrl);
		// -------------------------------------------------------------

		// TODO reimplement me!!!
		// Simulation simulation = null;

		// if (obj instanceof Visualization) {

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

		// } else if (obj instanceof Simulation) {
		// simulation = (Simulation) obj;
		// }

		// if (simulation != null) {
		// simulation.setLanguage(language);
		// BMotionStudio.setCurrentSimulation(simulation);
		// BMotionStudio.setCurrentPerspective(PerspectiveUtil
		// .openPerspective(visualizationFile));
		// BMotionStudio.setCurrentProjectFile(visualizationFile);
		// PerspectiveUtil.initViews(simulation);
		// }

		return true;

	}
	
	public static VisualizationViewPart initVisualizationViewPart(
			File visualizationFile) {

		VisualizationViewPart visualizationViewPart = null;

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();

		if (window != null) {

			IWorkbenchPage activePage = window.getActivePage();

			if (activePage != null) {

				String secId = visualizationFile.getName().replace(
						"." + PerspectiveUtil.getExtension(visualizationFile),
						"");

				// Check if view is already open
				IViewReference viewReference = activePage.findViewReference(
						VisualizationViewPart.ID, secId);

				if (viewReference != null) {
					visualizationViewPart = (VisualizationViewPart) viewReference
							.getPart(true);
				}

				if (visualizationViewPart != null
						&& !visualizationViewPart.isInitialized())
					visualizationViewPart.init(visualizationFile);

			}

		}

		return visualizationViewPart;

	}
	
	public static File createNewVisualizationViewFile(File modelFile) {

		Assert.isNotNull(modelFile);

		File visualizationFile = null;
		String fileName = BMotionUtil.getUniqueVisualizationFileName(modelFile);

		File parentFile = modelFile.getParentFile();
		if (parentFile.isDirectory()) {

			visualizationFile = new File(parentFile.getPath() + "/" + fileName
					+ "." + BMotionEditorPlugin.FILEEXT_STUDIO);
			IWorkspace workspace = ResourcesPlugin.getWorkspace();

			FileWriter output = null;
			BufferedWriter writer = null;
			try {
				String content = BMotionUtil.getInitialContent();
				output = new FileWriter(visualizationFile);
				writer = new BufferedWriter(output);
				writer.write(content);
				IPath location = Path.fromOSString(visualizationFile
						.getAbsolutePath());
				IFile ifile = workspace.getRoot().getFileForLocation(location);
				if (ifile != null)
					ifile.refreshLocal(IResource.DEPTH_ZERO,
							new NullProgressMonitor());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			} finally {
				try {
					if (writer != null)
						writer.close();
					if (output != null)
						output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return visualizationFile;

	}
	
	public static VisualizationViewPart createVisualizationViewPart(
			File visualizationFile) throws PartInitException {

		String secId = visualizationFile.getName().replace(
				"." + PerspectiveUtil.getExtension(visualizationFile), "");

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();

		VisualizationViewPart visualizationViewPart = (VisualizationViewPart) activePage
				.showView(VisualizationViewPart.ID, secId,
						IWorkbenchPage.VIEW_VISIBLE);

		if (visualizationViewPart != null
				&& !visualizationViewPart.isInitialized())
			visualizationViewPart.init(visualizationFile);

		return visualizationViewPart;

	}

	public static VisualizationView getVisualizationViewFromFile(
			File visualizationFile) {

		InputStream inputStream = null;

		try {

			inputStream = new FileInputStream(visualizationFile);

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

		} catch (FileNotFoundException e) {
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

	public static String getInitialContent()
			throws UnsupportedEncodingException {
		Visualization visualization = new Visualization();
		// TODO Make language more generic!!!!
		VisualizationView visualizationView = new VisualizationView(
				visualization, "EventB");
		return getInitialContent(visualizationView);
	}

	public static String getInitialContent(VisualizationView visualizationView)
			throws UnsupportedEncodingException {
		XStream xstream = new XStream();
		BMotionEditorPlugin.setAliases(xstream);
		return xstream.toXML(visualizationView);
	}

	public static File[] getVisualizationViewFiles(File modelFile) {

		Assert.isNotNull(modelFile);

		List<File> filteredFiles = new ArrayList<File>();

		File parentFile = modelFile.getParentFile();
		if (parentFile.isDirectory()) {
			File[] listFiles = parentFile.listFiles();
			for (File f : listFiles) {
				String extension = PerspectiveUtil.getExtension(f);
				if (extension != null
						&& extension.equals(BMotionEditorPlugin.FILEEXT_STUDIO))
					filteredFiles.add(f);
			}
		}
		File[] viewFiles = filteredFiles
				.toArray(new File[filteredFiles.size()]);
		return viewFiles;

	}
	
	private static String getUniqueVisualizationFileName(String fileName,
			File modelFile, int counter) {
		String newFileName = fileName + counter;
		File visFile = new File(modelFile.getParentFile().getPath() + "/"
				+ newFileName + ".bmso");
		if (visFile.exists()) {
			counter++;
			return getUniqueVisualizationFileName(fileName, modelFile, counter);
		} else {
			return newFileName;
		}
	}

	public static String getUniqueVisualizationFileName(File modelFile) {
		String fileName = modelFile.getName().replace(
				"." + PerspectiveUtil.getExtension(modelFile), "");
		File visFile = new File(modelFile.getParentFile().getPath() + "/"
				+ fileName + ".bmso");
		if (visFile.exists())
			return getUniqueVisualizationFileName(fileName, modelFile, 1);
		return fileName;
	}

	public static void initVisualizationViews(File modelFile) {
		File[] visualizationViewFiles = BMotionUtil
				.getVisualizationViewFiles(modelFile);
		for (File f : visualizationViewFiles) {
			BMotionUtil.initVisualizationViewPart(f);
		}
	}
	
}
