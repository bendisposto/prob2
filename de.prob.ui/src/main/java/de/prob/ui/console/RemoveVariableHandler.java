package de.prob.ui.console;

import groovy.lang.Binding;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.inject.Injector;

import de.prob.ui.BindingTableEntry;
import de.prob.webconsole.GroovyExecution;
import de.prob.webconsole.ServletContextListener;

public class RemoveVariableHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
			return null;

		IStructuredSelection sel = (IStructuredSelection) selection;
		Object x = sel.getFirstElement();
		if (!(x instanceof BindingTableEntry))
			return null;

		BindingTableEntry bte = (BindingTableEntry) x;

		Injector injector = ServletContextListener.INJECTOR;

		GroovyExecution groovyExecution = injector
				.getInstance(GroovyExecution.class);

		Binding bindings = groovyExecution.getBindings();
		bindings.getVariables().remove(bte.name);
		groovyExecution.notifyListerners();
		return null;
	}

}
