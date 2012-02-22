package de.prob.scripting;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.ProBException;
import de.prob.cli.ProBInstance;
import de.prob.model.IStateSpace;
import de.prob.model.StateSpace;

public class Api {
	// private static final Logger logger = LoggerFactory.getLogger(Api.class);

	private Provider<StateSpace> animationProvider;

	@Inject
	public Api(final Provider<StateSpace> animationFactory) {
		this.animationProvider = animationFactory;
	}

	public void raise() {
		// logger.error("Fataaaaal!");
		// logger.error("Fatal!", new IllegalArgumentException("bawang"));
	}

	public void shutdown(final ProBInstance x) {
		x.shutdown();
	}

	public IStateSpace b_def() {
		File f = null;
		try {

			ClassLoader classLoader = getClass().getClassLoader();
			System.out.println(classLoader);

			URL resource = classLoader.getResource("examples/scheduler.mch");

			System.out.println(resource);

			f = new File(resource.toURI());
			System.out.println(f);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return open_file(f);
	}

	public IStateSpace open_file(final File f) {
		StateSpace stateSpace = animationProvider.get();
		stateSpace.load(f);
		return stateSpace;
	}

	public IStateSpace load_b(final String dir, final String name,
			final String ext) {
		File f = new File(dir + File.separator + name + "." + ext);
		return open_file(f);
	}

	public String getCurrentId(final IStateSpace animation)
			throws ProBException {
		// new ICom<GetCurrentStateIdCommand>(new GetCurrentStateIdCommand())
		// .executeOn(animation);
		return null;
	}

}
