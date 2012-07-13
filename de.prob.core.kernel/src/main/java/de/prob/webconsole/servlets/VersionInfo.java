package de.prob.webconsole.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.prob.animator.command.GetVersionCommand;
import de.prob.statespace.StateSpace;

@Singleton
public class VersionInfo {

	private static String version = "";

	@SuppressWarnings("static-access")
	@Inject
	public VersionInfo(StateSpace instance) {
		GetVersionCommand command = new GetVersionCommand();
		instance.execute(command);
		this.version = "probcli "
				+ command.getVersionString().replaceAll("\n", "<br />");
	}

	public static String getVersion() {
		return version;
	}

}
