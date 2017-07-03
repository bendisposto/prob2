package de.prob.model;

import org.junit.BeforeClass;

import de.prob.Main;
import de.prob.cli.OsSpecificInfo;
import de.prob.cli.ProBInstanceProvider;
import de.prob.scripting.Installer;

public class AbstratUnitTestRunModelWithCli {
	@BeforeClass
	public static void setup() {
		ProBInstanceProvider provider = Main.getInjector().getInstance(ProBInstanceProvider.class);
		OsSpecificInfo osInfo = provider.getOsInfo();
		new Installer(osInfo).ensureCLIsInstalled();
	}
}
