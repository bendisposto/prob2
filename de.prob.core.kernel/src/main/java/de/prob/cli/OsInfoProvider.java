package de.prob.cli;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.prob.cli.ModuleCli.OsArch;
import de.prob.cli.ModuleCli.OsName;

@Singleton
class OsInfoProvider implements Provider<OsSpecificInfo> {

	private final OsSpecificInfo osInfo;

	@Inject
	public OsInfoProvider(@OsName final String osString,
			@OsArch final String osArch) {
		osInfo = whichOs(osString, osArch);
	}

	@Override
	public OsSpecificInfo get() {
		return osInfo;
	}

	private OsSpecificInfo whichOs(final String osString, final String osArch) {
		String os = osString.toLowerCase();
		if (os.indexOf("win") >= 0)
			return new OsSpecificInfo("probcli.exe", null,
					"send_user_interrupt.exe", "Windows", osString, "win32");
		if (os.indexOf("mac") >= 0)
			return new OsSpecificInfo("probcli.sh", "sh",
					"send_user_interrupt", "MacOs", osString, "leopard");
		if (os.indexOf("linux") >= 0) {
			if (osArch.equals("i386")) {
				return new OsSpecificInfo("probcli.sh", "sh",
						"send_user_interrupt", "Linux", osString, "linux");
			}
			if (osArch.equals("amd64")) {
				return new OsSpecificInfo("probcli.sh", "sh",
						"send_user_interrupt", "Linux", osString, "linux64");
			}
		}
		throw new UnsupportedOperationException("OS not supported");
	}
}
