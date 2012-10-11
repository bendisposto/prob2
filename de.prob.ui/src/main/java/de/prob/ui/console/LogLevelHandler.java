package de.prob.ui.console;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.commands.ToggleState;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.Main;

public class LogLevelHandler extends AbstractHandler implements IHandler {

	public static class NonPersistingToggleState extends ToggleState implements
			IExecutableExtension {

		@Override
		public void setInitializationData(IConfigurationElement config,
				String propertyName, Object data) throws CoreException {
			setValue(Boolean.FALSE);
		}
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		Command command = event.getCommand();
		boolean value = !HandlerUtil.toggleCommandState(command);
		String level = Main.setDebuggingLogLevel(value);
		GroovyConsole.getInstance().getOutputBrowser()
				.execute("setLogLevel('" + level + "')");
		return null;
	}

}
