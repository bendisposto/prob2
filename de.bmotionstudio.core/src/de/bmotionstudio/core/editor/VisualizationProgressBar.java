/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.core.editor;

import groovy.lang.Binding;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.xml.sax.SAXException;

import com.google.inject.Injector;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.bmotionstudio.core.BMotionEditorPlugin;
import de.bmotionstudio.core.BMotionStudio;
import de.bmotionstudio.core.model.Simulation;
import de.bmotionstudio.core.util.PerspectiveUtil;
import de.prob.animator.command.LoadEventBCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.model.eventb.EventBModel;
import de.prob.rodin.translate.EventBTranslator;
import de.prob.scripting.EventBFactory;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.StateSpace;
import de.prob.ui.eventb.internal.TranslatorFactory;
import de.prob.webconsole.GroovyExecution;
import de.prob.webconsole.ServletContextListener;

public class VisualizationProgressBar extends ProgressBarDialog {

	private IFile file;
	private int confirm = -1;
	private SelectOperationDialog dialog;
	private Simulation simulation;

	public VisualizationProgressBar(Shell parent, IFile file) {
		super(parent);
		this.file = file;
	}

	@Override
	public void initGuage() {
		this.setExecuteTime(6);
		this.setMayCancel(true);
		this.setProcessMessage("Starting Visualization ...");
		this.setShellTitle("Starting Visualization");
	}

	@Override
	protected String process(int i) {

		switch (i) {
		case 1:
			try {
				simulation = createSimulation();
			} catch (CoreException e) {
				openErrorDialog(e.getMessage());
				setClose(true);
			} catch (IOException e) {
				openErrorDialog(e.getMessage());
				setClose(true);
			} catch (ParserConfigurationException e) {
				openErrorDialog(e.getMessage());
				setClose(true);
			} catch (SAXException e) {
				openErrorDialog(e.getMessage());
				setClose(true);
			}
			return "Starting ProB Animator";
		case 2:
			startProbAnimator();
			return "Setup Constants";
		case 3:
//			try {
				// TODO Reimplement me!!!
//				setupOperation("SETUP_CONTEXT");
//			} catch (InterruptedException e) {
//				openErrorDialog(e.getMessage());
//				setClose(true);
//			}
			return "Create Visualization";
		case 4:
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					openPerspective();
				}
			});
			return "Initialize machine";
		case 5:
//			try {
				// TODO Reimplement me!!!				
