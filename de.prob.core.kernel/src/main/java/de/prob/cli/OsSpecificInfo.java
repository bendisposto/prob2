package de.prob.cli;

class OsSpecificInfo {
	final String cliName;
	final String helperCmd;
	final String userInterruptCmd;
	final String name;
	final String fullname;

	public OsSpecificInfo(final String cliName, final String helperCmd,
			final String userInterruptCmd, final String name, String fullname) {
		this.cliName = cliName;
		this.helperCmd = helperCmd;
		this.userInterruptCmd = userInterruptCmd;
		this.name = name;
		this.fullname = fullname;
	}
}