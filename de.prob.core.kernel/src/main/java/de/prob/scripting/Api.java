package de.prob.scripting;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import com.google.inject.Inject;

import de.prob.ProBException;
import de.prob.animator.command.notImplemented.EvaluateCommand;
import de.prob.animator.command.notImplemented.GetOperationNamesCommand;
import de.prob.annotations.Home;
import de.prob.cli.ProBInstance;
import de.prob.model.StateSpace;
import de.prob.model.representation.ClassicalBMachine;
import de.prob.model.representation.Operation;

public class Api {

	private final FactoryProvider modelFactoryProvider;
	private final String home;

	@Inject
	public Api(final FactoryProvider modelFactoryProvider,
			@Home final String home) {
		this.modelFactoryProvider = modelFactoryProvider;
		this.home = home;
	}

	// private static final Logger logger = LoggerFactory.getLogger(Api.class);

	public void raise() {
		// logger.error("Fataaaaal!");
		// logger.error("Fatal!", new IllegalArgumentException("bawang"));
	}

	public void shutdown(final ProBInstance x) {
		x.shutdown();
	}

	public ClassicalBMachine b_def() throws ProBException {
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

		return bFactory.load(f);
	}

	public StateSpace s() throws ProBException {
		return b_def().getStatespace();
	}

	public ClassicalBMachine b_load(final String file) throws ProBException {
		File f = new File(file);
		ClassicalBFactory bFactory = modelFactoryProvider
				.getClassicalBFactory();
		return bFactory.load(f);
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

	// public IStateSpace b_def() {
	// File f = null;
	// try {
	//
	// ClassLoader classLoader = getClass().getClassLoader();
	// System.out.println(classLoader);
	//
	// URL resource = classLoader.getResource("examples/scheduler.mch");
	//
	// System.out.println(resource);
	//
	// f = new File(resource.toURI());
	// System.out.println(f);
	// } catch (URISyntaxException e) {
	// e.printStackTrace();
	// }
	// return open_file(f);
	// }
	//
	// public IStateSpace open_file(final File f) {
	// StateSpace stateSpace = animationProvider.get();
	// stateSpace.load(f);
	// return stateSpace;
	// }
	//
	// public IStateSpace load_b(final String dir, final String name,
	// final String ext) {
	// File f = new File(dir + File.separator + name + "." + ext);
	// return open_file(f);
	// }

	public String getCurrentId(final StateSpace animation) throws ProBException {
		// new ICom<GetCurrentStateIdCommand>(new GetCurrentStateIdCommand())
		// .executeOn(animation);
		return null;
	}

}
