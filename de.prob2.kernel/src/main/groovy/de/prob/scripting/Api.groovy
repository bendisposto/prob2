package de.prob.scripting;

import java.util.Map.Entry

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.google.inject.Inject

import de.be4.classicalb.core.parser.exceptions.BException
import de.prob.Main
import de.prob.animator.IAnimator
import de.prob.animator.command.GetCurrentPreferencesCommand
import de.prob.animator.command.GetVersionCommand
import de.prob.cli.CliVersionNumber
import de.prob.cli.ProBInstance
import de.prob.exception.ProBError
import de.prob.model.classicalb.ClassicalBModel
import de.prob.model.eventb.Context
import de.prob.model.eventb.EventBModel
import de.prob.model.eventb.translate.EventBModelTranslator
import de.prob.model.representation.AbstractModel
import de.prob.model.representation.CSPModel
import de.prob.model.representation.Constant
import de.prob.model.representation.Invariant
import de.prob.model.representation.Machine
import de.prob.model.representation.Variable
import de.prob.prolog.output.PrologTermOutput


public class Api {
	def static Closure EMPTY = {AbstractModel model -> }
	def static Closure DEFAULT = {AbstractModel model ->
		model.getChildrenOfType(Machine.class).each { Machine machine ->
			machine.getChildrenOfType(Variable.class).each { Variable var ->
				var.subscribe(model.getStateSpace())
			}
			machine.getChildrenOfType(Invariant.class).each { Invariant inv ->
				inv.subscribe(model.getStateSpace())
			}
		}
		model.getChildrenOfType(Context.class).each { Context context ->
			context.getChildrenOfType(Constant.class).each { Constant constant ->
				constant.subscribe(model.getStateSpace())
			}
		}
	}


	Logger logger = LoggerFactory.getLogger(Api.class);

	private final FactoryProvider modelFactoryProvider;
	private final Downloader downloader;

	/**
	 * This variable specifies whether the variables in the model are
	 * registered by default when loading the model.
	 */
	def loadVariablesByDefault = true;

	def globals = [:]

	@Override
	public String toString() {
		return "ProB Connector";
	}

	/**
	 * A {@link FactoryProvider} and {@link Downloader} are injected into an api
	 * object at startup
	 *
	 * @param modelFactoryProvider
	 * @param downloader
	 */
	@Inject
	public Api(final FactoryProvider modelFactoryProvider,
	final Downloader downloader) {
		this.modelFactoryProvider = modelFactoryProvider;
		this.downloader = downloader;
	}

	public Closure getSubscribeClosure() {
		if (loadVariablesByDefault) {
			return DEFAULT
		}
		EMPTY
	}

	/**
	 * Shutdown the specified {@link ProBInstance} object.
	 *
	 * @param x
	 */
	public void shutdown(final ProBInstance x) {
		x.shutdown();
	}

	public EventBModel eventb_load(final String file, final Map<String, String> prefs=Collections.emptyMap(), Closure loadClosure=getSubscribeClosure()) {
		def fileName = file;
		EventBFactory factory = modelFactoryProvider.getEventBFactory();
		if (fileName.endsWith(".eventb")) {
			return factory.loadModelFromEventBFile(file, prefs, EMPTY)
		}
		return factory.load(fileName, prefs, loadClosure);
	}

	public EventBModel eventb_load(final String zipFile, final String componentName, final Map<String, String> prefs=Collections.emptyMap(), Closure loadClosure=getSubscribeClosure()) {
		if (!zipFile.endsWith(".zip")) {
			throw new IllegalArgumentException("$zipFile is not a zip file")
		}
		EventBFactory factory = modelFactoryProvider.getEventBFactory();
		return factory.loadModelFromZip(zipFile, componentName, prefs, loadClosure)
	}

	public void eventb_save(final EventBModel model, final String path) {
		EventBModelTranslator translator = new EventBModelTranslator(model);

		def fos = new FileOutputStream(path);
		PrologTermOutput pto = new PrologTermOutput(fos,false);

		pto.openTerm("package");
		translator.printProlog(pto);
		pto.closeTerm();
		pto.fullstop();

		pto.flush();
		fos.flush();
		fos.close();
	}

	/**
	 * Loads a {@link ClassicalBModel} from the specified file path.
	 *
	 * @param file
	 * @return classicalBModel
	 * @throws BException
	 * @throws IOException
	 */
	public ClassicalBModel b_load(final String file,
			final Map<String, String> prefs=Collections.emptyMap(), Closure loadClosure=getSubscribeClosure()) throws IOException, BException {
		ClassicalBFactory bFactory = modelFactoryProvider
				.getClassicalBFactory();
		return bFactory.load(file, prefs, loadClosure);
	}

	public ClassicalBModel tla_load(final String file,
			final Map<String, String> prefs=Collections.emptyMap(), Closure loadClosure=getSubscribeClosure()) throws IOException, BException {
		TLAFactory tlaFactory = modelFactoryProvider.getTLAFactory();
		return tlaFactory.load(file, prefs, loadClosure);
	}

