package de.prob.ui.eventb;

import groovy.lang.Binding;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IEventBRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

import com.google.inject.Injector;

import de.prob.animator.command.LoadEventBCommand;
import de.prob.animator.command.StartAnimationCommand;
import de.prob.model.eventb.EventBModel;
import de.prob.rodin.translate.EventBTranslator;
import de.prob.scripting.EventBFactory;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.Trace;
import de.prob.statespace.StateSpace;
import de.prob.ui.eventb.internal.TranslatorFactory;
import de.prob.webconsole.GroovyExecution;
import de.prob.webconsole.ServletContextListener;

public class StartAnimationHandler extends AbstractHandler {

	private ISelection fSelection;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		fSelection = HandlerUtil.getCurrentSelection(event);

		final IEventBRoot rootElement = getRootElement();

		EventBTranslator eventBTranslator = new EventBTranslator(rootElement);

		Injector injector = ServletContextListener.INJECTOR;

		final EventBFactory instance = injector
				.getInstance(EventBFactory.class);

		EventBModel model = instance.load(eventBTranslator.getMainComponent(),
				eventBTranslator.getMachines(), eventBTranslator.getContexts(),
				eventBTranslator.getModelFile());

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

		Trace h = new Trace(s);
		AnimationSelector selector = injector
				.getInstance(AnimationSelector.class);
		selector.addNewAnimation(h);
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

		return null;
	}

	private IEventBRoot getRootElement() {
		IEventBRoot root = null;
		if (fSelection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) fSelection;
			if (ssel.size() == 1) {
				final Object element = ssel.getFirstElement();
				if (element instanceof IEventBRoot) {
					root = (IEventBRoot) element;
				} else if (element instanceof IFile) {
					IRodinFile rodinFile = RodinCore.valueOf((IFile) element);
					if (rodinFile != null) {
						root = (IEventBRoot) rodinFile.getRoot();
					}
				}
			}
		}
		return root;
	}

}
