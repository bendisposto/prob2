package de.prob.scripting;

import com.google.inject.Inject;
import com.google.inject.Provider;
import de.be4.classicalb.core.parser.node.Start;
import de.prob.animator.IAnimator;
import de.prob.animator.command.GetVersionCommand;
import de.prob.cli.CliVersionNumber;
import de.prob.cli.ProBInstance;
import de.prob.exception.CliError;
import de.prob.exception.ProBError;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
import de.prob.model.eventb.translate.EventBModelTranslator;
import de.prob.model.representation.CSPModel;
import de.prob.prolog.output.PrologTermOutput;
import de.prob.statespace.StateSpace;
import groovy.lang.Closure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Api {
	private Logger logger = LoggerFactory.getLogger(Api.class);
	private final FactoryProvider modelFactoryProvider;
	private final Provider<IAnimator> animatorProvider;
	/**
	 * This variable specifies whether the variables in the model are
	 * registered by default when loading the model.
	 */
	private Boolean loadVariablesByDefault = true;
	private LinkedHashMap globals = new LinkedHashMap();

	@Override
	public String toString() {
		return "ProB Connector";
	}

	/**
	 * A {@link FactoryProvider} and {@link Provider}{@code <}{@link IAnimator}{@code >} are injected into an api
	 * object at startup
	 *
	 * @param modelFactoryProvider
	 * @param animatorProvider
	 */
	@Inject
	public Api(final FactoryProvider modelFactoryProvider, final Provider<IAnimator> animatorProvider) {
		this.animatorProvider = animatorProvider;
		this.modelFactoryProvider = modelFactoryProvider;
	}

	public Closure getSubscribeClosure(Closure<Object> closure) {
		if (loadVariablesByDefault) {
			return ((Closure) (closure));
		}

		return LoadClosures.getEMPTY();
	}

	/**
	 * Shutdown the specified {@link ProBInstance} object.
	 *
	 * @param x
	 */
	public void shutdown(final ProBInstance x) {
		x.shutdown();
	}

	public StateSpace eventb_load(final String file, final Map<String, String> prefs) throws IOException, ModelTranslationError {
		String fileName = file;
		Closure<Void> loadClosure = getSubscribeClosure(LoadClosures.getEVENTB());
		EventBFactory factory = modelFactoryProvider.getEventBFactory();
		if (fileName.endsWith(".eventb")) {
			return factory.loadModelFromEventBFile(file, prefs);
		}

		ExtractedModel<EventBModel> extracted = factory.extract(fileName);
		StateSpace s = extracted.load(prefs);
		loadClosure.call(s);
		return s;
	}

	public StateSpace eventb_load(final String file) throws IOException, ModelTranslationError{
		return eventb_load(file, Collections.<String, String>emptyMap());
	}

	public void eventb_save(final StateSpace s, final String path) throws IOException{
		EventBModelTranslator translator = new EventBModelTranslator((EventBModel) s.getModel(), s.getMainComponent());

		FileOutputStream fos = new FileOutputStream(path);
		PrologTermOutput pto = new PrologTermOutput(fos, false);

		pto.openTerm("package");
		translator.printProlog(pto);
		pto.closeTerm();
		pto.fullstop();

		pto.flush();
		fos.flush();
		fos.close();
	}

	/**
	 * Loads a {@link StateSpace} from the given B file path.
	 *
	 * @param file
	 * @throws ModelTranslationError
	 * @throws IOException
	 */
	public StateSpace b_load(final String file, final Map<String, String> prefs) throws IOException, ModelTranslationError {
		ClassicalBFactory bFactory = modelFactoryProvider.getClassicalBFactory();
		Closure loadClosure = getSubscribeClosure(LoadClosures.getB());
		ExtractedModel<ClassicalBModel> extracted = bFactory.extract(file);
		StateSpace s = extracted.load(prefs);
		loadClosure.call(s);
		return s;
	}

	/**
	 * Loads a {@link StateSpace} from the given B file path.
	 *
	 * @param file
	 * @throws ModelTranslationError
	 * @throws IOException
	 */
	public StateSpace b_load(final String file) throws IOException, ModelTranslationError {
		return b_load(file, Collections.<String, String>emptyMap());
	}

	public StateSpace b_load(final Start ast, final Map<String, String> prefs) throws IOException, ModelTranslationError {
		ClassicalBFactory bFactory = modelFactoryProvider.getClassicalBFactory();
		Closure loadClosure = getSubscribeClosure(LoadClosures.getB());
		ExtractedModel<ClassicalBModel> extracted = bFactory.create(ast);
		StateSpace s = extracted.load(prefs);
		loadClosure.call(s);
		return s;
	}

	public StateSpace b_load(final Start ast) throws IOException, ModelTranslationError {
		return b_load(ast, Collections.<String, String>emptyMap());
	}

	public StateSpace tla_load(final String file, final Map<String, String> prefs) throws IOException, ModelTranslationError {
		TLAFactory tlaFactory = modelFactoryProvider.getTLAFactory();
		Closure loadClosure = getSubscribeClosure(LoadClosures.getB());
		ExtractedModel<ClassicalBModel> extracted = tlaFactory.extract(file);
		StateSpace s = extracted.load(prefs);
		loadClosure.call(s);
		return s;
	}

	public StateSpace tla_load(final String file) throws IOException, ModelTranslationError {
		return tla_load(file, Collections.<String, String>emptyMap());
	}

	/**
	 * Loads a {@link StateSpace} from the given CSP file. If the user does not have
	 * the cspm parser installed, an Exception is thrown informing the user that
	 * they need to install it.
	 *
	 * @param file
	 */
	public StateSpace csp_load(final String file, final Map<String, String> prefs) throws IOException, ModelTranslationError {
		CSPFactory cspFactory = modelFactoryProvider.getCspFactory();
		StateSpace s = null;
		try {
			ExtractedModel<CSPModel> extracted = cspFactory.extract(file);
			s = extracted.load(prefs);
		} catch (ProBError error) {
			throw new CliError("Could not find CSP Parser. Perform 'installCSPM' to install cspm in your ProB lib directory", error);
		}

		return s;
	}

	/**
	 * Loads a {@link StateSpace} from the given CSP file. If the user does not have
	 * the cspm parser installed, an Exception is thrown informing the user that
	 * they need to install it.
	 *
	 * @param file
	 */
	public StateSpace csp_load(final String file) throws IOException, ModelTranslationError {
		return csp_load(file, Collections.<String, String>emptyMap());
	}

	public CliVersionNumber getVersion() {
		try {
			IAnimator animator = animatorProvider.get();
			GetVersionCommand versionCommand = new GetVersionCommand();
			animator.execute(versionCommand);
			animator.kill();
			return versionCommand.getVersion();
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * @return Returns a String representation of the currently available
	 * commands for the Api object. Intended to ease use in the Groovy
	 * console.
	 */
	public String help() {
		return ("Api Commands: \n\n ClassicalBModel b_load(String PathToFile): load .mch files \n" + " CSPModel csp_load(String PathToFile): load .csp files \n" + " toFile(StateSpace s): save StateSpace\n" + " readFile(): reload saved StateSpace\n" + " shutdown(ProBInstance x): shutdown ProBInstance\n" + " help(): print out available commands");
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Boolean getLoadVariablesByDefault() {
		return loadVariablesByDefault;
	}

	public void setLoadVariablesByDefault(Boolean loadVariablesByDefault) {
		this.loadVariablesByDefault = loadVariablesByDefault;
	}

	public LinkedHashMap getGlobals() {
		return globals;
	}

	public void setGlobals(LinkedHashMap globals) {
		this.globals = globals;
	}


}
