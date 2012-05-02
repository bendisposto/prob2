package de.prob.scripting;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.prob.ProBException;
import de.prob.animator.command.notImplemented.EvaluateCommand;
import de.prob.animator.command.notImplemented.GetOperationNamesCommand;
import de.prob.cli.ProBInstance;
import de.prob.model.classicalb.ClassicalBFactory;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.representation.Operation;
import de.prob.statespace.StateSpace;

public class Api {

	Logger logger = LoggerFactory.getLogger(Api.class);

	private final FactoryProvider modelFactoryProvider;
	private final Downloader downloader;

	@Inject
	public Api(final FactoryProvider modelFactoryProvider,
			final Downloader downloader) {
		this.modelFactoryProvider = modelFactoryProvider;
		this.downloader = downloader;
	}

	public void raise() {
		// logger.error("Fataaaaal!");
		// logger.error("Fatal!", new IllegalArgumentException("bawang"));
	}

	public void shutdown(final ProBInstance x) {
		x.shutdown();
	}

	public ClassicalBModel b_def() {
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource("examples/scheduler.mch");
		File f = null;
		try {
			f = new File(resource.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		ClassicalBFactory bFactory = modelFactoryProvider
				.getClassicalBFactory();

		try {
			ClassicalBModel machine = bFactory.load(f);
			return machine;
		} catch (ProBException e) {
			return null;
		}
	}

	public StateSpace s() throws ProBException {
		final ClassicalBModel b = b_def();
		return (b != null) ? b.getStatespace() : null;
	}

	/**
	 * Takes path of a Classical B Machine and loads it into the ClassicalBModel
	 * 
	 * @param file
	 * @return
	 */
	public ClassicalBModel b_load(final String file) {
		File f = new File(file);
		ClassicalBFactory bFactory = modelFactoryProvider
				.getClassicalBFactory();
		try {
			return bFactory.load(f);
		} catch (ProBException e) {
			return null;
		}
	}

	public List<Operation> testX(final StateSpace s) throws ProBException {
		GetOperationNamesCommand command = new GetOperationNamesCommand();
		s.execute(command);
		return command.getOperations();
	}

	public String eval(final String text, final StateSpace s)
			throws ProBException {
		EvaluateCommand command = new EvaluateCommand(text);
		s.execute(command);
		return command.getResult();
	}

	public String getCurrentId(final StateSpace animation) throws ProBException {
		// new ICom<GetCurrentStateIdCommand>(new GetCurrentStateIdCommand())
		// .executeOn(animation);
		return null;
	}

	/**
	 * Upgrades the ProB Cli to the given target version
	 * 
	 * @param targetVersion
	 * @return
	 */
	public String upgrade(final String targetVersion) {
		try {
			return downloader.downloadCli(targetVersion);
		} catch (ProBException e) {
			logger.error(
					"Could not download files for the given operating system",
					e);
		}
		return "--Upgrade Failed--";
	}

	/**
	 * Lists the versions of ProB Cli that are available for download
	 * 
	 * @return
	 */
	public String listVersions() {
		return downloader.listVersions();
	}
}
