package de.prob.scripting;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.prob.ProBException;
import de.prob.animator.command.GetMachineObjectsCommand;
import de.prob.cli.ProBInstance;
import de.prob.model.ClassicalBModelFactory;
import de.prob.model.IModelFactory;
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

	public StateSpace b_def() {
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
		return b_open_file(f, "scheduler");
	}

	public StateSpace b_open_file(final File f, final String name) {
		final IModelFactory classicalBFactory = new ClassicalBModelFactory();
		StateSpace stateSpace = animationProvider.get();

		try {
			stateSpace.execute(classicalBFactory.getLoadCommand(f, name));
			GetMachineObjectsCommand getInfo = new GetMachineObjectsCommand();
			stateSpace.execute(getInfo);
			stateSpace.setStateTemplate(classicalBFactory.generate(getInfo));
			stateSpace.exploreState("root");
		} catch (ProBException e) {
			e.printStackTrace();
		}
		return stateSpace;
	}

	public StateSpace load_b(final String dir, final String name,
			final String ext) {
		File f = new File(dir + File.separator + name + "." + ext);
		return b_open_file(f, name);
	}

	public String getCurrentId(final StateSpace animation) throws ProBException {
		//		new ICom<GetCurrentStateIdCommand>(new GetCurrentStateIdCommand())
		//				.executeOn(animation);
		return null;
	}

}
