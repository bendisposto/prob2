package de.prob;

import static java.io.File.separator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import de.prob.animator.AnimatorModule;
import de.prob.annotations.Home;
import de.prob.annotations.Logfile;
import de.prob.annotations.Version;
import de.prob.cli.ModuleCli;
import de.prob.scripting.ScriptingModule;
import de.prob.statespace.ModelModule;
import de.prob.web.WebModule;

/**
 * This Guice {@link AbstractModule} contains all the configuration information
 * necessary to configure ProB 2.0.
 * 
 * @author joy
 * 
 */
public class MainModule extends AbstractModule {

	@Override
	protected final void configure() {
		install(new MainConfiguration());
		install(new ModuleCli());
		install(new AnimatorModule());
		install(new ModelModule());
		install(new WebModule());
		install(new ScriptingModule());
	}
}
