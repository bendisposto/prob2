package de.prob.cli;

/**
 * The OsSpecificInfo takes on the following form based on the present Operating
 * System. Supports Windows, Mac, and Linux.
 * 
 * @author joy
 * 
 */
public class OsSpecificInfo {
	private final String cliName;
	private final String helperCmd;
	private final String userInterruptCmd;
	private final String name;
	private final String fullname;
	private final String dirName;

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

	/**
	 * @return cliName - Windows: "probcli.exe", Mac and Linux: "probcli.sh"
	 */
	public String getCliName() {
		return cliName;
	}

	/**
	 * @return helperCmd - Windows: null, Mac and Linux: "sh"
	 */
	public String getHelperCmd() {
		return helperCmd;
	}

	/**
	 * @return userInterruptCmd - Windows: "send_user_interrupt.exe", Mac and
	 *         Linux: "send_user_interrupt"
	 */
	public String getUserInterruptCmd() {
		return userInterruptCmd;
	}

	/**
	 * @return name Windows: "Windows", Mac: "MacOs", Linux: "Linux"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return {@link System#getProperty(String)} with String key "os.arch"
	 */
	public String getFullname() {
		return fullname;
	}

	/**
	 * @return dirName Windows: "win32", Mac: "leopard64", Linux 32-Bit:
	 *         "linux32", Linux 64-Bit: "linux64"
	 */
	public String getDirName() {
		return dirName;
	}
}