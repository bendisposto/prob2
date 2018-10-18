package de.prob.cli;

import java.io.File;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.prob.cli.ModuleCli.OsArch;
import de.prob.cli.ModuleCli.OsName;

/**
 * Creates {@link OsSpecificInfo} for each instance of the ProB 2.0 software.
 * This is determined from the System settings. The resulting
 * {@link OsSpecificInfo} can be injected into any desired class.
 * 
 * @author joy
 * 
 */
@Singleton
public class OsInfoProvider implements Provider<OsSpecificInfo> {

	private final OsSpecificInfo osInfo;

	@Inject
	public OsInfoProvider(@OsName final String osString, @OsArch final String osArch) {
		osInfo = whichOs(osString, osArch);
	}

	@Override
	public OsSpecificInfo get() {
		return osInfo;
	}

	private OsSpecificInfo whichOs(final String osString, final String osArch) {
		String os = osString.toLowerCase();
		if (os.contains("win")) {
			if ("amd64".equals(osArch)) {
				return new OsSpecificInfo("probcli.exe", null, "lib" + File.separator + "send_user_interrupt.exe",
						"Windows", "win64");
			} else {
				return new OsSpecificInfo("probcli.exe", null, "lib" + File.separator + "send_user_interrupt.exe",
						"Windows", "win32");
			}
		}
		if (os.contains("mac")) {
			return new OsSpecificInfo("probcli.sh", "sh", "send_user_interrupt", "MacOs", "leopard64");
		}
		if (os.contains("linux")) {
			if ("i386".equals(osArch)) {
				return new OsSpecificInfo("probcli.sh", "sh", "send_user_interrupt", "Linux", "linux32");
			}
			if ("amd64".equals(osArch)) {
				return new OsSpecificInfo("probcli.sh", "sh", "send_user_interrupt", "Linux", "linux64");
			}
		}
		throw new UnsupportedOperationException("OS not supported");
	}
}