	/**
	 * Loads a {@link CSPModel} from the given file. If the user does not have
	 * the cspm parser installed, an Exception is thrown informing the user that
	 * they need to install it.
	 *
	 * @param file
	 * @return {@link CSPModel} that has been loaded from file
	 * @throws Exception
	 */
	public CSPModel csp_load(final String file, final Map<String, String> prefs=Collections.emptyMap(), Closure loadClosure=EMPTY)
	throws Exception {
		CSPFactory cspFactory = modelFactoryProvider.getCspFactory();
		CSPModel m = null;
		try {
			m = cspFactory.load(file, prefs, loadClosure);
		} catch (ProBError error) {
			throw new Exception(
			"Could not find CSP Parser. Perform 'installCSPM' to install cspm in your ProB lib directory");
		}
		return m;
	}

	public AbstractModel load(final String filename) throws Exception {
		Properties p = new Properties();

		Map<String, String> prefs = new HashMap<String, String>();

		try {
			p.load(new FileInputStream(filename));

			Set<String> keys = p.stringPropertyNames();
			for (String key : keys) {
				if (key.endsWith(".prolog")) {
					prefs.put(key.substring(0, key.indexOf(".")),
							p.getProperty(key));
				}
			}

			String modelFile = p.getProperty("MODEL_FILE");
			String formalism = p.getProperty("FORMALISM");
			if (formalism.equals("ClassicalBModel")) {
				return b_load(modelFile, prefs);
			}
			if (formalism.equals("CSPModel")) {
				return csp_load(modelFile, prefs);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public void save(final AbstractModel m, final String filename) {
		GetCurrentPreferencesCommand cmd = new GetCurrentPreferencesCommand();

		m.getStateSpace().execute(cmd);
		Map<String, String> prefs = cmd.getPreferences();

		try {
			Properties p = new Properties();

			for (Entry<String, String> pref : prefs.entrySet()) {
				p.setProperty(pref.getKey() + ".prolog", pref.getValue());
			}

			p.setProperty("MODEL_FILE", m.getModelFile().getAbsolutePath());
			p.setProperty("FORMALISM", m.getClass().getSimpleName());

			p.store(new FileOutputStream(filename), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Upgrades the ProB Cli to the given target version
	 *
	 * @param targetVersion
	 * @return String with the version of the upgrade
	 */
	public String upgrade(final String targetVersion) {
		return downloader.downloadCli(targetVersion);
	}

	/**
	 * Lists the versions of ProB Cli that are available for download
	 *
	 * @return String with list of possible versions
	 */
	public String listVersions() {
		return downloader.listVersions();
	}

	/**
	 * Writes an xml representation of the StateSpace to file
	 *
	 * @param s
	 */
	//	public void toFile(final StateSpace s) {
	//		XStream xstream = new XStream(new JettisonMappedXmlDriver());
	//		xstream.omitField(IAnimator.class, "animator");
	//		// xstream.omitField(Trace.class, "trace");
	//		String xml = xstream.toXML(s);
	//		// System.out.println(xml);
	//		try {
	//			FileWriter fw = new FileWriter("statespace.xml");
	//			final BufferedWriter bw = new BufferedWriter(fw);
	//			bw.write(xml);
	//			bw.close();
	//		} catch (IOException e1) {
	//			System.out.println("could not create file");
	//		}
	//	}





	public CliVersionNumber getVersion() {
		try {
			IAnimator animator = Main.getInjector().getInstance(IAnimator.class);
			GetVersionCommand versionCommand = new GetVersionCommand();
			animator.execute(versionCommand);
			animator.cli.shutdown()
			return versionCommand.getVersion();
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Reads the statespace.xml file and returns the StateSpace that it
	 * represents
	 *
	 * @return {@link StateSpace} object specified by saved "statespace.xml"
	 *         file
	 */
	//	public StateSpace readFile() {
	//		FileInputStream fstream;
	//		StringBuffer sb = new StringBuffer();
	//		try {
	//			fstream = new FileInputStream("statespace.xml");
	//
	//			final DataInputStream in = new DataInputStream(fstream);
	//			final BufferedReader br = new BufferedReader(new InputStreamReader(
	//					in));
	//
	//			String tmp;
	//
	//			try {
	//				while ((tmp = br.readLine()) != null) {
	//					sb.append(tmp);
	//				}
	//				in.close();
	//			} catch (IOException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//		} catch (FileNotFoundException e) {
	//		}
	//		XStream xstream = new XStream(new JettisonMappedXmlDriver());
	//		// xstream.omitField(IAnimator.class, "animator");
	//
	//		StateSpace t = (StateSpace) xstream.fromXML(sb.toString());
	//		IAnimator anim = ServletContextListener.INJECTOR
	//				.getInstance(IAnimator.class);
	//		t.setAnimator(anim);
	//		anim.execute(t.getLoadcmd(), new StartAnimationCommand());
	//
	//		return t;
	//	}

	/**
	 * @return Returns a String representation of the currently available
	 *         commands for the Api object. Intended to ease use in the Groovy
	 *         console.
	 */
	public String help() {
		return "Api Commands: \n\n ClassicalBModel b_load(String PathToFile): load .mch files \n"
		+ " CSPModel csp_load(String PathToFile): load .csp files \n"
		+ " upgrade(String version): upgrade ProB cli to specified version\n"
		+ " listVersions(): list currently available ProB cli versions\n"
		+ " toFile(StateSpace s): save StateSpace\n"
		+ " readFile(): reload saved StateSpace\n"
		+ " shutdown(ProBInstance x): shutdown ProBInstance\n"
		+ " help(): print out available commands";
	}


}
