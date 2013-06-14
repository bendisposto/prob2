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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.bmotionstudio.core.AttributeConstants;
import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.editor.VisualizationViewPart;
import de.bmotionstudio.core.model.BMotionGuide;
import de.bmotionstudio.core.model.VisualizationView;
import de.bmotionstudio.core.model.control.BConnection;
import de.bmotionstudio.core.model.control.BControl;
import de.bmotionstudio.core.model.control.Visualization;
import de.prob.animator.command.EvaluateFormulasCommand;
import de.prob.animator.domainobjects.EvaluationResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.model.representation.AbstractModel;
import de.prob.scripting.CSPModel;
import de.prob.statespace.Trace;

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
	
	public static File createNewVisualizationViewFile(File modelFile,
			String language) {

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
				String content = BMotionUtil.getInitialContent(language);
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

			VisualizationView obj = null;
			try {
				BMotionUtil.setAliases(xstream);
				obj = (VisualizationView) xstream.fromXML(inputStream);
			} catch (ConversionException e) {

			}

			return obj;

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

	public static String getInitialContent(String language)
			throws UnsupportedEncodingException {
		Visualization visualization = new Visualization();
		// TODO Make language more generic!!!!
		VisualizationView visualizationView = new VisualizationView(
				visualization, language);
		return getInitialContent(visualizationView);
	}

	public static String getInitialContent(VisualizationView visualizationView)
			throws UnsupportedEncodingException {
		XStream xstream = new XStream();
		BMotionUtil.setAliases(xstream);
		return xstream.toXML(visualizationView);
	}

	public static File[] getVisualizationViewFiles(File modelFile,
			String language) {

		Assert.isNotNull(modelFile);

		List<File> filteredFiles = new ArrayList<File>();

		File parentFile = modelFile.getParentFile();
		if (parentFile.isDirectory()) {
			File[] listFiles = parentFile.listFiles();
			for (File f : listFiles) {
				String extension = PerspectiveUtil.getExtension(f);
				if (extension != null
						&& extension.equals(BMotionEditorPlugin.FILEEXT_STUDIO)) {
					VisualizationView visualizationView = BMotionUtil
							.getVisualizationViewFromFile(f);
					if (visualizationView != null) {
						if (visualizationView.getLanguage().equals(language))
							filteredFiles.add(f);
					}
				}
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

	public static void unregisterVisualizationViews(File modelFile,
			String language) {
		File[] visualizationViewFiles = BMotionUtil.getVisualizationViewFiles(
				modelFile, language);
		for (File f : visualizationViewFiles) {
			BMotionUtil.initVisualizationViewPart(f);
		}
	}
	
	public static void initVisualizationViews(File modelFile, String language) {
		File[] visualizationViewFiles = BMotionUtil.getVisualizationViewFiles(
				modelFile, language);
		for (File f : visualizationViewFiles) {
			BMotionUtil.initVisualizationViewPart(f);
		}
	}
	
	public static VisualizationViewPart[] getVisualizationViewParts(
			File modelFile, String language) {

		List<VisualizationViewPart> list = new ArrayList<VisualizationViewPart>();

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();

		if (window != null) {

			IWorkbenchPage activePage = window.getActivePage();

			if (activePage != null) {

				File[] visualizationViewFiles = BMotionUtil
						.getVisualizationViewFiles(modelFile, language);
				for (File f : visualizationViewFiles) {

					String secId = f.getName().replace(
							"." + PerspectiveUtil.getExtension(f), "");

					// Check if view is already open
					IViewReference viewReference = activePage
							.findViewReference(VisualizationViewPart.ID, secId);

					if (viewReference != null) {
						VisualizationViewPart visualizationViewPart = (VisualizationViewPart) viewReference
								.getPart(true);
						if (visualizationViewPart != null)
							list.add(visualizationViewPart);
					}

				}

			}

		}

		return list.toArray(new VisualizationViewPart[list.size()]);

	}

	public static String getLanguageFromModel(AbstractModel model) {
		if (model instanceof EventBModel)
			return "EventB";
		else if (model instanceof ClassicalBModel)
			return "ClassicalB";
		else if (model instanceof CSPModel)
			return "CSP";
		return null;
	}
	
	public static void setAliases(XStream xstream) {
		xstream.alias("view", VisualizationView.class);
		xstream.alias("control", BControl.class);
		xstream.alias("visualization", Visualization.class);
		xstream.alias("guide", BMotionGuide.class);
		xstream.alias("connection", BConnection.class);
	}
	
	public static String parseFormula(String expressionString, BControl control) {

		if (expressionString == null)
			return null;

		// Search for control ids
		Pattern cPattern = Pattern.compile("(\\w+)");
		Matcher cMatcher = cPattern.matcher(expressionString);

		while (cMatcher.find()) {

			String controlID = cMatcher.group(1);

			if (controlID.equals("this")) {

				expressionString = expressionString.replace(controlID, control
						.getAttributeValue(AttributeConstants.ATTRIBUTE_CUSTOM)
						.toString());

			} else {
				// TODO: Return error if no control exists
			}
		}

		return expressionString;

	}
	
	public static Map<String, EvaluationResult> getEvaluationResults(
			Trace history, List<IEvalElement> evals) {
		EvaluateFormulasCommand cmd = new EvaluateFormulasCommand(evals,
				history.getCurrentState().getId());
		history.getStateSpace().execute(cmd);
		Map<String, EvaluationResult> results = new HashMap<String, EvaluationResult>();
		List<EvaluationResult> values = cmd.getValues();
		for (EvaluationResult e : values)
			results.put(e.code, e);
		return results;
	}
	
}
