package de.prob.scripting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.be4.classicalb.core.parser.ParsingBehaviour;
import de.be4.classicalb.core.parser.exceptions.BCompoundException;
import de.be4.classicalb.core.parser.node.Start;
import de.be4.classicalb.core.parser.rules.RulesProject;

import de.prob.animator.IAnimator;
import de.prob.animator.command.GetVersionCommand;
import de.prob.cli.CliVersionNumber;
import de.prob.cli.ProBInstance;
import de.prob.exception.CliError;
import de.prob.exception.ProBError;
import de.prob.model.brules.RulesModelFactory;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.translate.EventBModelTranslator;
import de.prob.prolog.output.PrologTermOutput;
import de.prob.statespace.StateSpace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Api {
	private static final Logger LOGGER = LoggerFactory.getLogger(Api.class);
	
	private final FactoryProvider modelFactoryProvider;
	private final Provider<IAnimator> animatorProvider;
	private LinkedHashMap<Object, Object> globals = new LinkedHashMap<>();

	/**
	 * A {@link FactoryProvider} and
	 * {@link Provider}{@code <}{@link IAnimator}{@code >} are injected into an
	 * api object at startup
	 *
	 * @param modelFactoryProvider
	 * @param animatorProvider
	 */
	@Inject
	public Api(final FactoryProvider modelFactoryProvider, final Provider<IAnimator> animatorProvider) {
		this.animatorProvider = animatorProvider;
		this.modelFactoryProvider = modelFactoryProvider;
	}

	public LinkedHashMap<Object, Object> getGlobals() {
		return globals;
	}

	public void setGlobals(LinkedHashMap<Object, Object> globals) {
		this.globals = globals;
	}

	@Override
	public String toString() {
		return "ProB Connector";
	}

	public CliVersionNumber getVersion() {
		IAnimator animator = null;
		try {
			animator = animatorProvider.get();
			GetVersionCommand versionCommand = new GetVersionCommand();
			animator.execute(versionCommand);
			return versionCommand.getVersion();
		} catch (RuntimeException e) {
			LOGGER.warn("Exception while getting CLI version info", e);
			return null;
		} finally {
			if (animator != null) {
				animator.kill();
			}
		}
	}

	/**
	 * @return Returns a String representation of the currently available
	 *         commands for the Api object. Intended to ease use in the Groovy
	 *         console.
	 */
	public String help() {
		return "Api Commands:\n\n"
			+ " String help(): print out available commands\n"
			+ " CliVersionNumber getVersion(): get the version of the ProB CLI\n"
			+ " void shutdown(ProBInstance instance): shutdown ProBInstance\n"
			+ " StateSpace b_load(String filePath, [Map<String, String> prefs]): load a classical B machine from a .mch file\n"
			+ " StateSpace b_load(Start ast, [Map<String, String> prefs]): load aclassical B machine from an AST\n"
			+ " StateSpace eventb_load(String filePath, [Map<String, String> prefs]): load an EventB machine from a file\n"
			+ " void eventb_save(StateSpace stateSpace, String filePath): save an EventB state space to a file\n"
			+ " StateSpace tla_load(String filePath, [Map<String, String> prefs]): load a .tla file\n"
			+ " StateSpace brules_load(String filePath, [Map<String, String> prefs]): load a B rules machine from a .rmch file\n"
			+ " StateSpace csp_load(String filePath, [Map<String, String> prefs]): load a .csp file\n"
		;
	}

	/**
	 * Shutdown the specified {@link ProBInstance} object.
	 *
	 * @param x
	 */
	public void shutdown(final ProBInstance x) {
		x.shutdown();
	}

	/**
	 * Loads a {@link StateSpace} from the given B file path.
	 *
	 * @param file
	 * @throws ModelTranslationError
	 * @throws IOException
	 */
	public StateSpace b_load(final String file, final Map<String, String> prefs) throws IOException, ModelTranslationError {
		final ClassicalBFactory bFactory = modelFactoryProvider.getClassicalBFactory();
		return bFactory.extract(file).load(prefs);
	}

	/**
	 * Loads a {@link StateSpace} from the given B file path.
	 *
	 * @param file
	 * @throws ModelTranslationError
	 * @throws IOException
	 */
	public StateSpace b_load(final String file) throws IOException, ModelTranslationError {
		return b_load(file, Collections.emptyMap());
	}

	public StateSpace b_load(final Start ast, final Map<String, String> prefs) throws IOException, ModelTranslationError {
		final ClassicalBFactory bFactory = modelFactoryProvider.getClassicalBFactory();
		return bFactory.create(ast).load(prefs);
	}

	public StateSpace b_load(final Start ast) throws IOException, ModelTranslationError {
		return b_load(ast, Collections.emptyMap());
	}

	public StateSpace eventb_load(final String file, final Map<String, String> prefs) throws IOException, ModelTranslationError {
		final EventBFactory factory = modelFactoryProvider.getEventBFactory();
		if (file.endsWith(".eventb")) {
			return factory.loadModelFromEventBFile(file, prefs);
		} else {
			return factory.extract(file).load(prefs);
		}
	}

	public StateSpace eventb_load(final String file) throws IOException, ModelTranslationError {
		return eventb_load(file, Collections.emptyMap());
	}

	public void eventb_save(final StateSpace s, final String path) throws IOException, ModelTranslationError {
		final EventBModelTranslator translator = new EventBModelTranslator((EventBModel) s.getModel(), s.getMainComponent());

		try (final FileOutputStream fos = new FileOutputStream(path)) {
			final PrologTermOutput pto = new PrologTermOutput(fos, false);
			pto.openTerm("package");
			translator.printProlog(pto);
			pto.closeTerm();
			pto.fullstop();
		}
	}

	public StateSpace tla_load(final String file, final Map<String, String> prefs) throws IOException, ModelTranslationError {
		final TLAFactory tlaFactory = modelFactoryProvider.getTLAFactory();
		return tlaFactory.extract(file).load(prefs);
	}

	public StateSpace tla_load(final String file) throws IOException, ModelTranslationError {
		return tla_load(file, Collections.emptyMap());
	}

	public StateSpace brules_load(final String file, final Map<String, String> prefs) throws IOException {
		final RulesModelFactory bRulesFactory = modelFactoryProvider.getBRulesFactory();
		final RulesProject rulesProject = new RulesProject();
		final ParsingBehaviour parsingBehaviour = new ParsingBehaviour();
		parsingBehaviour.setAddLineNumbers(true);
		rulesProject.setParsingBehaviour(parsingBehaviour);
		rulesProject.parseProject(new File(file));
		rulesProject.checkAndTranslateProject();
		if (rulesProject.hasErrors()) {
			throw new ProBError(new BCompoundException(rulesProject.getBExceptionList()));
		}

		return bRulesFactory.extract(new File(file), rulesProject).load(prefs);
	}

	public StateSpace brules_load(final String file) throws IOException {
		return brules_load(file, Collections.emptyMap());
	}

	/**
	 * Loads a {@link StateSpace} from the given CSP file. If the user does not
	 * have the cspm parser installed, an Exception is thrown informing the user
	 * that they need to install it.
	 *
	 * @param file
	 */
	public StateSpace csp_load(final String file, final Map<String, String> prefs) throws IOException, ModelTranslationError {
		final CSPFactory cspFactory = modelFactoryProvider.getCspFactory();
		try {
			return cspFactory.extract(file).load(prefs);
		} catch (ProBError error) {
			throw new CliError(
				"Could not find CSP Parser. Perform 'installCSPM' to install cspm in your ProB lib directory",
				error
			);
		}
	}

	/**
	 * Loads a {@link StateSpace} from the given CSP file. If the user does not
	 * have the cspm parser installed, an Exception is thrown informing the user
	 * that they need to install it.
	 *
	 * @param file
	 */
	public StateSpace csp_load(final String file) throws IOException, ModelTranslationError {
		return csp_load(file, Collections.emptyMap());
	}
}
