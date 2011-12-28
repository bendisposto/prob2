package de.prob.cli;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.prob.cli.ModuleCli.OsName;

@Singleton
class OsInfoProvider implements Provider<OsSpecificInfo> {

	private final OsSpecificInfo osInfo;

	@Inject
	public OsInfoProvider(@OsName String osString) {
		osInfo = whichOs(osString);
	}

	public OsSpecificInfo get() {
		return osInfo;
	}

	private OsSpecificInfo whichOs(String osString) {
		String os = osString.toLowerCase();
		if (os.indexOf("win") >= 0)
			return new OsSpecificInfo("probcli.exe", null,
					"send_user_interrupt.exe", "Windows", osString);
		if (os.indexOf("mac") >= 0)
			return new OsSpecificInfo("probcli.sh", "sh",
					"send_user_interrupt", "MacOs", osString);
		if (os.indexOf("linux") >= 0)
			return new OsSpecificInfo("probcli.sh", "sh",
					"send_user_interrupt", "Linux", osString);
		throw new UnsupportedOperationException("OS not supported");
	}

}
