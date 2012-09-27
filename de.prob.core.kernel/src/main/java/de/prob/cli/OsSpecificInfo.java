package de.prob.cli;

public class OsSpecificInfo {
	final String cliName;
	final String helperCmd;
	final String userInterruptCmd;
	final String name;
	final String fullname;
	final String dirName;

	public OsSpecificInfo(final String cliName, final String helperCmd,
			final String userInterruptCmd, final String name,
			final String fullname, final String dirName) {
		this.cliName = cliName;
		this.helperCmd = helperCmd;
		this.userInterruptCmd = userInterruptCmd;
		this.name = name;
		this.fullname = fullname;
		this.dirName = dirName;
	}
}