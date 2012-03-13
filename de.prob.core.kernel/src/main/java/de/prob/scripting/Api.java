package de.prob.scripting;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.inject.Inject;

import de.prob.ProBException;
import de.prob.cli.ProBInstance;
import de.prob.model.StateSpace;
import de.prob.model.languages.ClassicalBFactory;
import de.prob.model.languages.ClassicalBMachine;

public class Api {

	private final FactoryProvider modelFactoryProvider;

	@Inject
	public Api(final FactoryProvider modelFactoryProvider) {
		this.modelFactoryProvider = modelFactoryProvider;
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
