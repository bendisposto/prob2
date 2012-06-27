package de.prob.scripting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.animator.IAnimator;
import de.prob.cli.ProBInstance;
import de.prob.model.classicalb.ClassicalBFactory;
import de.prob.model.classicalb.ClassicalBModel;
import de.prob.model.eventb.EventBModel;
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

	public ClassicalBModel b_def() throws IOException, BException {
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

		ClassicalBModel machine = bFactory.load(f);
		return machine;
	}

	public StateSpace s() {
		ClassicalBModel b = null;
		try {
			b = b_def();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BException e) {
			e.printStackTrace();
		}
		return (b != null) ? b.getStatespace() : null;
	}

	/**
	 * Takes path of a Classical B Machine and loads it into the ClassicalBModel
	 * 
	 * @param file
	 * @return classicalBModel
	 * @throws BException
	 * @throws IOException
	 */
	public ClassicalBModel b_load(final String file) throws IOException,
			BException {
		File f = new File(file);
		ClassicalBFactory bFactory = modelFactoryProvider
				.getClassicalBFactory();
		return bFactory.load(f);
	}

	public EventBModel eb_load(final String filename) throws IOException,
			BException {
		EventBFactory bFactory = modelFactoryProvider.getEventBFactory();
		return bFactory.load(new File(filename));
	}

	public String getCurrentId(final StateSpace animation) {
		// new ICom<GetCurrentStateIdCommand>(new GetCurrentStateIdCommand())
		// .executeOn(animation);
		return null;
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

	public void toFile(final StateSpace s) {
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
		xstream.omitField(IAnimator.class, "animator");
		// xstream.omitField(History.class, "history");
		String xml = xstream.toXML(s);
		// System.out.println(xml);
		try {
			FileWriter fw = new FileWriter("statespace.xml");
			final BufferedWriter bw = new BufferedWriter(fw);
			bw.write(xml);
			bw.close();
		} catch (IOException e1) {
			System.out.println("could not create file");
		}
	}

	public StateSpace readFile() {
		FileInputStream fstream;
		StringBuffer sb = new StringBuffer();
		try {
			fstream = new FileInputStream("statespace.xml");

			final DataInputStream in = new DataInputStream(fstream);
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					in));

			String tmp;

			try {
				while ((tmp = br.readLine()) != null) {
					sb.append(tmp);
				}
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
		}
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
		// xstream.omitField(IAnimator.class, "animator");

		StateSpace t = (StateSpace) xstream.fromXML(sb.toString());

		return t;
	}
}