//				setupOperation("INITIALISATION");
//			} catch (InterruptedException e) {
//				openErrorDialog(e.getMessage());
//				setClose(true);
//			}
			return "Starting Visualization";
		}

		return "Starting BMotion Studio Visualization";

	}

	private Simulation createSimulation() throws CoreException, IOException,
			ParserConfigurationException, SAXException {
		
		Simulation simulation= null;
		
		XStream xstream = new XStream(new DomDriver()) {
			@Override
			protected MapperWrapper wrapMapper(MapperWrapper next) {
				return new MapperWrapper(next) {
					@Override
					public boolean shouldSerializeMember(
							@SuppressWarnings("rawtypes") Class definedIn,
							String fieldName) {
						if (definedIn == Object.class) {
							return false;
						}
						return super
								.shouldSerializeMember(definedIn, fieldName);
					}
				};
			}
		};
		BMotionEditorPlugin.setAliases(xstream);
		InputStream inputStream = file.getContents();
		Object obj = xstream.fromXML(inputStream);
		inputStream.close();
		if(obj instanceof Simulation)
			simulation = (Simulation) obj;
		
		// Set the correct image path. In this case the image path is a
		// subfolder called "images" in the corresponding project
		IFolder imageFolder = file.getProject().getFolder("images");
		if (!imageFolder.exists())
			imageFolder.create(true, true, new NullProgressMonitor());
		String imageFolderUrl = imageFolder.getLocationURI().toString()
				.replace("file:/", "");
		BMotionStudio.setImagePath(imageFolderUrl);
		// -------------------------------------------------------------
		
		return simulation;
		
	}

	@Override
	protected void cleanUp() {
	}

	private void startProbAnimator() {
	
		IEventBRoot rootElement = null;

		String modelFile = simulation.getModel();
		
		IRodinFile rodinFile = RodinCore.valueOf(file.getProject().getFile(
				modelFile));

		if (rodinFile != null) {
			rootElement = (IEventBRoot) rodinFile.getRoot();
		} else {
			// TODO return some error message!?
			return;
		}

		EventBTranslator eventBTranslator = new EventBTranslator(rootElement);

		Injector injector = ServletContextListener.INJECTOR;

		final EventBFactory instance = injector
				.getInstance(EventBFactory.class);

		EventBModel model = instance.load(eventBTranslator.getMainComponent(),
				eventBTranslator.getMachines(), eventBTranslator.getContexts());

		StringWriter writer = new StringWriter();
		PrintWriter pto = new PrintWriter(writer);
		try {
			TranslatorFactory.translate(rootElement, pto);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		StateSpace s = model.getStatespace();

		Pattern p2 = Pattern.compile("^package\\((.*?)\\)\\.");
		Matcher m2 = p2.matcher(writer.toString());
		m2.find();
		String cmd = m2.group(1);

		s.execute(new LoadEventBCommand(cmd));
		s.execute(new StartAnimationCommand());

		History h = new History(s);
		AnimationSelector selector = injector
				.getInstance(AnimationSelector.class);
		selector.addNewHistory(h);
		final GroovyExecution ge = injector.getInstance(GroovyExecution.class);
		Binding bindings = ge.getBindings();
		try {
			bindings.setVariable(ge.freshVar("space_"), s);
			s.registerStateSpaceListener(ge);
		} catch (Error t) {
			t.printStackTrace();
		} finally {
			ge.notifyListerners();
		}

		System.gc();
		
	}

	// TODO Reimplement me!!!
//	private void setupOperation(String opName) throws InterruptedException {
//
//		final List<Operation> ops = animation.getState().getEnabledOperations();
//
//		if (ops.size() > 1) {
//
//			Display.getDefault().asyncExec(new Runnable() {
//				public void run() {
//					dialog = new SelectOperationDialog(getShell(), ops);
//					confirm = dialog.open();
//				}
//			});
//
//			while (true) {
//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					openErrorDialog(e.getMessage());
//					setClose(true);
//				}
//
//				if (confirm == Window.OK) {
//					Operation op = dialog.getSelectedOperation();
//					if (op != null)
//						try {
//							ExecuteOperationCommand.executeOperation(animator,
//									op);
//						} catch (ProBException e) {
//							openErrorDialog(e.getMessage());
//							setClose(true);
//						}
//					confirm = -1;
//					break;
//				} else if (confirm == Window.CANCEL) {
//					setClose(true);
//					confirm = -1;
//					break;
//				}
//
//			}
//
//		} else {
//			Operation op = animation.getCurrentStateOperation(opName);
//			if (op != null)
//				try {
//					ExecuteOperationCommand.executeOperation(animator, op);
//				} catch (ProBException e) {
//					openErrorDialog(e.getMessage());
//					setClose(true);
//				}
//		}
//
//		visualization.setIsRunning(true);
//
//	}

	private void openPerspective() {

		if (simulation != null) {
			BMotionStudio.setCurrentSimulation(simulation);
			BMotionStudio.setCurrentPerspective(PerspectiveUtil
					.openPerspective(file));
			PerspectiveUtil.initViews(simulation);
		}

	}

	// TODO Reimplement me!!!
//	private ILanguageService getGenericLoadMachine(String language) {
//		IExtensionRegistry registry = Platform.getExtensionRegistry();
//		IExtensionPoint extensionPoint = registry
//				.getExtensionPoint("de.bmotionstudio.gef.editor.language");
//		ILanguageService langService = null;
//
//		// Get GenericLoadMachine command for language
//		for (IExtension extension : extensionPoint.getExtensions()) {
//			for (IConfigurationElement configurationElement : extension
//					.getConfigurationElements()) {
//
//				if ("language".equals(configurationElement.getName())) {
//
//					String languageEx = configurationElement.getAttribute("id");
//					if (language.toLowerCase(Locale.ENGLISH).equals(
//							languageEx.toLowerCase(Locale.ENGLISH))) {
//
//						try {
//							langService = (ILanguageService) configurationElement
//									.createExecutableExtension("service");
//						} catch (final CoreException e) {
//							openErrorDialog(e.getMessage());
//							setClose(true);
//						}
//
//					}
//
//				}
//
//			}
//		}
//		return langService;
//	}

	public void kill() {
		if (getShell() != null)
			getShell().dispose();
	}

	public void openErrorDialog(final String msg) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				ErrorDialog.openError(getParent(), "Error",
						"Error creating visualization", new Status(
								IStatus.ERROR, BMotionEditorPlugin.PLUGIN_ID,
								msg));
			}
		});
	}

}