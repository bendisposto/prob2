/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.eventb;

import groovy.lang.Binding;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eventb.core.IEventBRoot;

import com.google.inject.Injector;

import de.prob.animator.command.LoadEventBCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.model.eventb.EventBModel;
import de.prob.rodin.translate.EventBTranslator;
import de.prob.scripting.EventBFactory;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.History;
import de.prob.statespace.StateSpace;
import de.prob.ui.Activator;
import de.prob.ui.ProBConfiguration;
import de.prob.ui.ProgressBarDialog;
import de.prob.ui.eventb.internal.TranslatorFactory;
import de.prob.ui.util.PerspectiveUtil;
import de.prob.webconsole.GroovyExecution;
import de.prob.webconsole.ServletContextListener;

public class StartAnimationProgressBar extends ProgressBarDialog {

	private IEventBRoot rootElement;
	
	public StartAnimationProgressBar(Shell parent, IEventBRoot rootElement) {
		super(parent);
		this.rootElement = rootElement;
	}

	@Override
	public void initGuage() {
		this.setExecuteTime(2);
		this.setMayCancel(true);
		this.setProcessMessage("Starting Animation ...");
		this.setShellTitle("Starting Animation");
	}

	@Override
	protected String process(int i) {

		switch (i) {
		case 1:
			startProBAnimator();
			return "Starting ProB Animator";
		case 2:
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					startBMotionStudioVisualization();
				}

			});
			return "Starting BMotion Studio Visualization";
		}

		return "Starting ProB Animaton";

	}

	private void startBMotionStudioVisualization() {

		IFile modelFile = rootElement.getResource();

		IPerspectiveDescriptor currentPerspective = ProBConfiguration
				.getCurrentPerspective();
		IFile currentModelFile = ProBConfiguration.getCurrentModelFile();

		// Close and save old perspective
		if (currentPerspective != null && currentModelFile != null) {
			// If yes ...
			// Export the current perspective
			IFile perspectiveFile = currentModelFile.getProject().getFile(
					PerspectiveUtil.getPerspectiveFileName(currentModelFile));
			PerspectiveUtil.exportPerspective(currentPerspective,
					perspectiveFile);
			// Close and delete current perspective, before opening the new one
			PerspectiveUtil.closePerspective(currentPerspective);
			PerspectiveUtil.deletePerspective(currentPerspective);
		}

		// Open new perspective
		IPerspectiveDescriptor perspective = PerspectiveUtil
				.openPerspective(modelFile);

		// Set some global variables
		ProBConfiguration.setCurrentPerspective(perspective);
		ProBConfiguration.setCurrentModelFile(modelFile);

	}

	private void startProBAnimator() {
	
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

	@Override
	protected void cleanUp() {
	}

	public void kill() {
		if (getShell() != null)
			getShell().dispose();
	}

	public void openErrorDialog(final String msg) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				ErrorDialog.openError(getParent(), "Error",
						"An error occured while starting the animation",
						new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg));
			}
		});
	}

}